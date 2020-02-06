package com.bibibao.mvc.framework.annotation;

import java.lang.annotation.*;

/**
 * All rights Reserved, Designed By www.coderhu.cn
 *
 * @version V1.0
 * @Title: Autowired
 * @Package com.bibibao.mvc.framework.annotation
 * @Description: 自动注入注解
 * @author: bibibao
 * @date: 2020/2/5 13:47
 * @Copyright: 2020 www.coderhu.cn Inc. All rights reserved.
 * 注意：本内容为个人练习专用
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    String value() default "";
}
