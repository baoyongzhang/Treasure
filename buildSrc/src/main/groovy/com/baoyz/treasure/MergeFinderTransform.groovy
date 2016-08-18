/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.baoyz.treasure

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.Sets
import javassist.ClassPath
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.CtNewMethod
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry;

/**
 * Created by baoyongzhang on 16/8/17.
 */
public class MergeFinderTransform extends Transform {

    static String FINDER_NAME = "com/baoyz/treasure/PreferencesFinder.class"
    static String FINDER_CLASS_NAME = "com.baoyz.treasure.PreferencesFinder"

    private Project mProject

    MergeFinderTransform(Project project) {
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

        // 合并 PreferencesFinder
        transformInvocation.inputs.each { input ->

            // 先从依赖中找出 PreferencesFinder
            input.jarInputs.each { jarInput ->
                String destName = jarInput.name;
                /**
                 * 重名名输出文件,因为可能同名,会覆盖
                 */
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath);
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4);
                }
                /**
                 * 获得输出文件
                 */
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR);

                // 检测有没有 PreferencesFinder

                def jarFile = new JarFile(jarInput.file)
                def entry = findEntry(jarFile, FINDER_NAME)
                if (entry) {
                    def finderFile = new File(jarInput.file.parentFile, entry.name)
                    finderFile.parentFile.mkdirs()
                    IOUtils.copy(jarFile.getInputStream(entry), new FileOutputStream(finderFile))
                    finderClassPaths.add(jarInput.file.parentFile)
                    deleteEntry(jarInput.file, jarFile, entry)
                }

                FileUtils.copyFile(jarInput.file, dest);
                mProject.logger.error "Copying ${jarInput.file.absolutePath} to ${dest.absolutePath}"
            }

            input.directoryInputs.each { directoryInput ->

                def dir = directoryInput.file
                if (finderClassPaths.size() > 0) {
                    // 查找当前 module 中 PreferencesFinder
                    File finderFile = findClassFile(dir, FINDER_NAME)
                    if (finderFile) {
                        finderClassPaths.add(dir)
                        println(".......................Finder File: ${finderFile.absolutePath}")
                        // 合并所有 PreferencesFinder
                        def methods = new ArrayList<CtMethod>()
                        def classPool = new ClassPool(true)
                        finderClassPaths.each { file ->
                            def classPath = classPool.insertClassPath(file.absolutePath)
                            def finderClass = classPool.get(FINDER_CLASS_NAME)
                            def method = finderClass.getDeclaredMethod("get")
                            if (method) {
                                methods.add(method)
                            }
                            classPool.removeClassPath(classPath)
                        }
                        if (methods.size() > 0){
                            def tmp = methods[0]

                            classPool = ClassPool.default
                            classPool.insertClassPath(dir.absolutePath)
                            def clazz = classPool.get(FINDER_CLASS_NAME)
                            def getMethod = CtNewMethod.copy(tmp, tmp.name, clazz, null)
                            clazz.addMethod(getMethod)
//                            methods.each { method ->
//                                println(".........add method ${method.name}")
//                                clazz.addMethod(CtNewMethod.copy(method, clazz, null))
//                            }

                            def body = new StringBuilder()
                            body.append("Object result = null;")
                            methods.eachWithIndex { method, index ->
                                def newName = "get\$\$${index}"
                                method.setName(newName)
                                clazz.addMethod(CtNewMethod.copy(method, clazz, null))
                                println(".........newName ${method.name}")
                                body.append("result = ${method.name}(context, clazz, id, factory);")
                                body.append("if (result != null) return result;")
                            }
                            body.append("return null;")

                            clazz.writeFile()
                            /*

                             Object result = null;

                             result = get$$1(context, clazz, id, factory);
                             if (result != null) return result;

                             return null;

                            * */
//                            public static Object get(Context context, Class clazz, String id, Converter.Factory factory) {
                        }
                    }
                }

                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY);
                mProject.logger.error "Copying ${dir.absolutePath} to ${dest.absolutePath}"
                /**
                 * 处理完后拷到目标文件
                 */
                FileUtils.copyDirectory(dir, dest);
            }
        }
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
