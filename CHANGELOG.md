# ChangeLog

## v0.5.2
 - 插件在加载出现异常时记录异常日志

## v0.5.1
 - 为插件提供默认的空依赖列表

## v0.5.0
 - 修复`SimpleGroupCommandRunner`在消息内可能@Bot超过一次时，命令仍然匹配的问题
 - 更改`MessageContext`的构造方式，采用类似建造者模式的链式调用，但并非纯正的建造者模式
 - `MessageContext`添加快捷回复消息的方法
 - 使用更加完善的插件加载器替代原有的PluginInitEvent()

## v0.4.1
 - 修复版本显示问题
 - 修复slf4j的版本冲突问题

## v0.4.0
 - 修复框架版本显示异常的问题
 - 调整BotInitEvent的位置
 - 试图解决slf4j的版本冲突问题

## v0.3.0
 - 【破坏性变更】更改项目包名

## v0.2.3
 - 添加启动信息

## v0.2.2
 - 修复无效echo导致的异常

## v0.2.1
 - 修复WebSocket客户端无法正常启动的问题

## v0.2.0
 - 弃用`SendingUtils`类，`Message`类添加消息上下文包装发送逻辑
 - 添加WebSocket请求-响应模式的实现
 - WebSocket客户端发送消息现在使用阻塞队列
 - 主类中添加`getName()`方法用于获取bot名称
 - 添加事件支持插件加载

## v0.1.1
 - 修复bot启动时记录的日志中bot名为null的问题

## v0.1.0
 - 初始版本