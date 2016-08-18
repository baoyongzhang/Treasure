/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.baoyz.treasure

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project;

/**
 * Created by baoyongzhang on 16/8/18.
 */
public abstract class BasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        if (project.getPlugins().hasPlugin(AppPlugin)) {
            onAppApply(project)
        } else if (project.getPlugins().hasPlugin(LibraryPlugin)) {
            onLibraryApply(project)
        }
    }

    void onAppApply(Project project) {

    }

    void onLibraryApply(Project project) {

    }

}
