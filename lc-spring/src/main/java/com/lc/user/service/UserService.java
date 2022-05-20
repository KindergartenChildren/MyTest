package com.lc.user.service;


import com.lc.springframerwork.Autowired;
import com.lc.springframerwork.Component;

/**
 * @author lc
 * @date 2022/5/20 23:02
 * @description TODO
 */
@Component
public class UserService {

    /**
     * 注解的type要指定可以注释在字段上
     */
    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println("牛逼，真tm不容易");
        System.out.println(orderService);
    }
}
