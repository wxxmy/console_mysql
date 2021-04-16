package com.example.demo.controler;


import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import java.util.List;
import java.util.Optional;
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
	public List<User> getUserByCondition(@RequestBody User user) {
		try {
			return userService.getUserByCondition(user);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	@PostMapping(value = "/getUserByAge")
	public String getUserByAge(@RequestBody User user) {
		User userByAge = userService.getUserByAge(user);
		return Optional.ofNullable(userByAge).isPresent() ? Optional.of(userByAge).map(User::getName)
				.orElse("用户名为空") : "这个年龄没有用户";
	}

	@PostMapping(value = "/getUserByAge2")
	public boolean getUserByAge2(@RequestBody User user) {
		User userByAge = userService.getUserByAge(user);
//		Optional.ofNullable(userByAge).get();
		return Optional.ofNullable(userByAge).isPresent();
	}
}
