package com.example.demo.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.UserDao;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.util.SqlHelper;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	SqlSessionFactory sqlSessionFactory;

	@Autowired
	SqlSession sqlSession;

	@Override
	public List<User> getAllUser() {
		return userDao.getAllUser();
	}

	@Override
	public void insert(User user) {
		userDao.insert(user);
	}

	@Override
	public List<User> getUserByCondition(User user) {
		String name = UserDao.class.getName();
		System.out.println("name = " + name);
		String sql = SqlHelper
				.getMyBatisSql("com.example.demo.dao.UserDao.getUserByCondition", user, sqlSessionFactory);
		System.out.println("-------------------------------------------------------------------");
		System.out.println("SqlHelper.getMyBatisSql = " + sql);
		System.out.println("-------------------------------------------------------------------");

		return userDao.getUserByCondition(user);
	}

	@Override
	public User getUserByAge(User user) {
//		String sql = sqlSessionFactory.getConfiguration().getMappedStatement("com.example.demo.dao.UserDao.getUserByAge").getBoundSql(user).getSql();
//		System.out.println("sql = " + sql);

//		String mapperSql = SqlHelper.getMapperSql(
//				sqlSession,
//				"com.example.demo.dao.UserDao.getUserByAge", 13);

		String sql = SqlHelper
				.getMyBatisSql("com.example.demo.dao.UserDao.getUserByAge", user, sqlSessionFactory);
		System.out.println("sql = " + sql);
		return userDao.getUserByAge(user);
	}
}