package com.bibibao.mvc.framework.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All rights Reserved, Designed By www.coderhu.cn
 *
 * @version V1.0
 * @Title: ViewResolver
 * @Package com.bibibao.mvc.framework.servlet
 * @Description: 视图解析器
 * @author: bibibao
 * @date: 2020/2/5 19:43
 * @Copyright: 2020 www.coderhu.cn Inc. All rights reserved.
 * 注意：本内容为个人练习专用
 */
public class ViewResolver {

    private String viewName;
    private File file;

    public ViewResolver(String viewName, File file) {
        this.viewName = viewName;
        this.file = file;
    }

    public String parse(ModeAndView mv) throws Exception {

        StringBuffer stringBuffer = new StringBuffer();

        RandomAccessFile ra = new RandomAccessFile(this.file, "r");
//      模板框架的语法是非常复杂，但是，原理是一样的
//      无非都是用正则表达式来处理字符串而已
        try {
            String line = null;
            while (null != (line = ra.readLine())) {
                Matcher matcher = matcher(line);
                while (matcher.find()) {
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        String paramName = matcher.group(i);
                        Object paramValue = mv.getModel().get(paramName);
                        if (null == paramValue){
                            continue;
                        }
                        line = line.replaceAll("@\\{"+paramName+"}",paramValue.toString());
                    }
                }
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ra.close();
        }
        return stringBuffer.toString();
    }

    private Matcher matcher(String str) {
        Pattern pattern = Pattern.compile("@\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    public String getViewName() {
        return viewName;
    }
}
