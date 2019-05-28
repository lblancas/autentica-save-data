package com.example.demo.web;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class  UsuarioException   
{
	private int code;
	private long user;
	private String mensaje;
    public UsuarioException() {
    }

    public UsuarioException(long id, int code , String mensaje) 
    {
        this.code =code;
        this.mensaje = mensaje;
        this.user= id;
    }
    public UsuarioException(long id) 
    {
        this.code =400;
        this.mensaje = "Usuario: " +id +" not found.";
        this.user= id;
    }

	public UsuarioException(int code2, String mensaje2) {
        this.code =code2;
        this.mensaje = mensaje2;
        this.user= 0;
	}
}
