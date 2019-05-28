package com.example.demo.web;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

import java.util.ArrayList;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Tools;
import com.example.demo.bean.Response;
import com.example.demo.domain.BO;
import com.example.demo.domain.Session;
import com.example.demo.domain.User;
import com.example.demo.repository.SesionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.TokenUtils;

@RestController()
@RequestMapping("/users")
public class UserinfoController {

	@Autowired
    UserRepository users;
	
	@Autowired
    SesionRepository sessionRepository;
	
    @Autowired
    private TokenUtils tokenUtils;
    
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    @GetMapping("/obtieneInfo")
    public ResponseEntity<?> currentUser(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request)
    {
    	String jwt =tokenUtils.getExtraInfo(request);
        Map<Object, Object> model = new HashMap<>();
        Optional<User> usuarioOpcional= users.findByUsername(userDetails.getUsername());
    	User usuario  =usuarioOpcional.get();
    	List<Session> listaSesssion = sessionRepository.findByUsuarioAndStatusAndSub((usuario.getId()).intValue(),1,jwt);
    	if(listaSesssion.size()>0)
    	{
            Session sesionUnica=  listaSesssion.get(0); 
            model.put("usuario", usuario.getUsername());
            model.put("id", usuario.getId());
            model.put("email", usuario.getEmail());
            model.put("nombre", usuario.getNombres());
            model.put("paterno", usuario.getPaterno());
            model.put("materno", usuario.getMaterno());
            model.put("telefono", usuario.getTelefono());
            return  new ResponseEntity((new Response("OK",200,(Object)model)),HttpStatus.OK);
    	}
    	else
    	{
    		model.put("usuario", usuario.getUsername());
    		model.put("Sesion","La session que consulta es invalida  ["+userDetails.getUsername()+"]");
    	}
    	return  new ResponseEntity((new Response("Error en Informacion",400,(Object)model)),HttpStatus.BAD_REQUEST);
    }
}
