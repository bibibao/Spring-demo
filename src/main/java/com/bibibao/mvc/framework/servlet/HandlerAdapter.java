package com.bibibao.mvc.framework.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * All rights Reserved, Designed By www.coderhu.cn
 *
 * @version V1.0
 * @Title: HandlerAdapter
 * @Package com.bibibao.mvc.framework.servlet
 * @Description: 方法适配器
 * @author: bibibao
 * @date: 2020/2/5 19:40
 * @Copyright: 2020 www.coderhu.cn Inc. All rights reserved.
 * 注意：本内容为个人练习专用
 */
public class HandlerAdapter {

    private Map<String,Integer> paramMapping;

    public HandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }


    /**
     * 通过反射调用url对应的method
     *
     *
     * @param req
     * @param rsp
     * @param handler
     * @return
     */
    public ModeAndView handle(HttpServletRequest req, HttpServletResponse rsp,Handler handler) throws Exception {
//        为什么要传req,为什么要传rsp,为什么要传handler？
        Class<?>[] paramTypes = handler.method.getParameterTypes();

//        要想给参数赋值，只能通过索引号来找到具体的某个参数
        Object[] paramValues = new Object[paramTypes.length];

        Map<String,String[]> params = req.getParameterMap();

        for (Map.Entry<String,String[]> param : params.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
            if (!this.paramMapping.containsKey(param.getKey())){
                continue;
            }
            int index = this.paramMapping.get(param.getKey());
//            单个赋值是不行的
            paramValues[index] = castStringValue(value,paramTypes[index]);
        }

//        request和response要赋值
        String reqName = HttpServletRequest.class.getName();
        if (this.paramMapping.containsKey(reqName)){
            int reqIndex = this.paramMapping.get(reqName);
            paramValues[reqIndex] = req;
        }
        String rspName = HttpServletResponse.class.getName();
        if (this.paramMapping.containsKey(rspName)){
            int rspIndex = this.paramMapping.get(rspName);
            paramValues[rspIndex] = rsp;
        }

//        执行方法
        Object result = handler.method.invoke(handler.controller, paramValues);
//        判断方法返回类型是否是指定的modelAndView实体类型
        boolean isModelAndView = handler.method.getReturnType() == ModeAndView.class;

        if (isModelAndView){
            return (ModeAndView) result;
        }

        return null;
    }


    /**
     * 转成String类型
     *
     * @param value
     * @param clazz
     * @return
     */
    private Object castStringValue(String value,Class<?> clazz){

        if (clazz == String.class){
            return value;
        }

        if (clazz == Integer.class){
            return Integer.valueOf(value);
        }

        if (clazz == int.class){
            return Integer.valueOf(value).intValue();
        }

        return null;
    }
}
