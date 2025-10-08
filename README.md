# Flandre-Bot-Framework

[![java21](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Maven](https://img.shields.io/badge/Maven-Central-blue.svg)](https://central.sonatype.com/artifact/io.github.lemonjuice95/flandre-bot-framework)

基于Java 21的轻量级QQ Bot框架，遵循OneBot 11协议，使用Websocket连接

注：即使在反向连接模式下，也仅支持**单个**OneBot实现的客户端与应用端连接，如需使用多个bot账号请**多开应用端实例**

**项目处在开发初期，目前还存在非常多不完善的地方**

有关基于本项目的示例项目，请参考[这里](https://github.com/LemonJuice95/flandre-bot-example)

## 重要声明（2025.9.12）
本项目与另一个同名的bot框架"[Flandre](https://github.com/FlandreBot/Flandre)"（使用C#开发）无任何关联

本项目的开发者（我）一开始使用这个名称只是出于对“[芙兰朵露·斯卡雷特](https://thwiki.cc/%E8%8A%99%E5%85%B0%E6%9C%B5%E9%9C%B2%C2%B7%E6%96%AF%E5%8D%A1%E8%95%BE%E7%89%B9)”这一角色的喜爱

当时的我并不知道市面上已经存在了一个使用此名称的OneBot应用项目

在我发现另一个项目的存在时，此框架已经初步完成，发布到Maven Central，经历了一段时间的迭代。
并且此框架的所有官方插件均已使用了"Flandre"这一名称

将此项目重命名已经不现实，只能继续沿用原有名称

**感谢各位的理解**

## [项目文档](docs/index.md)

## [快速开始](docs/quickstart.md)

## [ChangeLog](CHANGELOG.md)

## 为什么说“轻量级” ？

项目**没有**使用Spring等主流Java应用框架，而是选择自研生命周期管理；事件总线、资源加载等功能模块均为自研实现，**没有引入外部依赖**；框架没有内置**数据库连接**等套件，通过外部插件支持

这样的模式换来了相对较低的运行开销：

框架在空载的情况下启动仅需约0.6s，硬盘占用大小约7MB，内存占用约170MB

框架的开发者基于此框架开发的一个应用级Bot，启动仅需<2s，硬盘占用大小<15MB，内存占用约250MB

>上述数据的测试机配置：
> - CPU：i5-8265u CPU @ 1.60GHz （四核八线程，TDP 15W）
> - 内存：单通道 8GB DDR4 2400MHz
> - 系统：Ubuntu Server 25.04

以此为多开实例提供了支持

## 麻雀虽小，五脏俱全

框架本体内置了以下功能：
 - bot命令分发器，及一套命令执行器的抽象工具类（支持权限控制）
 - 文件资源加载器（支持从类路径与外部目录加载资源）
 - 事件总线（使用注解进行注册）
 - bot账号信息及群聊/好友上下文管理器
 - 交互式控制台（支持扩展指令）
 - 支持有复杂依赖关系的插件加载器（为应用带来更多可能）

## License

本项目使用[MIT许可证](LICENSE)

您可以自由使用、修改或分发本项目的代码，但必须保留原始版权声明与许可证