package com.baoyz.treasure;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.io.Serializable;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class SimpleConverterFactoryTest extends ApplicationTestCase<Application> {
    public SimpleConverterFactoryTest() {
        super(Application.class);
    }

    public void testSerializableConverter() {

        assertTrue(Serializable.class.isAssignableFrom(Person.class));

        final SimpleConverterFactory simpleConverterFactory = new SimpleConverterFactory();
        final Converter<Person, String> fromConverter = simpleConverterFactory.fromType(Person.class);

        Person person1 = new Person();
        person1.name = "Jack";
        person1.age = 21;
        final String result = fromConverter.convert(person1);
        Log.d("test", result);
        assertNotNull(result);

        final Converter<String, Person> toConverter = simpleConverterFactory.toType(Person.class);
        final Person person2 = toConverter.convert(result);

        assertEquals(person1, person2);
    }

    public void testParcelableConverter() {

        assertTrue(Serializable.class.isAssignableFrom(Person.class));

        final SimpleConverterFactory simpleConverterFactory = new SimpleConverterFactory();
        final Converter<MyParcelable, String> fromConverter = simpleConverterFactory.fromType(MyParcelable.class);

        MyParcelable data = new MyParcelable();
        data.intData = 10;
        data.stringData = "parcelable";
        final String result = fromConverter.convert(data);
        Log.d("test", result);
        assertNotNull(result);

        final Converter<String, MyParcelable> toConverter = simpleConverterFactory.toType(MyParcelable.class);
        final MyParcelable data2 = toConverter.convert(result);

        assertEquals(data, data2);
    }

    static class Person implements Serializable {
        String name;
        int age;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Person person = (Person) o;

            if (age != person.age) return false;
            return !(name != null ? !name.equals(person.name) : person.name != null);

        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + age;
            return result;
        }

    }

    static class MyParcelable implements Parcelable {
        private int intData;
        private String stringData;

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(intData);
            out.writeString(stringData);
        }

        public static final Parcelable.Creator<MyParcelable> CREATOR
                = new Parcelable.Creator<MyParcelable>() {
            public MyParcelable createFromParcel(Parcel in) {
                return new MyParcelable(in);
            }

            public MyParcelable[] newArray(int size) {
                return new MyParcelable[size];
            }
        };

        private MyParcelable(Parcel in) {
            intData = in.readInt();
            stringData = in.readString();
        }

        public MyParcelable() {

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyParcelable that = (MyParcelable) o;

            if (intData != that.intData) return false;
            return !(stringData != null ? !stringData.equals(that.stringData) : that.stringData != null);

        }

        @Override
        public int hashCode() {
            int result = intData;
            result = 31 * result + (stringData != null ? stringData.hashCode() : 0);
            return result;
        }
    }
}