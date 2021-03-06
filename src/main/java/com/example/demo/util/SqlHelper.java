package com.example.demo.util;

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.lang.reflect.Field;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mybatis - ??????Mybatis??????sql??????
 *
 * @author liuzh/abel533/isea533
 */
public class SqlHelper {

	private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();

	/**
	 * ??????????????????sql
	 */
	public static String getMapperSql(Object mapper, String methodName, Object... args) {
		MetaObject metaObject = SystemMetaObject.forObject(mapper);
		SqlSession session = (SqlSession) metaObject.getValue("h.sqlSession");
		Class mapperInterface = (Class) metaObject.getValue("h.mapperInterface");
		String fullMethodName = mapperInterface.getCanonicalName() + "." + methodName;
		if (args == null || args.length == 0) {
			return getNamespaceSql(session, fullMethodName, null);
		} else {
			return getMapperSql(session, mapperInterface, methodName, args);
		}
	}

	/**
	 * ??????Mapper???????????????sql
	 */
	public static String getMapperSql(SqlSession session, String fullMapperMethodName,
			Object... args) {
		if (args == null || args.length == 0) {
			return getNamespaceSql(session, fullMapperMethodName, null);
		}
		String methodName = fullMapperMethodName.substring(fullMapperMethodName.lastIndexOf('.') + 1);
		Class mapperInterface = null;
		try {
			mapperInterface = Class
					.forName(fullMapperMethodName.substring(0, fullMapperMethodName.lastIndexOf('.')));
			return getMapperSql(session, mapperInterface, methodName, args);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("??????" + fullMapperMethodName + "?????????");
		}
	}

	/**
	 * ??????Mapper??????????????????
	 */
	public static String getMapperSql(SqlSession session, Class mapperInterface, String methodName,
			Object... args) {
		String fullMapperMethodName = mapperInterface.getCanonicalName() + "." + methodName;
		if (args == null || args.length == 0) {
			return getNamespaceSql(session, fullMapperMethodName, null);
		}
		Method method = getDeclaredMethods(mapperInterface, methodName);
		Map params = new HashMap();
		final Class<?>[] argTypes = method.getParameterTypes();
		for (int i = 0; i < argTypes.length; i++) {
			if (!RowBounds.class.isAssignableFrom(argTypes[i]) && !ResultHandler.class
					.isAssignableFrom(argTypes[i])) {
				String paramName = "param" + String.valueOf(params.size() + 1);
				paramName = getParamNameFromAnnotation(method, i, paramName);
				params.put(paramName, i >= args.length ? null : args[i]);
			}
		}
		if (args != null && args.length == 1) {
			Object _params = wrapCollection(args[0]);
			if (_params instanceof Map) {
				params.putAll((Map) _params);
			}
		}
		return getNamespaceSql(session, fullMapperMethodName, params);
	}


	/**
	 * ??????????????????????????????sql
	 */
	public static String getNamespaceSql(SqlSession session, String namespace) {
		return getNamespaceSql(session, namespace, null);
	}

