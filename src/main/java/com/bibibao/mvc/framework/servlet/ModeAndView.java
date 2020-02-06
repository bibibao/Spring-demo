package com.bibibao.mvc.framework.servlet;

import java.util.Map;

/**
 * All rights Reserved, Designed By www.coderhu.cn
 *
 * @version V1.0
 * @Title: ModeAndView
 * @Package com.bibibao.mvc.framework.servlet
 * @Description: 简单描述下这个类是做什么用的
 * @author: bibibao
 * @date: 2020/2/5 14:11
 * @Copyright: 2020 www.coderhu.cn Inc. All rights reserved.
 * 注意：本内容为个人练习专用
 */
public class ModeAndView {

    /**
     * 页面模板
     */
    private String view;
    /**
     * 页面数据模型
     */
    private Map<String,Object> model;

    public ModeAndView(String view) {
        this.view = view;
    }

    public ModeAndView(String view, Map<String, Object> model) {
        this.view = view;
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
