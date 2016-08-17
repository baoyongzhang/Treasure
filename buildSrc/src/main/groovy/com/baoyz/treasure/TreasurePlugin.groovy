/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.baoyz.treasure

import com.android.build.api.transform.Context
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by baoyongzhang on 16/8/17.
 */
public class TreasurePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        if (project.getPlugins().hasPlugin(AppPlugin)) {
            project.android.registerTransform(new MergeFinderTransform())
            println("...........registerTransform")

            project.afterEvaluate {
            }

        }
    }
}