	/**
	 * ??????????????????????????????sql
	 */
	public static String getNamespaceSql(SqlSession session, String namespace, Object params) {
		params = wrapCollection(params);
		Configuration configuration = session.getConfiguration();
		MappedStatement mappedStatement = configuration.getMappedStatement(namespace);
		TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration()
				.getTypeHandlerRegistry();
		BoundSql boundSql = mappedStatement.getBoundSql(params);
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		String sql = boundSql.getSql();
		if (parameterMappings != null) {
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value;
					String propertyName = parameterMapping.getProperty();
					if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (params == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(params.getClass())) {
						value = params;
					} else {
						MetaObject metaObject = configuration.newMetaObject(params);
						value = metaObject.getValue(propertyName);
					}
					JdbcType jdbcType = parameterMapping.getJdbcType();
					if (value == null && jdbcType == null) {
						jdbcType = configuration.getJdbcTypeForNull();
					}
					sql = replaceParameter(sql, value, jdbcType, parameterMapping.getJavaType());
				}
			}
		}
		return sql;
	}

	/**
	 * ???????????????????????? ???????????????????????????????????????????????????????????????????????????????????????????????????
	 */
	private static String replaceParameter(String sql, Object value, JdbcType jdbcType,
			Class javaType) {
		String strValue = String.valueOf(value);
		if (jdbcType != null) {
			switch (jdbcType) {
				//??????
				case BIT:
				case TINYINT:
				case SMALLINT:
				case INTEGER:
				case BIGINT:
				case FLOAT:
				case REAL:
				case DOUBLE:
				case NUMERIC:
				case DECIMAL:
					break;
				//??????
				case DATE:
				case TIME:
				case TIMESTAMP:
					//?????????????????????????????????????????????
				default:
					strValue = "'" + strValue + "'";


			}
		} else if (Number.class.isAssignableFrom(javaType)) {
			//???????????????
		} else {
			strValue = "'" + strValue + "'";
		}
		return sql.replaceFirst("\\?", strValue);
	}

	/**
	 * ?????????????????????
	 */
	private static Method getDeclaredMethods(Class clazz, String methodName) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new IllegalArgumentException("??????" + methodName + "????????????");
	}

	/**
	 * ?????????????????????
	 */
	private static String getParamNameFromAnnotation(Method method, int i, String paramName) {
		final Object[] paramAnnos = method.getParameterAnnotations()[i];
		for (Object paramAnno : paramAnnos) {
			if (paramAnno instanceof Param) {
				paramName = ((Param) paramAnno).value();
			}
		}
		return paramName;
	}

	/**
	 * ??????????????????
	 */
	private static Object wrapCollection(final Object object) {
		if (object instanceof List) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", object);
			return map;
		} else if (object != null && object.getClass().isArray()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("array", object);
			return map;
		}
		return object;
	}

	/**
	 * ???????????????MyBatis?????????SQL?????????
	 *
	 * @param id Mapper xml ????????????select Id
	 * @param obj ??????
	 */
	public static String getMyBatisSql(String id, Object obj, SqlSessionFactory sqlSessionFactory) {
		MappedStatement ms = sqlSessionFactory.getConfiguration().getMappedStatement(id);
		BoundSql boundSql = ms.getBoundSql(obj);
		String sql = boundSql.getSql();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			Object[] parameterArray = new Object[parameterMappings.size()];
			ParameterMapping parameterMapping = null;
			Object value = null;
			Object parameterObject = null;
			MetaObject metaObject = null;
			PropertyTokenizer prop = null;
			String propertyName = null;
			String[] names = null;
			for (int i = 0; i < parameterMappings.size(); i++) {
				parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					propertyName = parameterMapping.getProperty();
					names = propertyName.split("\\.");
					if (propertyName.indexOf(".") != -1 && names.length == 2) {
						parameterObject = boundSql.getAdditionalParameter(names[0]);
						propertyName = names[1];
					} else if (propertyName.indexOf(".") != -1 && names.length == 3) {
						parameterObject = boundSql.getAdditionalParameter(names[0]); // map
						if (parameterObject instanceof Map) {
							parameterObject = ((Map) parameterObject).get(names[1]);
						}
						propertyName = names[2];
					} else {
						parameterObject = boundSql.getAdditionalParameter(propertyName);
					}
					if (null == parameterObject) {
						parameterObject = getFieldValueByFieldName(propertyName, obj);
					}
					prop = new PropertyTokenizer(propertyName);
					if (parameterObject == null) {
						value = null;
					} else if (ms.getConfiguration().getTypeHandlerRegistry()
							.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql
							.hasAdditionalParameter(prop.getName())) {
						value = boundSql.getAdditionalParameter(prop.getName());
					} else {
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}
					parameterArray[i] = value;
				}
				parameterObject = null;
			}
			int i = 0;
			while (sql.contains("?")) {
				if (null == parameterArray[i]) {
					throw new RuntimeException("mybatis??????sql??????");
				}
				if (parameterArray[i] instanceof String) {
					sql = sql.replaceFirst("\\?", "'" + parameterArray[i].toString() + "'");
				} else {
					sql = sql.replaceFirst("\\?", parameterArray[i].toString());
				}
				i++;
			}
			int index;
			StringBuilder sb = new StringBuilder();
			for (String s : sql.split("\n")) {
				index = s.indexOf("--");
				if (-1 == index) {
					index = s.length();
				}
				sb.append(s.substring(0, index).replace("\t", " ").trim()).append(" ");
			}
			return sb.toString().replaceAll("(\r?\n(\\s*\r?\n)+)", " ").replaceAll("\n", " ")
					.replaceAll("\r", " ").replaceAll(" {4}", " ").replaceAll(" {3}", " ")
					.replaceAll(" {2}", " ");

		}
		return sql;
	}

	/**
	 * ??????????????????????????????
	 */
	public static Object getFieldValueByFieldName(String fieldName, Object object) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			//???????????????????????????????????????private??????????????????
			field.setAccessible(true);
			return field.get(object);
		} catch (Exception e) {
			new RuntimeException(e);
			return null;
		}
	}

}
