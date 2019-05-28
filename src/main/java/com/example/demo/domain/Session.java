package com.example.demo.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name="Session")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;	
	@Column( name ="usuario")
    private Integer  usuario;
	@Column( name ="fecha_creacion")
    private Timestamp fecha_creacion;
    @Column( name ="fecha_inactividad")
    private Timestamp fecha_inactividad;
    @Column( name ="status")
    private Integer status;
    private Long tipousuario;
    private String sub;
    public Session(Integer usuario, Timestamp fecha_creacion, Timestamp fecha_inactividad, Integer status,Long tipousu, String sub) 
    {
		super();
		this.usuario = usuario;
		this.fecha_creacion = fecha_creacion;
		this.fecha_inactividad = fecha_inactividad;
		this.status = status;
		this.tipousuario = tipousu;
		this.sub=sub;
	}
    
}
