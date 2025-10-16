# 快速开始

## 目录
- [1. 创建项目](#创建项目)
- [2. 部署你的Bot](#部署你的Bot)

## 创建项目

1. 首先，你需要一个可靠的Java IDE来进行开发，这里推荐使用[IntelliJ IDEA](https://www.jetbrains.com.cn/idea/)
2. 创建一个新的gradle项目，**Java版本需选用21**
3. 在`build.gradle`文件中，进行如下配置：
```groovy
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

如果你在寻找一些简单的参考，示例项目在[这里](https://github.com/LemonJuice95/flandre-bot-example)

## 部署你的Bot

### Websocket正向连接模式

#### 实现端部分

1. 部署Bot前，你需要一个`OneBot11实现端`来与QQ进行对接（具体框架请自行搜索）
2. 在实现端配置正向WebSocket连接，建立一个Websocket服务器后，选择字符串作为消息格式
3. **<u>强烈建议为你的WebSocket连接配置一个Token！！！</u>**
4. 启动实现端的服务

#### 应用端部分

1. **在此之前，请先确保你进行部署的设备有Java 21环境，且环境变量已被正确设置**
2. 使用gradle的shadowJar任务构建Bot的jar包
3. 在运行目录下，根据你的系统创建启动脚本（win系统使用.bat文件，linux系统使用.sh文件），写入
```shell
java -jar ${你的jar包文件名}.jar
# 如果bot在windows系统上部署，建议在启动脚本后加入pause
```
4. 确保OneBot实现端的服务器正在运行后，运行启动脚本
5. 首次启动时，会要求用户在运行目录的`config/bot.properties`文件中进行配置，配置方式请参考[这里](base_config.md)

### Websocket反向连接模式

#### 应用端部分

1. 同上，先使用shadowJar任务构建jar包，制作启动脚本，然后运行一次应用，在生成配置中文件进行配置
2. 配置完成后再次启动应用端

#### 实现端部分

1. 在实现端配置Websocket反向连接，建立一个指向应用端的Websocket客户端
2. 选择字符串作为消息格式
3. 确保应用端正在运行后，启动客户端