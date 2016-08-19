/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.baoyz.treasure
/**
 * Created by baoyongzhang on 16/8/17.
 */
public class TreasurePlugin extends BasePlugin {

    @Override
    void onAppApply(ProjectWrapper project) {
        super.onAppApply(project)

        project.android.registerTransform(new MergeFinderTransform(project))
        println("...........registerTransform")

        project.afterEvaluate {
        }
    }

}
