/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.baoyz.treasure

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.Sets

import java.util.jar.JarEntry;

/**
 * Created by baoyongzhang on 16/8/17.
 */
public class MergeFinderTransform extends Transform {

    MergeFinderTransform() {
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

        transformInvocation.inputs.each { input ->

            input.directoryInputs.each { directoryInput ->

                directoryInput.file

                println("...............directoryInput: ${directoryInput}")
            }

            input.jarInputs.each { jarInput ->
                println("...............jarInput: ${jarInput}")

            }
        }
    }
}