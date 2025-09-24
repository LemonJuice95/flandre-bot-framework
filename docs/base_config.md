# 基础配置文件

配置文件位于运行目录下的`config/bot.properties`文件内，在程序检测到该文件不存在时将会自动释放

## bot.properties配置项

### 基础内容
- `bot.name` 
  - bot的名称
- `bot.log_messages` 
  - 是否在日志中记录收到的消息


### 网络
- `bot.network.mode`
  - 应用端的连接模式
  - 0：Websocket正向连接
  - 1：Websocket反向连接
  - 默认值：0
- `bot.network.client.url` 
  - **仅在正向连接（应用端作为客户端）模式下有效**
  - 实现端侧服务器的url
- `bot.network.server.listening_ips`
  - **仅在反向连接（应用端作为服务端）模式下有效**
  - 应用端服务器监听的ip
  - 默认值："0.0.0.0"
- `bot.network.server.port`
  - **仅在反向连接（应用端作为服务端）模式下有效**
  - 应用端服务器监听的端口
- `bot.network.token` 
  - 连接附带的token

### 调试模式
- `bot.debug_mode` 
  - 是否启用调试模式
- `bot.debug_users` 
  - 拥有调试权限用户的qq号
  - 允许用`,`分隔来添加多个用户

### 危险区
- `bot.command_sync_mode` 
  - 控制命令执行是否同步执行（在主线程上串行执行）
  - **注意：将此选项更改为`true`可能导致线程阻塞等问题**
  - 除非你确信自己无法保证命令执行器的线程安全性，或者你知道自己在做什么，否则建议将其保持为默认的`false`值