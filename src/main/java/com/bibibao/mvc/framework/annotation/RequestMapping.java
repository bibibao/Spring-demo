package com.bibibao.mvc.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * All rights Reserved, Designed By www.coderhu.cn
 *
 * @version V1.0
 * @Title: RequestMapping
 * @Package com.bibibao.mvc.framework.annotation
 * @Description: 简单描述下这个类是做什么用的
 * @author: bibibao
 * @date: 2020/2/5 13:55
 * @Copyright: 2020 www.coderhu.cn Inc. All rights reserved.
 * 注意：本内容为个人练习专用
 */
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RequestMapping {
    String value() default "";
}
