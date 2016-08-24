/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.baoyz.treasure.compiler;

/**
 * Created by baoyongzhang on 16/8/24.
 */
public class NotSupportTypeException extends IllegalArgumentException {

    public NotSupportTypeException(String s) {
        super(s);
    }
}
