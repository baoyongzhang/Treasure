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
package com.baoyz.treasure.compiler.conveter;

import com.baoyz.treasure.compiler.TypeMethods;

import javax.lang.model.type.TypeMirror;

/**
 * Created by baoyz on 15/11/11.
 */
public class SimpleValueConverter implements ValueConverter {

    @Override
    public String convert(TypeMirror type, String[] value) {
        boolean isDefault = false;
        if (value == null || value.length < 1) {
            // not have value, return default value.
            isDefault = true;
        }
        switch (TypeMethods.typeName(type)) {
            case TypeMethods.INT:
                return isDefault ? "0" : value[0];
            case TypeMethods.FLOAT:
                return isDefault ? "0f" : value[0];
            case TypeMethods.LONG:
                return isDefault ? "0l" : value[0];
            case TypeMethods.BOOLEAN:
                return isDefault ? "false" : value[0];
            case TypeMethods.STRING:
                return isDefault ? null : "\"" + value[0] + "\"";
            case TypeMethods.STRINGSET:
                if (isDefault)
                    return null;
                StringBuilder builder = new StringBuilder();
                builder.append("new java.util.HashSet<String>(java.util.Arrays.asList(new java.lang.String[]{");
                for (int i = 0; i < value.length; i++) {
                    builder.append("\"").append(value[i]).append("\"");
                    if (i != (value.length - 1)) {
                        builder.append(",");
                    }
                }
                builder.append("}))");
                return builder.toString();
        }
        return null;
    }
}
