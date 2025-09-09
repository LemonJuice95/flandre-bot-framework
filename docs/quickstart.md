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
                'Main-Class': 'io.lemonjuice.flandre_bot_framework.FlandreBot'
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

### OneBot实现端部分

1. 部署Bot前，你需要一个`OneBot11实现端`来与QQ进行对接（具体框架请自行搜索）
2. 在OneBot实现端配置正向WebSocket连接后，选择字符串作为消息格式
3. **<u>强烈建议为你的WebSocket连接配置一个Token！！！</u>**
4. 启动实现端的服务

### 应用端部分

1. **在此之前，请先确保你进行部署的设备有Java 21环境，且环境变量已被正确设置**
2. 使用gradle的shadowJar任务构建Bot的jar包
3. 在运行目录下，根据你的系统创建启动脚本（win系统使用.bat文件，linux系统使用.sh文件），写入
```
java -jar ${你的jar包文件名}.jar
# 如果bot在windows系统上部署，建议在启动脚本后加入pause
```
4. 确保OneBot实现端的服务正在运行后，运行启动脚本
5. 首次启动时，会要求用户在运行目录的`config/bot.properties`文件中进行配置，配置方式请参考[这里](base_config.md)