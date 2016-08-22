# Treasure

![Download](https://api.bintray.com/packages/baoyongzhang/maven/Treasure/images/download.svg) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Treasure-green.svg?style=true)](https://android-arsenal.com/details/1/2787)

`Treasure` is a wrapper library for Android SharePreferences , only you need to define the interface without write implementation code. Non-reflective, high-performance runtime.

# Usage

##### Step 1, Add dependency

Gradle

``` groovy
compile 'com.baoyz.treasure:treasure:0.6.4'
provided 'com.baoyz.treasure:treasure-compiler:0.6.4'
```

##### Step 2, Define interface

``` java
@Preferences
public interface SimplePreferences {

    String getUsername();

    void setUsername(String username);

}
```

We define an `interface`, use `@Preferences`annotation declared, and then declared some `get`、`set` methods for getting and setting values. Method name will be used as storage `key`, for example,  `getUsername()` and `setUsername()` the key is `username` , which is calling `setUsername()` to set value, you can get to value by  `getUsername()`, because they are the same key.

##### Step 3, Get instance

``` java
SimplePreferences preferences = Treasure.get(context, SimplePreferences.class);
preferences.setUsername("Hello Treasure!");
preferences.getUsername(); // return "Hello Treasure!"
```

Get `Preferences` instance object by `Treasure.get()` method, which can call `set` method to set value, call `get` method to get value.

# Advanced Features

#### Multiple Files

Multiple files can be generated for the `Preferences`, such as multi-account management, different accounts have different `Preferences`.

``` java
Treasure.get(context, SimplePreferences.class, "id_one");
Treasure.get(context, SimplePreferences.class, "id_two");
```

#### Default value

`@Default` annotation can specify the default value returned.

``` java
@Default("Hello Treasure!")
String getUsername();

@Default("false")
boolean isLogin();

// default is 1 hour
@Default("1000 * 60 * 60")
long getTimeout();

@Default({"hello", "world", "!"})
Set<String> getStringSet();
```

If not specify `@Default`, then the default value in the table below.

| return value type | default value |
| ----------------- | ------------- |
| int               | 0             |
| float             | 0f            |
| long              | 0l            |
| boolean           | false         |
| String            | null          |
| Set\<String>      | null          |

#### Commit type

`set` method default commit type is `edit().apply()`, if you want to use `edit().commit()`, there are three ways.

The first way, all valid.

``` java
@Preferences(edit = Preferences.Edit.COMMIT)
public interface SimplePreferences
```

The second way, specify method valid.

``` java
@Commit
void setUsername(String username);
```

The third way, specify method return value is `boolean`.

``` java
boolean setUsername(String username);
```

The way, if the `set` method return value is `boolean`, then commit type is `edit().commit()` and  the returns will return  `edit().commit()` value.

#### Remove data

``` java
@Remove
void removeUsername();

@Remove
void deleteTimeout();
```

Using `@Remove` annotation declared methods, calling methods to remove the corresponding `key` data.

#### Clear data

``` java
@Clear
void clear();
```

Using `@Clear` annotation declared methods, calling methods to clear the preferences data.

#### Effective time

You can use `@Expired` specify a effective time configuration. Start counting from the last call setter method. @Expired can declare in the getter and setter methods, the same effect.

``` java
// Ten seconds expired
@Expired(value = 10, unit = Expired.UNIT_SECONDS)
String getTestExpired();
```

`@Expired` can also declare the method parameters for dynamically specify a valid time, it can only be declared in the parameter `setter` methods. `unit` default is `UNIT_MILLISECONDS`.

``` java
void setTestExpired(String value, @Expired(unit = Expired.UNIT_MINUTES) int second);
```

#### Object serialization

Treasure support `Serializable` and `Parcelable`.

``` java
// Serializable or Parcelable
class User implements Serializable {...}

// Preferences Interface
void setUser(User user);
User getUser();
```

You can customize the converter. For example, using `GSON`.

``` java
public class GsonConverterFactory implements Converter.Factory {

    @Override
    public <F> Converter<F, String> fromType(Class<F> fromClass) {
        return new Converter<F, String>() {
            @Override
            public String convert(F value) {
                return new Gson().toJson(value);
            }
        };
    }

    @Override
    public <T> Converter<String, T> toType(final Class<T> toClass) {
        return new Converter<String, T>() {
            @Override
            public T convert(String value) {
                return new Gson().fromJson(value, toClass);
            }
        };
    }
}
```

After you customize, you need to call `Treasure.set ConverterFactory ()` method to set a custom converter.

``` java
Treasure.setConverterFactory(new GsonConverterFactory());
```

#### Get `SharedPreferences` Object

``` java
@Prototype
SharedPreferences getSharedPreferences();
```

#### About method name

If the method name prefix is `get`、 `set` 、`put`、`is`、`remove`、`delete`, then ignores the prefix and all lowercase as `key`, otherwise the method full name all lowercase as `key`.

#### Proguard

`Treasure` not need to add any configuration of `Proguard`.



# Thanks

[Favor](https://github.com/soarcn/Favor)（Inspired by it）

[javapoet](https://github.com/square/javapoet)



# License

``` 
The MIT License (MIT)

Copyright (c) 2015 baoyongzhang <baoyz94@gmail.com>
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
