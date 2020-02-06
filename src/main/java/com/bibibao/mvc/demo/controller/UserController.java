package com.bibibao.mvc.demo.controller;

import com.bibibao.mvc.demo.service.NameService;
import com.bibibao.mvc.framework.annotation.Autowired;
import com.bibibao.mvc.framework.annotation.Controller;
import com.bibibao.mvc.framework.annotation.RequestMapping;
import com.bibibao.mvc.framework.annotation.RequestParam;
import com.bibibao.mvc.framework.servlet.ModeAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.coderhu.cn
 *
 * @version V1.0
 * @Title: UserController
 * @Package com.bibibao.mvc.demo.controller
 * @Description: controller
 * @author: bibibao
 * @date: 2020/2/5 13:46
 * @Copyright: 2020 www.coderhu.cn Inc. All rights reserved.
 * 注意：本内容为个人练习专用
 */
@Controller
@RequestMapping("/web")
public class UserController {
    @Autowired
    private NameService nameService;

    @RequestMapping("/query/bibibao")
    public ModeAndView query(HttpServletRequest request, HttpServletResponse rsponse, @RequestParam(value = "name",required = false) String name,@RequestParam("addr")String addr){
        Map<String, Object> model = new HashMap<String, Object>();
        nameService.doSomething();
        model.put("name",name);
        model.put("addr",addr);
        return new ModeAndView("first.bbml",model);
    }

    @RequestMapping("/add")
    public ModeAndView add(HttpServletResponse response,String str){
        out(response,str);
        return null;
    }

    private void out(HttpServletResponse response,String str){
        try {
            response.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
