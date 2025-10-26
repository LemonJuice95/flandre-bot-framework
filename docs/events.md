# 事件系统

框架内实现了一个**同步的**事件总线，可以通过订阅事件来实现部分的逻辑插入

## 目录
 - [使用方式](#使用方式)
 - [事件列表](#事件列表)

## 使用方式

事件订阅者基于注解进行注册，通过`@EventSubscriber`注解来声明一个订阅者类， 一个订阅者类需要有一个**无参**的构造函数（通常使用无需显式声明的默认构造函数即可）

在订阅类的内部，使用`@SubscribeEvent`注解来声明一个订阅事件的方法（不要使用`static`修饰），方法的名称**不会被关心**，方法要求**有且仅有一个参数**，参数类型为订阅的事件类型（`Event`的某个子类）

一个正确的事件订阅类看起来像这样：

```java
@EventSubscriber
public class SomeClass {
    @SubscribeEvent
    public void onSomeEvent(SomeEvent event) {
        //执行逻辑
    }
    
    @SubscribeEvent
    public void onCancelableEvent(SomeCancelableEvent event) {
        //执行逻辑
        if(someCondition()) {
            event.setCancelled(true); //部分事件可以被取消，能够阻断部分原有后续逻辑的执行
        }
    }
    
    //...
}
```

## 事件列表

WIP

文档尚未完成，请前往`io.lemonjuice.flandre_bot_framework.event`包查看事件列表，事件有javadoc进行说明