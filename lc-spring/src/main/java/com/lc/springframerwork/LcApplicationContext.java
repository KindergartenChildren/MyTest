package com.lc.springframerwork;

import com.lc.user.AppConfig;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lc
 * @date 2022/5/20 22:56
 * @description 手写模拟Spring容器
 * 知识点：
 *      单例bean:
 *          懒加载：getBean的时候才去创建bean
 *          非懒加载：直接创建bean
 *      多例bean：
 *          每次调用getBean的时候都去创建bean
 */
public class LcApplicationContext {

    private Map<String,BeanDefinition> beanDefinedMap = new HashMap<>();
    private Map<String,Object> singletonPool = new HashMap<>();

    public LcApplicationContext(Class<AppConfig> appConfigClass) throws ClassNotFoundException {
        //扫描并加载beanName和BeanDefinition;
        scanClass(appConfigClass);

        for (String beanName : beanDefinedMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinedMap.get(beanName);
            /**
             * 只创建 非懒加载的单例bean 。
             */
            if ( beanDefinition.getScope().equals("singleton") && !beanDefinition.isLazy()) {
                Object bean = createBean(beanName,beanDefinition);
                singletonPool.put(beanName,bean);
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class type = beanDefinition.getType();
        try {

            Object bean = type.newInstance();

            //获得bean所有属性进行遍历
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object o = getBean(field.getName());
                    field.setAccessible(true);
                    field.set(bean,o);
                }
            }
            return bean;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scanClass(Class<AppConfig> appConfigClass) throws ClassNotFoundException {
        /**
         * 在运行时，会生成对应的class文件，通过类加载器找到对应的目录
         */
        //拿到注解扫描的地址
        if (appConfigClass.isAnnotationPresent(ComponentScan.class)) {
            String value = appConfigClass.getAnnotation(ComponentScan.class).value();
            String resource = value.replace(".","/");

            //通过类加载器，把地址加载成文件夹
            ClassLoader classLoader = appConfigClass.getClassLoader();
            URL url = classLoader.getResource(resource);
            File files = new File(url.getFile());

            //如果是文件夹，接着往下找，如果是文件，查看它有没有扫描的注解
            List<File> fileList = new ArrayList<>();
            fileList = getFile(files,fileList);
            for (File bean: fileList) {
                // 获得文件的绝对路径
                String absolutePath = bean.getAbsolutePath();
                String classPath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class")).replace("\\",".");
                //通过类加载器将class文件加载成bean
                Class<?> clazz = classLoader.loadClass(classPath);
                if(clazz.isAnnotationPresent(Component.class)){
                    /**
                     * 思路 ： 加载bean要什么属性， beanClass , beanScope 作用域,is Lazy 是否懒加载
                     *
                     * （懒加载， 非懒加载 ）单例 ，多例
                     */
                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setType(clazz);
                    if (clazz.isAnnotationPresent(Scope.class)) {
                        beanDefinition.setScope(clazz.getAnnotation(Scope.class).value());
                    } else {
                        beanDefinition.setScope("singleton");
                    }
                    beanDefinition.setLazy(clazz.isAnnotationPresent(Lazy.class));

                    /**
                     * bean的属性我们已经构建好了，接下来要把它放到Map当中,为了构建bean使用
                     */
                    String beanName = clazz.getAnnotation(Component.class).value();
                    if (beanName.isEmpty()) {
                        //处理beanName
                        beanName = Introspector.decapitalize(clazz.getSimpleName());
                    }
                    beanDefinedMap.put(beanName,beanDefinition);
                }
            }
        }
    }

    private List<File> getFile(File files, List<File> fileList){
        for (File file : files.listFiles()) {
            //如果是文件夹，继续往下找
            if( file.isDirectory()){
                getFile(file, fileList);
            } else {
                fileList.add(file);
            }
        }
        return fileList;
    }

    /**
     * @param beanName
     * @return
     * 获取bean的时候，根据类型判断，如果是单例，去单例池拿，如果是多例，则直接创建
     */
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinedMap.get(beanName);
        if(beanDefinition==null){
            throw new NullPointerException("beanName啥也没有");
        }
        String scope = beanDefinition.getScope();
        if(scope.equals("singleton")){
            Object bean = singletonPool.get(beanName);
            //懒加载情况下为空
            if(bean==null){
                bean = createBean(beanName,beanDefinition);
                singletonPool.put(beanName,bean);
            }
            return bean;
        }else {
            return createBean(beanName,beanDefinition);
        }
    }
}
