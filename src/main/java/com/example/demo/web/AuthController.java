package com.example.demo.web;
import static org.springframework.http.ResponseEntity.ok;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.Tools;
import com.example.demo.bean.Response;
import com.example.demo.domain.BO;
import com.example.demo.domain.Session;
import com.example.demo.domain.User;
import com.example.demo.repository.SesionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtTokenProvider;
import com.example.demo.security.jwt.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/auth")
@Api(value="API REST Servicio de autenticacion")
@CrossOrigin(origins="*")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository users;
    
    @Autowired
    SesionRepository sessionRepository;
    
    @GetMapping("/obtieneInfo")
    public ResponseEntity<String> getInfo()
    {
    	return  new ResponseEntity((new String("Servicio activo")),HttpStatus.OK);
    }
    
    
    @PostMapping("/signin")
    @ApiOperation(value="Servicio que obtiene un token por medio de username / password. El campo cuenta es opcional {1,2,3}")
    public ResponseEntity signin(@RequestBody AuthenticationRequest data) 
    {
        try 
        {
        	String username = data.getUsername();
        	List<User> lista= users.findByUsernameAndActivo(username,1);
        	if(lista.size()<=0)
        		return  new ResponseEntity((new Response("Usuario invalido / o password incorrecto",400,(Object)"Problemas en logeo de usuario:"+data.getUsername())),HttpStatus.BAD_REQUEST);	
                Optional<User> usuarioOpcional= users.findByUsername(username);
        	User usuario  =usuarioOpcional.get();
        	if(usuario.getActivo()==0)// inactivo
        		return  new ResponseEntity((new Response("Usuario inactivo",400,(Object)"Problemas en logeo de usuario:"+username)),HttpStatus.BAD_REQUEST);
        	if(usuario.getIntentos()>= usuario.getMaximo_intentos())// inactivo
        		return  new ResponseEntity((new Response("Usuario bloqueado",400,(Object)"Problemas en logeo de usuario:"+username)),HttpStatus.BAD_REQUEST);
        	try
        	{
	        	if(usuario.getFecha_cambio_password().getTime()< (new Date()).getTime() )
	        		return  new ResponseEntity((new Response("Usuario con password expirada",400,(Object)"Problemas en logeo de usuario:"+username)),HttpStatus.BAD_REQUEST);
        	}catch(Exception dateEx)
        	{
        		return  new ResponseEntity((new Response("Usuario problema en fecha :Fecha_cambio_password",400,(Object)"Problemas en logeo de usuario:"+username)),HttpStatus.BAD_REQUEST);
        	}
        	if(existeSession(usuario))
        		return  new ResponseEntity((new Response("Usuario con sesion activa",400,(Object)"Problemas en logeo de usuario:"+username)),HttpStatus.BAD_REQUEST);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String tokenJWT  =  jwtTokenProvider.createToken(username, this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());
            Session sesionCreada= createSession(usuario,1l,tokenJWT);
            Map<Object, Object> model = new HashMap<>();
            model.put("usuario", usuario.getUsername());
            model.put("id", usuario.getId());
            model.put("email", usuario.getEmail());
            model.put("nombre", usuario.getNombres());
            model.put("paterno", usuario.getPaterno());
            model.put("materno", usuario.getMaterno());
            model.put("caduca", sesionCreada.getFecha_inactividad());
            model.put("token",tokenJWT);
            return  new ResponseEntity((new Response("OK",200,(Object)model)),HttpStatus.OK);
        }
        catch (AuthenticationException e) 
        {
        	return  new ResponseEntity((new Response("Usuario invalido / o password incorrecto",400,(Object)"Problemas en logeo de usuario:"+data.getUsername())),HttpStatus.BAD_REQUEST);
        }
    }

	private boolean existeSession(User usuario) 
	{
		List<Session> listaSesssion = sessionRepository.findByUsuarioAndStatus((usuario.getId()).intValue(),1);
		if(listaSesssion.size()>0)
		{
			boolean modificoSessiones=false;
			for (Session sess: listaSesssion) 
			{
				long timeNow = (new Date()).getTime();
				long timeSess= sess.getFecha_inactividad().getTime();
				if(timeNow>timeSess)
				{
					sess.setStatus(0);
					sessionRepository.save(sess);
					modificoSessiones=true;
				}
				else
					return true;
			}
			if(modificoSessiones)
			{
				listaSesssion = sessionRepository.findByUsuarioAndStatus((usuario.getId()).intValue(),1);
				if(listaSesssion.size()>0)
					return true;
				return false;
			}
			
			return true;
		}
		return false;
	}
	private Session createSession(User usuario,Long idtipousuario,String sub) 
	{
		try
		{
			int seg =60;
    		int mil = 1000;
    		Date creacion = new Date(System.currentTimeMillis());
    		
    		Date now = new Date();
            now.setYear(now.getYear()+1);
            Date expiracion = new Date(now.getTime()); 
            
            Timestamp  creacionStamp = (new Timestamp(creacion.getTime()));  
            Timestamp  expiracionStamp = ( new Timestamp(expiracion.getTime()));
			Session se=new Session((usuario.getId()).intValue(),creacionStamp,expiracionStamp,1,idtipousuario,sub);
			Session sesionCreada=sessionRepository.save(se);
			return sesionCreada;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
