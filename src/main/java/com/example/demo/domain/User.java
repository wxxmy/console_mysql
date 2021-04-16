package com.example.demo.domain;

import java.util.Objects;
import lombok.Data;

@Data
public class User {

	private Integer id;
	private String name;
	private Integer age;

	public User(Integer id, String name, Integer age, String sex) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.sex = sex;
	}

	private String sex;

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", age=" + age +
				", sex='" + sex + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		User user = (User) o;
		return Objects.equals(id, user.id) && Objects.equals(name, user.name)
				&& Objects.equals(age, user.age) && Objects.equals(sex, user.sex);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, age, sex);
	}
}