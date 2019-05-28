package com.example.demo.bean;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioBean {
	private String username;
    private String password;
    private String nombres;
    private String paterno;
    private String materno;
    private String email;
    private String perfil;
    private int minutes;
}
