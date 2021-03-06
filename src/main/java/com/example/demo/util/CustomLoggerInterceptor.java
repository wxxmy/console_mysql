package com.example.demo.util;

import ch.qos.logback.classic.Logger;
import com.alibaba.druid.sql.SQLUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.helpers.BasicMarker;

/**
 * desc:
 *
 * @author : ckl
 * creat_date: 2019/12/31 0031
 * creat_time: 10:52
 **/
public class CustomLoggerInterceptor implements InvocationHandler {

    private static final ThreadLocal<SqlLogVO> SQL_LOG_VO_THREAD_LOCAL = new ThreadLocal<SqlLogVO>(){
        @Override
        protected SqlLogVO initialValue() {
            return new SqlLogVO();
        }
    };

    private static final Set<String> skipMethodSet = new HashSet<String>();

    static {
        skipMethodSet.add("isTraceEnabled");
        skipMethodSet.add("isDebugEnabled");
        skipMethodSet.add("isInfoEnabled");
        skipMethodSet.add("isWarnEnabled");
        skipMethodSet.add("isErrorEnabled");

    }

    private Logger logger;


    public static Set<String> methods = new LinkedHashSet<>();

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (logger.getName().startsWith("com.example.demo.dao")) {
            if (skipMethodSet.contains(method.getName())) {
                return method.invoke(logger,args);
            }

            /**
             * 先调用原始方法，获取返回值
             */
            Object orginResult = method.invoke(logger, args);

            String s = assemblyCompleteMybatisQueryLog(args);
            if (s != null) {
                // 格式化sql,换行显示
                String formatMySql = SQLUtils.formatMySql(s);
                Object[] objects = new Object[args.length];
                System.arraycopy(args,0,objects,0,args.length);

                for (int i = (objects.length - 1); i >= 0 ; i--) {
                    if (objects[i] != null && objects[i] instanceof String) {
                        objects[i] = formatMySql;
                        break;
                    }
                }
                return method.invoke(logger, objects);
            }

            return orginResult;
        }


        methods.add(method.getDeclaringClass() + "#" + method.getName());
        if (method.getName().equals("info")) {
            System.out.println();

        }
        return method.invoke(logger,args);
    }

    private String assemblyCompleteMybatisQueryLog(Object[] args) {
        if (args != null && args.length > 1) {
            if (!(args[0] instanceof BasicMarker)) {
                return null;
            }
            /**
             * marker不匹配，直接返回
             */
            BasicMarker arg = (BasicMarker) args[0];
            if (!Objects.equals(arg.getName(), "MYBATIS")) {
                return null;
            }

            String message = null;
            for (int i = (args.length - 1); i >= 0 ; i--) {
                if (args[i] != null && args[i] instanceof String) {
                    message = (String) args[i];
                    break;
                }
            }
            if (message == null) {
                return null;
            }
            if (message.startsWith("==>  Preparing:")) {
                String newMessage = message.substring("==>  Preparing:".length()).trim();
                SQL_LOG_VO_THREAD_LOCAL.get().setPrepareSqlStr(newMessage);
            } else if (message.startsWith("==> Parameters:")) {
                try {
                    return populateSqlWithParams(message);
                } catch (Exception e) {
                    logger.error("{}",e);
                }finally {
                    SQL_LOG_VO_THREAD_LOCAL.remove();
                }
            }
        }

        return null;
    }

    private String populateSqlWithParams(String message) {
        String s = message.substring("==> Parameters:".length()).trim();
        String[] params = s.split(",");
        if (params.length == 0) {
            SQL_LOG_VO_THREAD_LOCAL.remove();
            return null;
        }

        /**
         * 组装参数
         */
        LinkedHashMap<String, String> paramValueAndTypaMap = new LinkedHashMap<>();
        for (String param : params) {
            String[] paramValueAndType = param.split("\\(");
            if (paramValueAndType.length != 2) {
                continue;
            }
            String type = paramValueAndType[1];
            /**
             * 去掉右边的圆括号
             */
            String trimTheRightParenthesis = type.substring(0, type.length() - 1);
            paramValueAndTypaMap.put(paramValueAndType[0], trimTheRightParenthesis);
        }

        String prepareSqlStr = SQL_LOG_VO_THREAD_LOCAL.get().getPrepareSqlStr();
        if (prepareSqlStr == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : paramValueAndTypaMap.entrySet()) {
            String type = entry.getValue();
            String paramValue = entry.getKey();
            if ("String".equals(type)) {
                paramValue = "\"" + paramValue +  "\"";
            }


            prepareSqlStr = prepareSqlStr.replaceFirst("\\?",paramValue);
        }

//        System.out.println(prepareSqlStr);

        return prepareSqlStr;

    }
}
