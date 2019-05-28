package com.example.demo.security; 
import org.springframework.security.core.Authentication; 
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest; 
import java.util.Map;

@Component
public class TokenUtils { 
    public static final String DETAILS_SUB= "sub";
    public static final String DETAILS_IAT = "iat";
    public static final String DETAILS_EXP = "exp";
    public static final String DETAILS_ROLES = "roles";
    private BearerTokenExtractor tokenExtractor;
    //@Autowired
    private TokenStore tokenStore;
    @PostConstruct
    private void postConstruct() {
        tokenExtractor = new BearerTokenExtractor();
    }
    public String getExtraInfo(HttpServletRequest request) 
    {
        String token = "N/A";
        Map<String, Object> details = null;
        try {
            Authentication auth = tokenExtractor.extract(request);
            return (String) auth.getPrincipal().toString();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
        return token;
    }
  
}
