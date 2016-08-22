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

import com.baoyz.treasure.Converter;
import com.baoyz.treasure.Treasure;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by baoyz on 15/11/15.
 */
public class FinderGenerator implements Generator {

    private Filer mFiler;
    private Set<? extends Element> mSet;

    public FinderGenerator(Filer filer, Set<? extends Element> set) {
        mFiler = filer;
        mSet = set;
    }

    @Override
    public void generate() {
        TypeSpec.Builder builder = TypeSpec.classBuilder("PreferencesFinder")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Generated code from Treasure. Do not modify!");

        /**
         if (name.equals($S)) {
             if (id == null) {
                return new $T(context);
             } else {
                return new $T(context, id);
             }
         }

         */

        MethodSpec.Builder getMethodBuilder = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(TypeName.OBJECT)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addParameter(ClassName.get(Class.class), "clazz")
                .addParameter(ClassName.get(String.class), "id")
                .addParameter(ClassName.get(Converter.Factory.class), "factory");

        for (Element element : mSet) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                final String name = typeElement.getQualifiedName().toString() + Treasure.PREFERENCES_SUFFIX;
                final String packageName = name.substring(0, name.lastIndexOf("."));
                final String className = name.substring(packageName.length() + 1);
                final ClassName classType = ClassName.get(packageName, className);
                getMethodBuilder.beginControlFlow("if (clazz.isAssignableFrom($T.class))", ClassName.get(packageName, className))
                        .beginControlFlow("if (id == null)")
                        .addStatement("return new $T(context, factory)", classType)
                        .nextControlFlow("else")
                        .addCode("return new $T(context, factory, id);", classType)
                        .endControlFlow()
                        .endControlFlow();
            }
        }
        getMethodBuilder.addStatement("return null");

        builder.addMethod(getMethodBuilder.build());

        JavaFile javaFile = JavaFile.builder("com.baoyz.treasure", builder.build()).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
        }
    }
}
