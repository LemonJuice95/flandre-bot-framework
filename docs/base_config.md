# 基础配置文件

配置文件位于运行目录下的`config/bot.properties`文件内，在程序检测到该文件不存在时将会自动释放

## bot.properties配置项

### 基础内容
- `bot.name`: String
  - bot的名称
- `bot.log_messages`: Boolean
  - 是否在日志中记录收到的消息


### 网络
- `bot.network.mode`: Integer
  - 应用端的连接模式
  - 0：Websocket正向连接
  - 1：Websocket反向连接
  - 默认值：0
- `bot.network.client.url`: String
  - **仅在正向连接（应用端作为客户端）模式下有效**
  - 实现端侧服务器的url
- `bot.network.server.host`: String
  - **仅在反向连接（应用端作为服务端）模式下有效**
  - 应用端服务器监听的ip
  - 默认值："0.0.0.0"
- `bot.network.server.port`: Integer
  - **仅在反向连接（应用端作为服务端）模式下有效**
  - 应用端服务器监听的端口
- `bot.network.token`: String
  - 连接附带的token（需要与实现端配置的token一致）

### 调试模式
- `bot.debug_mode`: Boolean
  - 是否启用调试模式
  - 默认值：`false`
- `bot.debug_users`: List\<Long\>
  - 拥有调试权限用户的qq号
  - 允许用`,`分隔来添加多个用户

### 实验性功能
- `bot.eventbus.bus_mode`: Enum
  - 决定内部事件总线采用的发布模式
  - 可用的值：`sync` `parralel` `async`（不区分大小写）
  - 默认值：`sync`
  - 模式说明：
  - `sync`同步模式，所有订阅者串行执行订阅方法
  - `parllel`并行模式，所有订阅者并发执行订阅方法，post端将等待所有订阅者方法返回
  - `async`异步模式，所有订阅者并发执行订阅方法，除可取消事件（实现`ICancellableEvent`）外，post端不会等待订阅者方法返回

### 危险区
- `bot.command_sync_mode`: Boolean
  - 控制命令执行是否同步执行（在一个单独的线程内串行执行）
  - **注意：将此选项更改为`true`可能导致线程阻塞等问题**
  - 并且由于同步模式仅将**命令执行**的逻辑放入同一线程内，对于应用中的其他线程而言，其操作可能仍然不安全
  - 除非你确信自己无法保证命令执行器的线程安全性，或者你知道自己在做什么，否则建议将其保持为默认的`false`值
  - 默认值：`false` 
- `bot.eventbus.subscribers.scan_classpath`: Boolean
  - 决定事件订阅类在初始化时的发现方式
  - **将此配置项更改为`true`时将在一定程度上增加bot的启动时长**
  - 如果你不知道这是什么，建议将其保持为`false`
  - 在`flandre-bot-framework v0.30.0`以后，框架使用了编译时注解处理器来处理所有使用注解声明的订阅者类，并使用SPI机制在运行时发现它们并注册，以此优化启动速度
  - 在`v0.30.0`以前，框架主要使用**全量扫描类路径**的方式来发现所有的订阅者类
  - 因此，当此配置项为`true`时，框架将回退到`v0.30.0`以前的发现机制，在启动时进行一次类路径全量扫描
  - 在注解处理器与SPI未正常工作时，可将此配置项更改为`true`作为应急的兜底策略
  - 默认值：`false`