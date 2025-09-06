# 快速开始

## 目录
- [1. 创建项目](#创建项目)
- [2. 部署你的Bot](#部署你的Bot)

## 创建项目

1. 首先，你需要一个可靠的Java IDE来进行开发，这里推荐使用[IntelliJ IDEA](https://www.jetbrains.com.cn/idea/)
2. 创建一个新的gradle项目，**Java版本需选用21**
3. 在`build.gradle`文件中，进行如下配置：
```
plugins {
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

jar {
    manifest {
        attributes(
                'Main-Class': 'io.lemonjuice.flandre_bot.FlandreBot'
        )
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.lemonjuice95:flandre-bot-framework:${替换为具体框架版本}'
}
```

在此之后，你便可以开始进行Bot的开发了，祝一切顺利！

## 部署你的Bot

WIP