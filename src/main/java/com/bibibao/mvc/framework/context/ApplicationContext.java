package com.bibibao.mvc.framework.context;

import com.bibibao.mvc.framework.annotation.Autowired;
import com.bibibao.mvc.framework.annotation.Controller;
import com.bibibao.mvc.framework.annotation.Service;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All rights Reserved, Designed By www.coderhu.cn
 *
 * @version V1.0
 * @Title: ApplicationContext
 * @Package com.bibibao.mvc.framework.context
 * @Description: ApplicationContext
 * @author: bibibao
 * @date: 2020/2/5 14:10
 * @Copyright: 2020 www.coderhu.cn Inc. All rights reserved.
 * 注意：本内容为个人练习专用
 */
public class ApplicationContext {

    private Map<String,Object> instanceMapping = new ConcurrentHashMap<String, Object>();

    private List<String> classCache = new ArrayList<String>();

    private Properties config = new Properties();

    public ApplicationContext(String location) {
        InputStream is = null;
        try {
//            定位
            is = this.getClass().getClassLoader().getResourceAsStream(location);
//            载入
            config.load(is);
//            注册，把所有class找出来存着
            String packageName = config.getProperty("scanPackage");
            doRegister(packageName);
//            实例化需要ioc的对象（就是加了@Service,@Controller），只要循环class就可以了
            doCreateBean();
//            注入
            populate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("容器初始化已完成");
    }

    /**
     * 把符合条件所有的class全部找出来，注册到缓存里面去
     *
     * @param packageName
     */
    private void doRegister(String packageName){
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File dir = new File(url.getFile());
        for (File file: dir.listFiles()
             ) {
//            如果是一个文件夹，继续递归
            if(file.isDirectory()){
                doRegister(packageName+"."+file.getName());
            }else {
                classCache.add(packageName+"."+file.getName().replace(".class","").trim());
            }
        }
    }

    /**
     * bean初始化，并放入map中
     */
    private void doCreateBean() {
//        检查看有没有注册信息，注册信息里面保存了所有的class名字
//        BeanDefinition保存了类的名字，也保存类和类之间的关系(Map/list/Set/ref/parent)
//        processArray
        if (classCache.size() == 0){
            return;
        }

        try {
            for (String className : classCache){
                Class<?> clazz = Class.forName(className);
//                根据类是否被特定注解标记来判断哪些类需要初始化
                if (clazz.isAnnotationPresent(Controller.class)){
//                    名字默认是首字母小写
                    String id = lowerFirstChar(clazz.getSimpleName());
                    instanceMapping.put(id,clazz.newInstance());
                    continue;
                }
                if (clazz.isAnnotationPresent(Service.class)){
                    Service service = clazz.getAnnotation(Service.class);
//                    如果设置了自定义名字，就优先用他自己定义的名字
                    String id = service.value();
                    if (!"".equals(id.trim())){
                        instanceMapping.put(id,clazz.newInstance());
                        continue;
                    }

//                    如果是空的，就用默认规则
//                    1、类名首字母小写
//                    如果这个类是接口
//                    可以根据类型类匹配
                    Class<?>[] interfaces = clazz.getInterfaces();
//                    如果这个类实现了接口，就用接口的类型作为Id
                    for (Class<?> i : interfaces){
                        instanceMapping.put(i.getName(),clazz.newInstance());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 依赖注入
     */
    private void populate(){
//        首先要判断IOC容器中有没有东西
        if (instanceMapping.isEmpty()){
            return;
        }
        for (Map.Entry<String,Object> entry : instanceMapping.entrySet()){
//            把所有的属性全部取出来，包括私有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field: fields
                 ) {
                if (!field.isAnnotationPresent(Autowired.class)){
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);
                String id = autowired.value().trim();
//                如果id为空，也就是说，自己没有设置，默认根据类型来注入
                if ("".equals(id)){
                    id = field.getType().getName();
                }
//                把私有变量开放访问权限
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(),instanceMapping.get(id));
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private String lowerFirstChar(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Map<String,Object> getAll(){
        return instanceMapping;
    }

    public Properties getConfig(){
        return config;
    }


}
