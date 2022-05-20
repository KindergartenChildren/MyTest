package com.lc.user;

import com.lc.springframerwork.LcApplicationContext;
import com.lc.user.service.UserService;

/**
 * @author lc
 * @date 2022/5/20 22:55
 * @description TODO
 */
public class MyApplication {
    public static void main(String[] args) throws ClassNotFoundException {
        LcApplicationContext context = new LcApplicationContext(AppConfig.class);
        UserService bean = (UserService)context.getBean("userService");
        System.out.println(bean);
        bean.test();
    }
}
