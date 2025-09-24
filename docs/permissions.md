# 权限系统
权限系统控制着命令执行所需的权限，
应用于[此处](commands.md#基类方法)

## 目录
- [IPermissionLevel类](#ipermissionlevel类)
- [原生权限](#原生权限permissionlevel枚举类)

## IPermissionLevel类
权限类的基类

### 方法
 - `validatePermission(Message message)`
   - 传入的参数为用户发送的消息本身
   - 返回消息是否符合该权限的要求

## 原生权限（PermissionLevel枚举类）
框架提供了四种默认的权限

- `NORMAL`
  - 任意用户都可以使用
- `ADMIN`
  - 仅限群管理或群主可以使用
- `OWNER`
  - 仅限群主使用
- `DEBUG`
  - 最高权限，可以使用`NORMAL`至`OWNER`权限控制的所有命令
  - 需要在配置文件中设置调试模式为开启状态的情况下才生效
  - 在配置文件中写入的qq号将拥有这一权限

注意：对于私聊命令，`NORMAL`至`OWNER`的权限将不会生效（`validatePermission`方法总是返回true）