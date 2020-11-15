# SimpleSPI
android跨模块调用服务管理器

## 描述
该库的目的是实现跨模块调用服务，利用注解和AOP实现服务自动注册，客户端不需要关注接口的具体实现，只需要调用服务暴露的接口即可。

## 工程结构
工程有4个module,分别是基础依赖api，业务模块moduleOne和moduleTwo，这里使用client模块对其他业务模块对功能进行调用。
![模块](https://github.com/CrazyGuizi/SimpleSPI/blob/master/doc/module.jpg)

## 具体效果
![效果](https://github.com/CrazyGuizi/SimpleSPI/blob/master/doc/screen.gif)
