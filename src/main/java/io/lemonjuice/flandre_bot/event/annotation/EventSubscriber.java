package io.lemonjuice.flandre_bot.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 所有带有此注解的类都将会被作为监听者注册到Bot框架的事件总线 <br>
 * <b>注意：应用此注解的类必须拥有一个<u>无参的构造函数</u></b>（默认情况下可以不显式声明）
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventSubscriber {
}
