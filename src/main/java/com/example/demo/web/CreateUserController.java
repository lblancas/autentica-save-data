package com.example.demo.web;

import static org.springframework.http.ResponseEntity.ok;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.Response;
import com.example.demo.bean.UsuarioBean;
import com.example.demo.domain.Session;
import com.example.demo.domain.User;
import com.example.demo.repository.SesionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.TokenUtils;
import com.example.demo.security.jwt.JwtTokenProvider;

@RestController
@RequestMapping("/users")
public class CreateUserController 
{
		private int minutosSession =10;
	 	@Autowired
	    AuthenticationManager authenticationManager;

	    @Autowired
	    JwtTokenProvider jwtTokenProvider;

	    @Autowired
	    UserRepository users;

	    @Autowired
	    PasswordEncoder passwordEncoder;
	    
	    @Autowired
	    SesionRepository sessionRepository;
	    
	    @Autowired
	    private TokenUtils tokenUtils;
	    
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
	    @PutMapping("/creaUsuario")
	    public ResponseEntity create(@RequestBody UsuarioBean bean,@AuthenticationPrincipal UserDetails userDetails,HttpServletRequest request) 
	    {
	    	String jwt =tokenUtils.getExtraInfo(request);
	        Optional<User> usuarioBusqueda= users.findByUsername(userDetails.getUsername());
	    	User userBusqueda  =usuarioBusqueda.get();
	    	List<Session> listaSesssion = sessionRepository.findByUsuarioAndStatusAndSub((userBusqueda.getId()).intValue(),1,jwt);
	    	if(listaSesssion.size()==0)
	    	{
	    		return  new ResponseEntity((new Response("Session invalida del usuario:  ["+userDetails.getUsername()+"]",400,(Object)
	    				"Problemas con la creacion del usuario:"+bean.getUsername())),HttpStatus.BAD_REQUEST);
	    	} 
	    	String strAdmin ="";
	    	boolean existeUsuario=false;
	        try 
	        {
	        	Optional<User> usuarioOpcional= users.findByUsername(bean.getUsername());
	        	User usuario  =usuarioOpcional.get();
	        	existeUsuario=true;
	        }
	        catch(Exception ex)
	        {
	        	existeUsuario=false;
	        }
	        if(!existeUsuario)
	        {
		        try
		        {
		        	User usuario= this.users.save(User.builder()
		                    .username(bean.getUsername())
		                    .password(this.passwordEncoder.encode(bean.getPassword()))
		                    .roles(Arrays.asList(bean.getPerfil()))
		                    .build()
		                );
		        	usuario.setNombres(bean.getNombres());
		        	usuario.setPaterno(bean.getPaterno());
		        	usuario.setMaterno(bean.getMaterno());
		        	usuario.setActivo(1);
		        	usuario.setEmail(bean.getEmail());
		        	usuario.setIntentos(0);
		        	usuario.setMaximo_intentos(5);
		        	int seg =60;
		    		int mil = 1000;
		    		int sesion =  10 ; //minutos;
		    		Date dateCreation = new Date(System.currentTimeMillis());
		            Date expirationPassword= new Date(System.currentTimeMillis() + (minutosSession *  seg *  mil));
		            usuario.setFecha_cambio_password (new Timestamp(expirationPassword.getTime()));  
		            usuario.setFecha_creacion ( new Timestamp(dateCreation.getTime()));
		            usuario.setSesionminutos(bean.getMinutes());
		            this.users.save(usuario);
		            Map<Object, Object> model = new HashMap<>();
		            model.put("usuario",usuario);
		            return  new ResponseEntity((new Response("OK",200,(Object)model)),HttpStatus.OK);
		        } catch (AuthenticationException e) {
		        	strAdmin = ("Invalido forma de usuario ::: "+bean.getUsername());
		        }
	        }
	        else
	        {
	        	strAdmin = ("El usuario ya existe ::: "+bean.getUsername());
	        }
	        return  new ResponseEntity((new Response("Problemas al crear usuario:  ["+userDetails.getUsername()+"]",400,(Object)
	        		strAdmin+" "+bean.getUsername())),HttpStatus.BAD_REQUEST);
	    }/*
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
	    @DeleteMapping("/deleteUser")
	    public ResponseEntity deleteAdmin(@RequestBody String username,@AuthenticationPrincipal UserDetails userDetails) 
	    {
	    	if(!existeSessionValida(userDetails))
	    	{
	    		Map<Object, Object> model = new HashMap<>();
	            model.put("usuario",userDetails.getUsername());
	            model.put("mensaje","Session invalida");
	            model.put("code",400);
	            return new ResponseEntity<>(model,HttpStatus.BAD_REQUEST);
	    	}
	    	
	    	String strAdmin ="Usuario no encontrado ::: "+username;
	    	boolean existeUsuario=false;
	        try 
	        {
	        	Optional<User> usuarioOpcional= users.findByUsername(username);
	        	User usuario  =usuarioOpcional.get();
	        	users.delete(usuario);
	        	existeUsuario=true;
	        	Map<Object, Object> model = new HashMap<>();
	            model.put("usuario",usuario.getUsername());
	            model.put("code",200);
	            model.put("mensaje","Usuario Borrado con exito");
	            return ok(model);
	        }
	        catch(Exception ex)
	        {
	        	strAdmin = ex.getMessage();
	        }
	        throw new BadCredentialsException(strAdmin );
	    }*/
	    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
	    @PostMapping("/modificaPassword")
	    public ResponseEntity modifiPassord(@RequestBody UsuarioBean bean,@AuthenticationPrincipal UserDetails userDetails,HttpServletRequest request) 
	    {
	    	String jwt =tokenUtils.getExtraInfo(request);
	        Optional<User> usuarioBusqueda= users.findByUsername(userDetails.getUsername());
	    	User userBusqueda  =usuarioBusqueda.get();
	    	List<Session> listaSesssion = sessionRepository.findByUsuarioAndStatusAndSub((userBusqueda.getId()).intValue(),1,jwt);
	    	if(listaSesssion.size()==0)
	    	{
	    		return  new ResponseEntity(new UsuarioException(400,"Session invalida del usuario:  ["+userDetails.getUsername()+"]"),HttpStatus.BAD_REQUEST);
	    	} 
	    	String strAdmin ="Usuario no encontrado ::: "+bean.getUsername();
	    	boolean existeUsuario=false;
	        try 
	        {
	        	Optional<User> usuarioOpcional= users.findByUsername(bean.getUsername());
	        	User usuario  =usuarioOpcional.get();
	        	usuario.setPassword(this.passwordEncoder.encode(bean.getPassword()));
	            this.users.save(usuario);
	        	Map<Object, Object> model = new HashMap<>();
	        	model.put("usuario",usuario);
	            model.put("code",200);
	            model.put("mensaje","Usuario modificado con exito ::"+bean.getUsername());
	            return  new ResponseEntity((new Response("OK",200,(Object)model)),HttpStatus.OK);
	        }
	        catch(Exception ex)
	        {
	        	strAdmin = ex.getMessage();
	        }
	        return  new ResponseEntity((new Response("Problemas al modificar usuario:  ["+bean.getUsername()+"]",400,(Object)
	        		strAdmin+" "+bean.getUsername())),HttpStatus.BAD_REQUEST);
	    }
	    /* 
	    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
	    @PostMapping("/modifyTimeSession")
	    public ResponseEntity modifyTimeSession(@RequestBody UsuarioBean bean,@AuthenticationPrincipal UserDetails userDetails) 
	    {
	    	if(!existeSessionValida(userDetails))
	    	{
	    		Map<Object, Object> model = new HashMap<>();
	            model.put("usuario",userDetails.getUsername());
	            model.put("mensaje","Session invalida");
	            model.put("code",400);
	            return new ResponseEntity<>(model,HttpStatus.BAD_REQUEST);
	    	}
	    	String strAdmin ="Usuario no encontrado ::: "+bean.getUsername();
	    	boolean existeUsuario=false;
	        try 
	        {
	        	Optional<User> usuarioOpcional= users.findByUsername(bean.getUsername());
	        	User usuario  =usuarioOpcional.get();
	        	usuario.setSesionminutos(bean.getMinutes());
	            this.users.save(usuario);
	        	Map<Object, Object> model = new HashMap<>();
	            model.put("usuario",usuario);
	            model.put("code",200);
	            model.put("mensaje","Usuario modificado con exito ::"+bean.getUsername());
	            return ok(model);
	        }
	        catch(Exception ex)
	        {
	        	strAdmin = ex.getMessage();
	        }
	        throw new BadCredentialsException(strAdmin );
	    }
	   */
	    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
	    @PostMapping("/desbloqueaUsuario")
	    public ResponseEntity desbloqueaUser(@AuthenticationPrincipal UserDetails userDetails,@RequestBody UsuarioBean bean,HttpServletRequest request) 
	    {
	        String jwt =tokenUtils.getExtraInfo(request);
	        Optional<User> usuarioBusqueda= users.findByUsername(userDetails.getUsername());
	    	User userBusqueda  =usuarioBusqueda.get();
	    	List<Session> listaSesssion = sessionRepository.findByUsuarioAndStatusAndSub((userBusqueda.getId()).intValue(),1,jwt);
	    	if(listaSesssion.size()==0)
	    	{
	    		return  new ResponseEntity(new UsuarioException(400,"Session invalida del usuario:  ["+userDetails.getUsername()+"]"),HttpStatus.BAD_REQUEST);
	    	}
	        try 
	        {
	        	Optional<User> usuarioOpcional= users.findByUsername(bean.getUsername());
	        	User usuario  =usuarioOpcional.get();
	        	usuario.setActivo(1);
	        	int seg =60;
	    		int mil = 1000;
	    		int sesion =  10 ; //minutos;
	    		usuario.setIntentos(0);
	        	usuario.setMaximo_intentos(5);
	    		Date dateCreation = new Date(System.currentTimeMillis());
	            Date expirationPassword= new Date(System.currentTimeMillis() + (minutosSession *  seg *  mil));
	            usuario.setFecha_cambio_password (new Timestamp(expirationPassword.getTime()));  
	            usuario.setFecha_creacion ( new Timestamp(dateCreation.getTime()));
	            this.users.save(usuario);
	        	Map<Object, Object> model = new HashMap<>();
	            model.put("usuario",usuario);
	            model.put("code",200);
	            model.put("mensaje","Usuario modificado con exito ::"+bean.getUsername());
	            return  new ResponseEntity((new Response("OK",200,(Object)model)),HttpStatus.OK);
	        }
	        catch(Exception ex)
	        {
	        	ex.printStackTrace();
	        }
	        return  new ResponseEntity((new Response("Problemas al desbloquear usuario:  ["+bean.getUsername()+"] Session invalida",400,(Object)
	        		"Session invalida: "+userDetails.getUsername())),HttpStatus.BAD_REQUEST);
	    }
	    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
	    @PostMapping("/logout")
	    public ResponseEntity logout(@AuthenticationPrincipal UserDetails userDetails,HttpServletRequest request)
	    {
	        Map<Object, Object> model = new HashMap<>();
	        String jwt =tokenUtils.getExtraInfo(request);
	        Optional<User> usuarioOpcional= users.findByUsername(userDetails.getUsername());
	    	User usuario  =usuarioOpcional.get();
	    	List<Session> listaSesssion = sessionRepository.findByUsuarioAndStatusAndSub((usuario.getId()).intValue(),1,jwt);
	    	if(listaSesssion.size()>0)
	    	{
		    	for (Session sess: listaSesssion) 
				{
					long timeNow = (new Date()).getTime();
					long timeSess= sess.getFecha_inactividad().getTime();
					sess.setStatus(0);
					sessionRepository.save(sess);
				}
	
		    	model.put("usuario", usuario.getUsername());
	            model.put("id", usuario.getId());
	            model.put("email", usuario.getEmail());
	            model.put("nombre", usuario.getNombres());
	            model.put("paterno", usuario.getPaterno());
	            model.put("materno", usuario.getMaterno());
	            model.put("telefono", usuario.getTelefono());
	            model.put("code", 200);
	            model.put("message", "Session terminada");
	            return  new ResponseEntity((new Response("OK",200,(Object)model)),HttpStatus.OK);
	    	}
	    	else
	    		return  new ResponseEntity((new Response("Problemas al salir de la session usuario:  ["+userDetails.getUsername()+"] Session invalida",400,(Object)
		        		"Session invalida: "+userDetails.getUsername())),HttpStatus.BAD_REQUEST);
	    } 
}
