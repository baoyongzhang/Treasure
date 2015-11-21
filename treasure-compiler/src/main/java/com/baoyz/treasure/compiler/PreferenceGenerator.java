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

import com.baoyz.treasure.Apply;
import com.baoyz.treasure.Clear;
import com.baoyz.treasure.Commit;
import com.baoyz.treasure.Converter;
import com.baoyz.treasure.Default;
import com.baoyz.treasure.Preferences;
import com.baoyz.treasure.Remove;
import com.baoyz.treasure.Treasure;
import com.baoyz.treasure.compiler.conveter.KeyConverter;
import com.baoyz.treasure.compiler.conveter.SimpleKeyConverter;
import com.baoyz.treasure.compiler.conveter.SimpleValueConverter;
import com.baoyz.treasure.compiler.conveter.ValueConverter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.type.TypeKind.BOOLEAN;
import static javax.lang.model.type.TypeKind.VOID;

/**
 * Created by baoyz on 15/11/10.
 */
public class PreferenceGenerator extends ElementGenerator {

    KeyConverter mKeyConverter = new SimpleKeyConverter();
    ValueConverter mValueConverter = new SimpleValueConverter();

    public PreferenceGenerator(Filer filer) {
        super(filer);
    }

    @Override
    public TypeSpec onCreateTypeSpec(TypeElement element, String packageName, String className) {

        Preferences preferences = element.getAnnotation(Preferences.class);
        Preferences.Edit edit = preferences.edit();

        String fileName = "".equals(preferences.name()) ? className.toLowerCase() : preferences.name();

        TypeSpec.Builder builder = TypeSpec.classBuilder(className + Treasure.PREFERENCES_SUFFIX)
                .addSuperinterface(ClassName.get(packageName, className))
                .addField(ClassName.get("android.content", "SharedPreferences"), "mPreferences", Modifier.PRIVATE)
                .addField(ClassName.get(Converter.Factory.class), "mConverterFactory", Modifier.PRIVATE)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Generated code from Treasure. Do not modify!");

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addParameter(ClassName.get(Converter.Factory.class), "converterFactory")
                .addStatement("mPreferences = context.getSharedPreferences($S, Context.MODE_PRIVATE)", fileName)
                .addStatement("mConverterFactory = converterFactory")
                .build();

        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addParameter(ClassName.get(Converter.Factory.class), "converterFactory")
                .addParameter(ClassName.get(String.class), "id")
                .addStatement("mConverterFactory = converterFactory")
                .addStatement("mPreferences = context.getSharedPreferences($S + \"_\" + id, Context.MODE_PRIVATE)", fileName)
                .build();

        builder.addMethod(constructor);
        builder.addMethod(constructor2);

        // implements
        // implements extension method
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        for (Element e : enclosedElements) {
            if (e instanceof ExecutableElement) {
                String editMethod = edit == Preferences.Edit.APPLY ? "apply" : "commit";

                ExecutableElement methodElement = (ExecutableElement) e;

                String methodName = methodElement.getSimpleName().toString();

                if (methodName.equals("<init>")) {
                    continue;
                }

                TypeMirror returnType = methodElement.getReturnType();

                final TypeName returnTypeName = TypeName.get(returnType);
                MethodSpec.Builder methodBuilder = MethodSpec
                        .methodBuilder(methodName)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(returnTypeName);

                // throw exception
                if (methodElement.getThrownTypes() != null && methodElement.getThrownTypes().size() > 0) {
                    List<TypeName> exceptions = new ArrayList<>();
                    for (TypeMirror type : methodElement.getThrownTypes()) {
                        TypeName typeName = TypeName.get(type);
                        exceptions.add(typeName);
                    }
                    methodBuilder.addExceptions(exceptions);
                }

                if (methodElement.getAnnotation(Commit.class) != null) {
                    editMethod = "commit";
                } else if (methodElement.getAnnotation(Apply.class) != null) {
                    editMethod = "apply";
                }

                if (methodElement.getAnnotation(Clear.class) != null) {
                    // clear preferences
                    methodBuilder.addStatement("mPreferences.edit().clear().$L()", editMethod);
                } else if (methodElement.getAnnotation(Remove.class) != null) {

                    boolean isReturn = false;
                    if (returnType.getKind().equals(BOOLEAN)) {
                        editMethod = "commit";
                        isReturn = true;
                    }

                    String key = mKeyConverter.convert(methodName);
                    methodBuilder.addStatement((isReturn ? "return " : "") + "mPreferences.edit().remove($S).$L()", key, editMethod);
                } else {

                    /**
                     *
                     mPreferences.getAll();
                     mPreferences.getBoolean();
                     mPreferences.getFloat();
                     mPreferences.getInt();
                     mPreferences.getLong();
                     mPreferences.getString();
                     mPreferences.getStringSet();
                     */

                    List<? extends VariableElement> parameters = methodElement.getParameters();

                    String key = mKeyConverter.convert(methodName);
                    if (returnType.getKind().equals(VOID) || (returnType.getKind().equals(BOOLEAN) && parameters != null && parameters.size() > 0)) {
                        // setter
                        boolean isReturn = false;
                        if (!returnType.getKind().equals(VOID)) {
                            // the method return value is boolean and not have parameter, that edit mode is commit.
                            editMethod = "commit";
                            isReturn = true;
                        } else if (parameters == null || parameters.size() < 1) {
                            throw new RuntimeException("The method must have a return value or parameter");
                        }
                        VariableElement param = parameters.get(0);
                        String value = param.getSimpleName().toString();
                        final TypeName paramTypeName = TypeName.get(param.asType());
                        methodBuilder.addParameter(paramTypeName, value);

                        String setterMethodName = TypeMethods.setterMethod(param.asType());
                        if (setterMethodName == null) {
                            // Object convert to String
//                            if (mConverterFactory != null) {
//                                Converter<?, String> converter = mConverterFactory.fromType(obj.getClass());
//                                final String value = converter.convert(obj);
//                            }
                            setterMethodName = "putString";
                            methodBuilder
                                    .beginControlFlow("if (mConverterFactory == null) ")
                                    .addStatement("throw new NullPointerException(\"You need set ConverterFactory Object. :D\")")
                                    .endControlFlow()
                                    .addStatement("$T converter = mConverterFactory.fromType($T.class)", ParameterizedTypeName.get(ClassName.get(Converter.class), paramTypeName, ClassName.get(String.class)), paramTypeName)
                                    .addStatement("String value = converter.convert($L)", value)
                                    .addStatement((isReturn ? "return " : "") + "mPreferences.edit().$L($S, value).$L()", setterMethodName, key, editMethod);
                        } else {
                            methodBuilder.addStatement((isReturn ? "return " : "") + "mPreferences.edit().$L($S, $L).$L()", setterMethodName, key, value, editMethod);
                        }
                    } else {
                        // getter
                        String[] defaultVal = null;
                        final Default annotation = methodElement.getAnnotation(Default.class);
                        if (annotation != null) {
                            defaultVal = annotation.value();
                        }
                        String getterMethodName = TypeMethods.getterMethod(returnType);
                        if (getterMethodName == null) {
                            getterMethodName = "getString";
                            methodBuilder
                                    .beginControlFlow("if (mConverterFactory == null) ")
                                    .addStatement("throw new NullPointerException(\"You need set ConverterFactory Object. :D\")")
                                    .endControlFlow()
                                    .addStatement("$T converter = mConverterFactory.toType($T.class)", ParameterizedTypeName.get(ClassName.get(Converter.class), ClassName.get(String.class), returnTypeName), returnTypeName)
                                    .addStatement("return converter.convert(mPreferences.$L($S, $L))", getterMethodName, key, mValueConverter.convert(returnType, defaultVal));
                        } else {
                            methodBuilder.addStatement("return mPreferences.$L($S, $L)", getterMethodName, key, mValueConverter.convert(returnType, defaultVal));
                        }
                    }
                }

                builder.addMethod(methodBuilder.build());

            }
        }

        return builder.build();
    }
}
