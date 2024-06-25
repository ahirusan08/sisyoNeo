package com.example.demo.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;

@Component
@SessionScope
@Data
public class UserAccount {

	private Integer id;
	private String name;

	public UserAccount() {

	}

	public UserAccount(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

}
