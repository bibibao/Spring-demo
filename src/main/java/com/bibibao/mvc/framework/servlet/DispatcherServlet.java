package com.bibibao.mvc.framework.servlet;

import com.bibibao.mvc.framework.annotation.Controller;
import com.bibibao.mvc.framework.annotation.RequestMapping;
import com.bibibao.mvc.framework.annotation.RequestParam;
import com.bibibao.mvc.framework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All rights Reserved, Designed By www.coderhu.cn
 *
 * @version V1.0
 * @Title: DispatcherServlet
 * @Package com.bibibao.mvc.framework.servlet
 * @Description: 简单描述下这个类是做什么用的
 * @author: bibibao
 * @date: 2020/2/5 13:25
 * @Copyright: 2020 www.coderhu.cn Inc. All rights reserved.
 * 注意：本内容为个人练习专用
 */
public class DispatcherServlet extends HttpServlet {

    private static final String LOCALTION = "contextConfigLocation";

    private List<Handler> handlerMapping = new ArrayList<Handler>();

    private Map<Handler, HandlerAdapter> adapterMapping = new HashMap<Handler, HandlerAdapter>();

    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

    /**
     * 初始化ioc容器
     *
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
//        IOC容器必须要先初始化
//        假装容器已启动
        ApplicationContext context = new ApplicationContext(config.getInitParameter(LOCALTION));

//        请求解析
        this.initMultipartResolver(context);
//        多语言、国际化
        this.initMultipartResolver(context);
//        主题view层
        this.initThemeResolver(context);
//        解析url和Method的关联关系
        this.initHandlerMappings(context);
//        适配器匹配过程
        this.initHandlerAdapters(context);
//        异常解析
        this.initHandlerExceptionResolvers(context);
//        视图转发（根据视图名字匹配到一个具体模板）
        this.initRequestToViewNameTranslator(context);
//        解析模板中的内容
        this.initViewResolvers(context);
//        初始化渲染
        this.initFlashMapManager(context);
        System.out.println("Spring MVC is init.");


    }

    /**
     * 请求解析
     *
     * @param context
     */
    private void initMultipartResolver(ApplicationContext context) {

    }

    /**
     * 多语言、国际化
     *
     * @param context
     */
    private void initLocaleResolver(ApplicationContext context) {

    }

    /**
     * 主题view层
     *
     * @param context
     */
    private void initThemeResolver(ApplicationContext context) {

    }

    /**
     * 解析url和Method的关联关系
     *
     * @param context
     */
    private void initHandlerMappings(ApplicationContext context) {
        Map<String, Object> ioc = context.getAll();
        if (ioc.isEmpty()) {
            return;
        }
//        只要是由Controller修饰类，里面方法全部找出来
//        而且这个方法上应该要加RequestMapping注解，如果没加这个注解，这个方法是不能被外界访问的
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            String url = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                url = requestMapping.value();
            }
//            扫描Controller下面的所有方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = (url + requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                handlerMapping.add(new Handler(entry.getValue(), method, pattern));
                System.out.println("Mapping:" + regex + " " + method.toString());
            }

        }

    }


    /**
     * 适配器匹配过程
     * 主要是用来动态匹配我们参数的
     *
     * @param context
     */
    private void initHandlerAdapters(ApplicationContext context) {

        if (handlerMapping.isEmpty()) {
            return;
        }
        Map<String, Integer> paramMapping;
//        只需要取出来具体的某个方法
        for (Handler handler : handlerMapping) {
//          参数类型作为key，参数索引号作为值
            paramMapping = new HashMap<String, Integer>();
//            把这个方法上面所有的参数全部获取到
            Class<?>[] parameterTypes = handler.method.getParameterTypes();
//            只能通过参数顺序，无法通过反射，因为无法拿到我们的参数的名字
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                if (type == HttpServletRequest.class || type == HttpServletResponse.class){
                    paramMapping.put(type.getName(),i);
                }
            }

//            这里是匹配Request和Response
            Annotation[][] parameterAnnotations = handler.method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof RequestParam){
                        String paramName = ((RequestParam) annotation).value();
                        if (!"".equals(paramName.trim())){
                            paramMapping.put(paramName,i);
                        }
                    }
                }

            }
            adapterMapping.put(handler,new HandlerAdapter(paramMapping));
        }

    }

    /**
     * 异常解析
     *
     * @param context
     */
    private void initHandlerExceptionResolvers(ApplicationContext context) {

    }

    /**
     * 视图转发（根据视图名字匹配到一个具体模板）
     *
     * @param context
     */
    private void initRequestToViewNameTranslator(ApplicationContext context) {

    }

    /**
     * 解析模板中的内容（拿到服务器传过来的数据，生成Html代码）其实就是一般的文件，生成文件名和文件的映射关系
     *
     * @param context
     */
    private void initViewResolvers(ApplicationContext context) {
//        模板一般是不会放到WebRoot下的，而是放在WEB-INF，或者classes下
//        这样就避免了用户直接请求到模板
//        加载模板的个数，存储到缓存中
//        检查模板中的语法错误
        String templateRoot = context.getConfig().getProperty("templateRoot");
//        归根到底就是一个文件，普通文件
        String rootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File rootDir = new File(rootPath);
        for (File file : rootDir.listFiles()) {
            viewResolvers.add(new ViewResolver(file.getName(),file));
        }

    }


    private void initFlashMapManager(ApplicationContext context) {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        在这里调用自己写的Controller的方法
        try {
            this.doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception, Msg :"+ Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * 调度
     *
     * @param req
     * @param resp
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//        先取出来一个Hanlder,从HandlerMapping取
        Handler handler = this.getHandler(req);
        if (handler == null){
            resp.getWriter().write("404 Not Found");
            return;
        }

//        再取出一个适配器
        HandlerAdapter handlerAdapter = this.getHandlerAdapter(handler);
//        再由适配去调用我们的具体方法
        ModeAndView modeAndView = handlerAdapter.handle(req, resp, handler);
//        进行数据填充并转换字符串
        this.applyDefaultViewName(resp,modeAndView);

    }

    private void applyDefaultViewName(HttpServletResponse resp,ModeAndView modeAndView) throws Exception {
        if (modeAndView == null) {
            return;
        }
        if (viewResolvers.isEmpty()) {
            return;
        }
        for (ViewResolver viewResolver : viewResolvers) {
            if (!modeAndView.getView().equals(viewResolver.getViewName())) {
                continue;
            }
            String result = viewResolver.parse(modeAndView);
            if (result != null){
                resp.getWriter().write(result);
                break;
            }
        }


    }

    /**
     * 获取handler
     *
     * @param req
     * @return
     */
    private Handler getHandler(HttpServletRequest req) {
        if (this.handlerMapping.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url.replace(contextPath, "").replaceAll("/+", "/");
//        循环handlerMapping
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.pattern.matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    /**
     * 获取handler适配器
     *
     * @param handler
     * @return
     */
    private HandlerAdapter getHandlerAdapter(Handler handler) {
        if (adapterMapping.isEmpty()) {
            return null;
        }
        return adapterMapping.get(handler);
    }

}
