/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 baoyongzhang <baoyz94@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.baoyz.treasure.compiler;

import java.util.HashMap;

import javax.lang.model.type.TypeMirror;

/**
 * Created by baoyz on 15/11/11.
 */
public class TypeMethods {

    public static final String INT = "Int";
    public static final String LONG = "Long";
    public static final String FLOAT = "Float";
    public static final String BOOLEAN = "Boolean";
    public static final String STRING = "String";
    public static final String STRINGSET = "StringSet";
    public static final String ALL = "All";

    /**
     * mPreferences.getAll();
     * mPreferences.getBoolean();
     * mPreferences.getFloat();
     * mPreferences.getInt();
     * mPreferences.getLong();
     * mPreferences.getString();
     * mPreferences.getStringSet();
     */
    private static final HashMap<String, String> METHOD_MAP = new HashMap<String, String>() {
        {
            put("int", INT);
            put("long", LONG);
            put("float", FLOAT);
            put("boolean", BOOLEAN);
            put("java.lang.String", STRING);
            put("java.util.Set<java.lang.String>", STRINGSET);
            put("java.util.Map<String, ?>", ALL);
        }
    };

    public static String typeName(TypeMirror type) {
        return METHOD_MAP.get(type.toString());
    }

    public static String getterMethod(TypeMirror type) {
        System.out.println(type.toString());
        String method = METHOD_MAP.get(type.toString());
        if (method != null) {
            return "get" + method;
        }
        throw new RuntimeException("getter not found");
    }

    public static String setterMethod(TypeMirror type) {
        String method = METHOD_MAP.get(type.toString());
        if (method != null) {
            return "put" + method;
        }
        throw new RuntimeException("setter not found");
    }
}
