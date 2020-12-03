package com.example.demo.controler;


import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserControler {

	@Autowired
	UserService userService;

	@PostMapping(value = "/getAllUser")
	public List<User> getAllUser() {
		return userService.getAllUser();
	}

	@PostMapping(value = "/insert")
	public void insert(@RequestBody User user) {
		userService.insert(user);
	}
	@PostMapping(value = "/getUserByCondition")
	public User getUserByCondition(@RequestBody User user) {
		return userService.getUserByCondition(user);
	}
}
