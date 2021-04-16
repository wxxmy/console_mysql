package com.example.demo.service;


import com.example.demo.domain.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

	List<User> getAllUser();

	void insert(User user);

	List<User> getUserByCondition(User user) throws NoSuchMethodException;

	User getUserByAge(User user);
}
