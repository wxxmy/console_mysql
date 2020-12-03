package com.example.demo.dao;


import com.example.demo.domain.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
	List<User> getAllUser();
	void insert(User user);
	User getUserByCondition(User user);
}
