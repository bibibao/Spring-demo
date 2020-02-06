package com.bibibao.mvc.framework.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * All rights Reserved, Designed By www.coderhu.cn
 *
 * @version V1.0
 * @Title: Handler
 * @Package com.bibibao.mvc.framework.servlet
 * @Description: 简单描述下这个类是做什么用的
 * @author: bibibao
 * @date: 2020/2/5 19:40
 * @Copyright: 2020 www.coderhu.cn Inc. All rights reserved.
 * 注意：本内容为个人练习专用
 */
public class Handler {
    protected Object controller;
    protected Method method;
    protected Pattern pattern;

    public Handler(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
