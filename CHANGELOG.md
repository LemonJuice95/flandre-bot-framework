# ChangeLog

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