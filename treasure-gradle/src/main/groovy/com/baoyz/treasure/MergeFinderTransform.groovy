/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.baoyz.treasure

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.Sets
import javassist.ClassPool
import javassist.CtMethod
import javassist.CtNewMethod
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Created by baoyongzhang on 16/8/17.
 */
public class MergeFinderTransform extends Transform {

    static String FINDER_NAME = "com/baoyz/treasure/PreferencesFinder.class"
    static String FINDER_CLASS_NAME = "com.baoyz.treasure.PreferencesFinder"

    private ProjectWrapper mProject

    MergeFinderTransform(ProjectWrapper project) {
        mProject = project
    }

    @Override
    String getName() {
        return "mergePreferencesFinder"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.ContentType> getOutputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return Sets.immutableEnumSet(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.SUB_PROJECTS);
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        def outputProvider = transformInvocation.outputProvider

        def finderClassPaths = new ArrayList<File>()
        def dependencyClassPaths = new ArrayList<String>()

        // 合并 PreferencesFinder
        transformInvocation.inputs.each { input ->

            // 先从依赖中找出 PreferencesFinder
            input.jarInputs.each { jarInput ->
                String destName = jarInput.name;
                def md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath);
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4);
                }
                File dest = outputProvider.getContentLocation("${destName}_${md5Name}", jarInput.contentTypes, jarInput.scopes, Format.JAR);

                // 检测有没有 PreferencesFinder
                def jarFile = new JarFile(jarInput.file)
                def entry = findEntry(jarFile, FINDER_NAME)
                if (entry) {
                    def finderFile = new File(jarInput.file.parentFile, entry.name)
                    finderFile.parentFile.mkdirs()
                    IOUtils.copy(jarFile.getInputStream(entry), new FileOutputStream(finderFile))
                    finderClassPaths.add(jarInput.file.parentFile)
                    deleteEntry(jarInput.file, jarFile, entry)
                } else {
                    // 没有找到 PreferencesFinder，是一个没有使用 Treasure 的依赖，判断是否是 treasure 包。
                    dependencyClassPaths.add(jarInput.file.absolutePath)
                }

                FileUtils.copyFile(jarInput.file, dest);
            }

            input.directoryInputs.each { directoryInput ->

                def dir = directoryInput.file
                if (finderClassPaths.size() > 0) {
                    // 查找当前 module 中 PreferencesFinder
                    File finderFile = findClassFile(dir, FINDER_NAME)
                    if (finderFile) {
                        finderClassPaths.add(dir)
                        def methods = new ArrayList<CtMethod>()
                        // 找到所有的 get 方法
                        finderClassPaths.each { file ->
                            // ClassPool 会有缓存，所以每次都 new 一个，防止从缓存获取
                            def classPool = new ClassPool(true)
                            def classPath = classPool.insertClassPath(file.absolutePath)
                            def finderClass = classPool.get(FINDER_CLASS_NAME)
                            def method = finderClass.getDeclaredMethod("get")
                            if (method) {
                                methods.add(method)
                            }
                            classPool.removeClassPath(classPath)
                        }

                        if (methods.size() > 0) {
                            try {
                                def tmp = methods[0]

                                def classPool = new ClassPool(true)
                                classPool.insertClassPath(dir.absolutePath)
                                // 添加 android.jar
                                classPool.insertClassPath(getAndroidClassPath())
                                // TODO 添加依赖 Class Path，这里主要是为了依赖 treasure 包，但是依赖很多不好寻找，暂时把所有都加上
                                dependencyClassPaths.each { path ->
                                    classPool.insertClassPath(path)
                                }
                                def clazz = classPool.get(FINDER_CLASS_NAME)
                                def getMethod = CtNewMethod.copy(tmp, tmp.name, clazz, null)
                                // 删除原来的 get 方法
                                clazz.removeMethod(clazz.getDeclaredMethod("get"))

                                // 合并所有 PreferencesFinder
                                def body = new StringBuilder()
                                body.append("{Object result = null;\n")
                                methods.eachWithIndex { method, index ->
                                    def newName = "get\$\$${index}"
                                    method.setName(newName)
                                    clazz.addMethod(CtNewMethod.copy(method, clazz, null))
                                    body.append("result = ${method.name}(\$1, \$2, \$3, \$4);\n")
                                    body.append("if (result != null) return result;\n")

                                }
                                body.append("return null;\n")
                                body.append("}\n")

                                getMethod.setBody(body.toString())

                                // 添加新的 get 方法
                                clazz.addMethod(getMethod)
                                // 把修改后的 Class 写入文件
                                clazz.writeFile(dir.absolutePath)
                            } catch (Exception e) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY);
                FileUtils.copyDirectory(dir, dest);
            }
        }
    }

    String getAndroidClassPath() {
        Properties properties = new Properties()
        properties.load(mProject.rootProject.file('local.properties').newDataInputStream())
        def sdkDir = properties.getProperty('sdk.dir')
        if (sdkDir == null || sdkDir.isEmpty()) {
            sdkDir = System.getenv("ANDROID_HOME")
        }
        return "${sdkDir}/platforms/${mProject.android.compileSdkVersion}/android.jar"
    }

    File findClassFile(File file, String className) {
        def paths = className.split("/")
        paths.each { path ->
            file = findFile(file, path)
        }
        return file
    }

    File findFile(File dir, String name) {
        def result;
        if (dir && dir.exists()) {
            dir.listFiles().each { file ->
                if (name.equals(file.name)) {
                    result = file
                }
            }
        }
        return result
    }

    JarEntry findEntry(JarFile jarFile, String name) {
        def entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            def jarEntry = entries.nextElement()
            if (name.equals(jarEntry.name)) {
                return jarEntry;
            }
        }
    }

    void deleteEntry(File file, JarFile jarFile, JarEntry entry) {
        def tmpJar = new File(file.parentFile, file.name + ".tmp")
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpJar))

        def entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            def jarEntry = entries.nextElement()
            if (entry.name.equals(jarEntry.name)) {
                continue
            }
            String entryName = jarEntry.name
            ZipEntry zipEntry = new ZipEntry(entryName)
            InputStream inputStream = jarFile.getInputStream(jarEntry)
            jarOutputStream.putNextEntry(zipEntry)
            jarOutputStream.write(IOUtils.toByteArray(inputStream))
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        jarFile.close()

        if (file.exists()) {
            file.delete()
        }
        tmpJar.renameTo(file)
    }

}
