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
package com.baoyz.treasure;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by baoyz on 15/11/10.
 */
public class Treasure {

    public static final String PREFERENCES_SUFFIX = "$$Preferences";

    private static HashMap<Class<?>, Object> mPreferencesCache;

    static {
        mPreferencesCache = new HashMap();
    }

    public static <T> T get(Context context, Class<T> interfaceClass) {
        T value = (T) mPreferencesCache.get(interfaceClass);
        if (value != null) {
            return value;
        }
        try {
            final Constructor<?> constructor = Class.forName(getPreferencesClassName(interfaceClass)).getConstructor(Context.class);
            value = (T) constructor.newInstance(context);
            if (value != null) {
                mPreferencesCache.put(interfaceClass, value);
                return value;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getPreferencesClassName(Class interfaceClass) {
        final String interfaceName = interfaceClass.getCanonicalName();
        return interfaceName + PREFERENCES_SUFFIX;
    }
}
