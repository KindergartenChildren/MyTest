package com.lc.springframerwork;

/**
 * @author lc
 * @date 2022/5/20 23:37
 * @description TODO
 */
public class BeanDefinition {

    Class type;
    String scope;
    boolean isLazy;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }
}
