package com.example.demo.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;

@Component
@SessionScope
@Data
public class HostAccount {

	private Integer id;
	private String name;

	public HostAccount() {

	}

	public HostAccount(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

}
