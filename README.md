# Treasure

[ ![Download](https://api.bintray.com/packages/baoyongzhang/maven/Treasure/images/download.svg) ](https://bintray.com/baoyongzhang/maven/Treasure/_latestVersion)

[English document](./README_EN.md)

`Treasure`是一个Android平台上基于`SharePreferences`的偏好存储库，只需要定义接口，无需编写实现。运行时0反射，不仅使用方便而且性能和原生写法几乎无差别。

## 使用方法

##### 1、添加依赖

Gradle

``` groovy
compile 'com.baoyz.treasure:treasure:0.3.1'
provided 'com.baoyz.treasure:treasure-compiler:0.3.1'
```

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