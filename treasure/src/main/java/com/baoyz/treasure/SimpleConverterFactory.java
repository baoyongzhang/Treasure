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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Created by baoyz on 15/11/21.
 */
public class SimpleConverterFactory implements Converter.Factory {

    Converter<? extends Serializable, String> mFromSerializableConverter = new Converter<Serializable, String>() {
        @Override
        public String convert(Serializable value) {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream out = new ObjectOutputStream(byteOutputStream);
                out.writeObject(value);
                out.close();
                return Base64.encodeToString(byteOutputStream.toByteArray(), Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    Converter<String, ? extends Serializable> mToSerializableConverter = new Converter<String, Serializable>() {
        @Override
        public Serializable convert(String value) {
            try {
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(value, Base64.DEFAULT)));
                return (Serializable) in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    Converter<Parcelable, String> mFromParcelableConverter = new Converter<Parcelable, String>() {
        @Override
        public String convert(Parcelable value) {
            Parcel parcel = Parcel.obtain();
            value.writeToParcel(parcel, 0);
            final byte[] marshall = parcel.marshall();
            parcel.recycle();
            return Base64.encodeToString(marshall, Base64.DEFAULT);
        }
    };

    class ToParcelableConverter<T> implements Converter<String, T> {

        public ToParcelableConverter(Parcelable.Creator<T> creator) {
            this.mCreator = creator;
        }

        Parcelable.Creator<T> mCreator;

        @Override
        public T convert(String value) {
            final byte[] bytes = Base64.decode(value, Base64.DEFAULT);
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0);
            return mCreator.createFromParcel(parcel);
        }
    }

    @Override
    public <F> Converter<F, String> fromType(Type fromType) {
        if (fromType instanceof Class) {
            Class fromClass = (Class) fromType;
            if (Serializable.class.isAssignableFrom(fromClass)) {
                return (Converter<F, String>) mFromSerializableConverter;
            }
            if (Parcelable.class.isAssignableFrom(fromClass)) {
                return (Converter<F, String>) mFromParcelableConverter;
            }
        }
        throw new IllegalArgumentException("SimpleConverterFactory supports only Serializable and Parcelable");
    }

    @Override
    public <T> Converter<String, T> toType(Type toType) {
        if (toType instanceof Class) {
            Class toClass = (Class) toType;
            if (Serializable.class.isAssignableFrom(toClass)) {
                return (Converter<String, T>) mToSerializableConverter;
            }
            if (Parcelable.class.isAssignableFrom(toClass)) {
                try {
                    return new ToParcelableConverter<T>((Parcelable.Creator<T>) toClass.getField("CREATOR").get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new IllegalArgumentException("SimpleConverterFactory supports only Serializable and Parcelable");
    }
}
