# 命令系统

## 目录
  - [命令执行器](#命令执行器CommandRunner类)
  - [命令注册器](#命令注册器commandregister类)

## 命令执行器（CommandRunner类）
命令执行器是整个命令系统的基础，bot的命令依靠此类的子类来执行

命令默认并行执行，但该类中的所有非`static`成员变量都不会导致线程安全问题，
但需保证在类内部的方法中对于共享状态进行读/写时操作的原子性
（除非在配置文件中开启了同步执行模式）

（注：由于同步模式实际上只是将所有的命令在一个单独的线程串行执行，对于应用中的其他线程而言，其操作可能仍然不安全）

### 基类方法
- 构造器
  - 传入一个`Message`类型参数，代表用户发送的消息
- `getType()`
  - 返回命令类型的枚举值（`CommandRunner.Type`类）
  - Type类共有三个成员
    - `GROUP`（群聊命令）
    - `PRIVATE`（私聊命令）（包括临时会话与好友）
    - `FRIEND`（好友命令）（仅限好友使用）
- `getPermissionLevel()`
  - 返回执行命令所需的权限（有关原生的权限系统参见[这里](permissions.md#原生权限permissionlevel枚举类)）
- `matches()`
  - 判断用户消息是否与匹配该命令
- `apply()`
  - 执行具体的命令逻辑
- `blockAfterCommands()`
  - 当前命令执行后，是否使当前消息停止匹配后续的命令执行器

### 实用性子类
- `GroupCommandRunner`
  - 群聊命令的基类（覆写`getType()`方法，返回`Type.GROUP`)
- `SimpleGroupCommandRunner`
  - 在群聊中直接匹配整串命令
  - 由`getCommandBody()`方法设置命令体
  - 由`needAtFirst()`方法设置是否需要先`@Bot`再键入命令
- `MultiSimpleGroupCommandRunner`
  - 直接匹配整串命令，但支持多种命令串
- `PrivateCommandRunner`
  - 私聊命令的基类（覆写`getType()`方法）
  - 通过`needBeFriends()`方法控制是否需要添加bot为好友
- `SimplePrivateCommandRunner`
  - 大体上与`SimpleGroupCommandRunner`相似，只是缺少了`needAtFirst()`方法
- `MultiSimplePrivateCommandRunner`
  - 直接匹配整串命令，但支持多种命令串

## 命令注册器（CommandRegister类）
用于注册命令执行器

使用方式

1. 首先，获取一个注册器的实例
```java
public static final CommandRegister COMMANDS = new CommandRegister();
```

2. 注册命令执行器构造器的方法引用
```java
static {
  COMMANDS.register(ExampleCommandRunner::new);
}
```

3. 在Bot初始化时，加载命令注册器
```java
@EventSubscriber
public class ExampleBot {
    @SubscribeEvent
    public void init(BotInitEvent event) {
        ExampleCommandRegister.COMMANDS.load();
    }
}
```