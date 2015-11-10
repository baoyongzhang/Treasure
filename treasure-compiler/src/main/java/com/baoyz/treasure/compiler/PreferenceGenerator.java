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

import com.baoyz.treasure.Preferences;
import com.baoyz.treasure.Treasure;
import com.baoyz.treasure.conveter.KeyConverter;
import com.baoyz.treasure.conveter.NormalKeyConverter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.type.TypeKind.VOID;

/**
 * Created by baoyz on 15/11/10.
 */
public class PreferenceGenerator extends FilerGenerator {

    KeyConverter mKeyConverter = new NormalKeyConverter();

    public PreferenceGenerator(Filer filer) {
        super(filer);
    }

    @Override
    public TypeSpec onCreateTypeSpec(TypeElement element, String packageName, String className) {

        Preferences preferences = element.getAnnotation(Preferences.class);

        String fileName = "".equals(preferences.name()) ? className.toLowerCase() : preferences.name();

        TypeSpec.Builder builder = TypeSpec.classBuilder(className + Treasure.PREFERENCS_SUFFIX)
                .addSuperinterface(ClassName.get(packageName, className))
                .addField(ClassName.get("android.content", "SharedPreferences"), "mPreferences", Modifier.PRIVATE)
                .addModifiers(Modifier.PUBLIC);

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addStatement("mPreferences = context.getSharedPreferences($S, Context.MODE_PRIVATE);", fileName)
                .build();

        builder.addMethod(constructor);

        // implements
        // implements extension method
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        for (Element e : enclosedElements) {
            if (e instanceof ExecutableElement) {
                ExecutableElement ee = (ExecutableElement) e;

                // TODO return value?
                boolean isVoid = false;
                if (ee.getReturnType().getKind().equals(VOID)) {
                    isVoid = true;
                }
                String methodName = ee.getSimpleName().toString();
                MethodSpec.Builder methodBuilder = MethodSpec
                        .methodBuilder(methodName)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(ee.getReturnType()));

                // throw exception
                if (ee.getThrownTypes() != null && ee.getThrownTypes().size() > 0) {
                    List<TypeName> exceptions = new ArrayList<>();
                    for (TypeMirror type : ee.getThrownTypes()) {
                        TypeName typeName = TypeName.get(type);
                        exceptions.add(typeName);
                    }
                    methodBuilder.addExceptions(exceptions);
                }

                String key = mKeyConverter.convert(methodName);
                if (isVoid) {
                    // setter
                    List<? extends VariableElement> parameters = ee.getParameters();
                    if (parameters == null || parameters.size() < 1) {
                        throw new RuntimeException("The method must have a return value or parameter");
                    }
                    VariableElement param = parameters.get(0);
                    String value = param.getSimpleName().toString();
                    methodBuilder.addParameter(TypeName.get(param.asType()), value);
                    methodBuilder.addStatement("mPreferences.edit().putString($S, $L).apply()", key, value);
                } else {
                    // getter
                    String defaultVal = "";
                    methodBuilder.addStatement("return mPreferences.getString($S, $S)", defaultVal);
                }

                /**
    @Override
    public String getUsername() {
        return mPreferences.getString("username", null);
    }

                 @Override
                 public void setUsername(String username) {
                    mPreferences.edit().putString("username", username).apply();
                 }

                 */


                builder.addMethod(methodBuilder.build());

            }
        }

        return builder.build();
    }
}
