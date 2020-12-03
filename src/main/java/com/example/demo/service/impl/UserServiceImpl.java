package com.example.demo.service.impl;


import com.example.demo.dao.UserDao;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	UserDao userDao;

	@Override
	public List<User> getAllUser() {
		return userDao.getAllUser();
	}

	@Override
	public void insert(User user) {
		userDao.insert(user);
	}

	@Override
	public User getUserByCondition(User user) {
		return userDao.getUserByCondition(user);
	}
}