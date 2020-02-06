package com.bibibao.mvc.demo.service.impl;

import com.bibibao.mvc.demo.service.NameService;
import com.bibibao.mvc.demo.service.UserService;
import com.bibibao.mvc.framework.annotation.Autowired;
import com.bibibao.mvc.framework.annotation.Service;

/**
 * All rights Reserved, Designed By www.coderhu.cn
 *
 * @version V1.0
 * @Title: NameServiceImpl
 * @Package com.bibibao.mvc.demo.service.impl
 * @Description: 简单描述下这个类是做什么用的
 * @author: bibibao
 * @date: 2020/2/5 14:06
 * @Copyright: 2020 www.coderhu.cn Inc. All rights reserved.
 * 注意：本内容为个人练习专用
 */
@Service("bibibao")
public class NameServiceImpl implements NameService {
    @Autowired
    private UserService userService;

    @Override
    public void doSomething() {
        userService.sayHello();
    }
}
