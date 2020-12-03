package com.example.demo.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextSelector;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * desc:
 *
 * @author : ckl
 * creat_date: 2019/12/31 0031
 * creat_time: 10:24
 **/
public class CustomDefaultContextSelector implements ContextSelector, MethodInterceptor {

    private LoggerContext defaultLoggerContext;

    private LoggerContext proxyedDefaultLoggerContext;

    private static ConcurrentHashMap<String, org.slf4j.Logger> cachedLogger = new ConcurrentHashMap<>(1000);


    public CustomDefaultContextSelector(LoggerContext context) {
        this.defaultLoggerContext = context;
    }

    @Override
    public LoggerContext getLoggerContext() {
        return getDefaultLoggerContext();
    }

    @Override
    public LoggerContext getDefaultLoggerContext() {
        if (proxyedDefaultLoggerContext == null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(defaultLoggerContext.getClass());
            enhancer.setCallback(this);
            proxyedDefaultLoggerContext = (LoggerContext) enhancer.create();
        }
        return proxyedDefaultLoggerContext;
    }

    @Override
    public LoggerContext detachLoggerContext(String loggerContextName) {
        return defaultLoggerContext;
    }

    @Override
    public List<String> getContextNames() {
        return Collections.singletonList(defaultLoggerContext.getName());
    }

    @Override
    public LoggerContext getLoggerContext(String name) {
        if (defaultLoggerContext.getName().equals(name)) {
            return defaultLoggerContext;
        } else {
            return null;
        }
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object result;
        result = methodProxy.invokeSuper(o,args);
        if (Objects.equals(method.getReturnType().getName(), org.slf4j.Logger.class.getName()) && Objects.equals(method.getName(), "getLogger")) {
            org.slf4j.Logger logger = (org.slf4j.Logger) result;
            String loggerName = logger.getName();

            /**
             * 只关心mybatis层的logger，mybatis层的logger的包名，我们这边是固定的包下面
             * 如果不是这个包下的，直接返回
             */
            if (!loggerName.startsWith("com.example.demo.dao")) {
                return result;
            }

            /**
             * 对mybatis mapper的log，需要进行代理；代理后的对象，我们暂存一下，免得每次都创建代理对象
             * 从缓存获取代理logger
             */
            if (cachedLogger.get(loggerName) != null) {
                return cachedLogger.get(loggerName);
            }

            CustomLoggerInterceptor customLoggerInterceptor = new CustomLoggerInterceptor();
            customLoggerInterceptor.setLogger((Logger) result);
            Object newProxyInstance = Proxy.newProxyInstance(result.getClass().getClassLoader(), result.getClass().getInterfaces(), customLoggerInterceptor);

            cachedLogger.put(loggerName, (org.slf4j.Logger) newProxyInstance);

            return newProxyInstance;
        }

        return result;
    }

    public static ConcurrentHashMap<String, org.slf4j.Logger> getCachedLogger() {
        return cachedLogger;
    }
}