package com.opay.algo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * @author cxy
 */
@Component
public class SpringBeanUtil implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(ApplicationContextAware.class);
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (SpringBeanUtil.applicationContext == null) {
            SpringBeanUtil.applicationContext = context;
        }
        logger.info("\r\nload applicationContext success");
    }

    /**
     * 获取applicationContext
     * */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     * */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     * */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        try {
            char[] cs = clazz.getSimpleName().toCharArray();
            // 首字母大写到小写
            cs[0] += 32;
            return (T) getApplicationContext().getBean(String.valueOf(cs));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     * */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}