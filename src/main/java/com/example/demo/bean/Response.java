package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Response {
	private String menssage;
	private Integer code;
	private Object result;
}
