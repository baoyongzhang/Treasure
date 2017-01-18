# Treasure

[![Travis CI](https://travis-ci.org/baoyongzhang/Treasure.svg?branch=master) ](https://travis-ci.org/baoyongzhang/Treasure)[![Download](https://api.bintray.com/packages/baoyongzhang/maven/Treasure/images/download.svg) ](https://bintray.com/baoyongzhang/maven/Treasure/_latestVersion)[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Treasure-green.svg?style=true)](https://android-arsenal.com/details/1/2787)

[English document](./README_EN.md)

`Treasure`是一个Android平台上基于`SharePreferences`的偏好存储库，只需要定义接口，无需编写实现，默认支持`Serializable`和`Parcelable`。运行时0反射，不仅使用方便而且性能和原生写法几乎无差别。

## 使用方法

##### 1、添加依赖

#### Gradle

``` groovy
compile 'com.baoyz.treasure:treasure:0.7.4'
annotationProcessor 'com.baoyz.treasure:treasure-compiler:0.7.4'
```

#### Gradle Plugin

如果在多 Module 中同时使用 Treasure，会出现 `Multiple dex files define Lcom/baoyz/treasure/PreferencesFinder` 错误，使用 Treasure Gradle Plugin 可以解决这个错误。

在 `buildscript` 中添加依赖

``` groovy
dependencies {
    classpath 'com.baoyz.treasure:treasure-gradle:0.7.4'
}
```

然后只在你的 Application Module 中 apply 插件，Library Module 不需要。

``` groovy
apply plugin: 'com.baoyz.treasure'
```

如果没有多 Module 同时使用 Treasure 的情况，不需要使用这个插件。

##### 2、定义接口

``` java
@Preferences
public interface SimplePreferences {

    String getUsername();

    void setUsername(String username);

}

```

我们定义了一个`interface`，需要使用`@Preferences`注解进行声明。然后可以定义一系列的`get`、`set`方法，用于获取和设置值。方法名会作为存储的`key`，例如`getUsername()`和`setUsername()`的`key`就是`username`，也就是通过`setUsername()`设置的`value`可以通过`getUsername()`获取到，因为他们的`key`是一样的。

##### 3、实例化

``` java
SimplePreferences preferences = Treasure.get(context, SimplePreferences.class);
preferences.setUsername("Hello Treasure!");
preferences.getUsername(); // return "Hello Treasure!"
```

通过`Treasure.get()`方法可以获取指定的`Preferences`对象，之后可以调用`set`方法设置值，通过对应的`get`方法获取值。

## 高级用法

#### 多文件

可以为一个`Preferences`生成多个文件，例如多账号管理，不同账号有不同`Preferences`。

``` java
Treasure.get(context, SimplePreferences.class, "id_one");
Treasure.get(context, SimplePreferences.class, "id_two");
```

`Treasure`提供了一个重载的`get`方法，可以传入一个`String`类型的ID，不同ID返回的`Preferences`对象不同，保存的文件也不同。（感谢好基友[zzz40500](https://github.com/zzz40500)提出的建议）

#### 默认值

`@Default`注解可以指定`get`方法的默认值。

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

如果没有指定`@Default`那么默认值见下表。

| 返回值类型        | 默认值   |
| ------------ | ----- |
| int          | 0     |
| float        | 0f    |
| long         | 0l    |
| boolean      | false |
| String       | null  |
| Set\<String> | null  |

#### 提交类型

设置值的默认提交类型是`edit().apply()`，如果想使用`edit().commit()`有三种方式。

##### 1、全局

``` java
@Preferences(edit = Preferences.Edit.COMMIT)
public interface SimplePreferences
```

全局指定提交方式之后，所有的`set`方法都会以`commit()`方法提交数据。

##### 2、注解指定方法

``` java
@Commit
void setUsername(String username);
```

使用`@Commit`注解只对当前方法有效。

##### 3、指定方法返回`boolean`

``` java
boolean setUsername(String username);
```

无论上面两种方式有没有设置，只要`set`方法的返回值是`boolean`，那么这个方法就会以`commit()`方法提交，并且返回`commit()`的结果。

#### 移除数据

``` java
@Remove
void removeUsername();

@Remove
void deleteTimeout();
```

使用`@Remove`注解修饰方法，调用方法移除对应`key`的数据。

#### 清空数据

``` java
@Clear
void clear();
```

可以声明一个方法，使用`@Clear`注解修饰，那么调用这个方法就会清空整个`Preferences`的数据。

#### 有效期

可以使用`@Expired`指定某个配置的有效期。开始时间是最后一次`set`的时间。`@Expired`可以修饰在`getter`和`setter`方法 ，效果相同。

``` java
// 10秒钟后过期
@Expired(value = 10, unit = Expired.UNIT_SECONDS)
String getTestExpired();
```

`@Expired`还可以修饰在方法参数上，用于动态指定有效时间，只能在`setter`方法的参数上修饰。

``` java
// second参数的值是有效时间，单位是UNIT_MINUTES（分钟）
void setTestExpired(String value, @Expired(unit = Expired.UNIT_MINUTES) int second);
```

`@Expired`的`unit`不指定默认是`UNIT_MILLISECONDS`毫秒。

#### 对象序列化

如果`interface`中声明的数据类型不是`SharePreferences`支持的，需要用到转换器，`Treasure`默认提供`Serializable`和`Parcelable`的支持。

``` java
// Serializable or Parcelable
class User implements Serializable {...}

// Preferences Interface
void setUser(User user);
User getUser();
```

可以自定义转换规则，例如用`Gson`将对象以`JSON`的形式保存。

``` java
public class GsonConverterFactory implements Converter.Factory {

    @Override
    public <F> Converter<F, String> fromType(Type fromType) {
        return new Converter<F, String>() {
            @Override
            public String convert(F value) {
                return new Gson().toJson(value);
            }
        };
    }

    @Override
    public <T> Converter<String, T> toType(final Type toType) {
        return new Converter<String, T>() {
            @Override
            public T convert(String value) {
                return new Gson().fromJson(value, toType);
            }
        };
    }
}
```

自定义之后，需要调用`Treasure.setConverterFactory()`方法设置自定义的转换规则。

``` java
Treasure.setConverterFactory(new GsonConverterFactory());
```

#### 获取 SharedPreferences 对象

``` java
@Prototype
SharedPreferences getSharedPreferences();
```

使用 `@Prototype` 修饰，返回值是 `SharedPreferences` 类型，调用此方法可以获取原始的 `SharedPreferences` 对象。

#### 自定义 Key
``` java
@Key("custom_key")
String getValue();
@Key("custom_key")
void setValue(String value);
```

#### 关于方法名

如果方法名以`get`、`set`、`put`、`is`、`remove`、`delete`开头，那么会忽略这些前缀并且全部小写作为`key`，如果不包含这些前缀，那么方法名全部小写会作为`key`。

#### Proguard

`Treasure`运行时0反射，不需要添加`Proguard`配置。



# 致谢

[Favor](https://github.com/soarcn/Favor)（灵感来源）

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
