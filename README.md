# SimpleSPI
android跨模块调用服务管理器

## 描述
该库的目的是实现跨模块调用服务，利用注解和AOP实现服务自动注册，客户端不需要关注接口的具体实现，只需要调用服务暴露的接口即可。

## 工程结构
工程有4个module,分别是基础依赖api，业务模块moduleOne和moduleTwo，这里使用client模块对其他业务模块对功能进行调用。
![模块](https://github.com/CrazyGuizi/SimpleSPI/blob/master/doc/module.jpg)

## 具体效果
![效果](https://github.com/CrazyGuizi/SimpleSPI/blob/master/doc/screen.gif)

## 使用

- service库引用
在需要使用服务的模块中引用依赖：
```
implementation 'com.ldg.serviceprovider:service:1.0.0'
```

- service注入插件
在根目录的build.gradle文件中添加
```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.ldg.serviceprovider:plugin-inject:1.0.0'
    }
}
```

然后在可运行的app项目中添加插件：
```
apply plugin: 'com.ldg.serviceprovider.inject'
```

- 示例
1. 定义一个服务接口 `ApiOne` ，提供一个 `getMgs` 方法
2. 在实现类 `ApiOneImpl` 上添加注释 `@ServiceDeclare(provider = ApiOne.class)`
3. 在调用的地方执行方法: `ServiceManager.get(ApiOne.class).getMsg()`
