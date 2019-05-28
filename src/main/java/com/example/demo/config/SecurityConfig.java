package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.example.demo.security.jwt.JwtConfigurer;
import com.example.demo.security.jwt.JwtTokenProvider;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/auth/signin").permitAll()
            .antMatchers("/auth/obtieneInfo").permitAll()
            .antMatchers("/**/*.html").permitAll()
            .antMatchers("/v2/api-docs").permitAll()
            .antMatchers("/configuration/ui").permitAll()
            .antMatchers("/swagger-resources/**").permitAll()
            .antMatchers("/swagger-ui.html").permitAll()
			.antMatchers("/swagger-ui").permitAll()
			.antMatchers("/swagger-resources*").permitAll()
			.antMatchers("/*.html").permitAll()
			.antMatchers("/v2/api-docs").hasAnyRole("ANONYMOUS")
            .antMatchers("/configuration/ui").hasAnyRole("ANONYMOUS")
            .antMatchers("/swagger-resources/**").hasAnyRole("ANONYMOUS")
            .antMatchers("/swagger-ui.html").hasAnyRole("ANONYMOUS")
			.antMatchers("/swagger-ui").hasAnyRole("ANONYMOUS")
			.antMatchers("/swagger-resources*").hasAnyRole("ANONYMOUS")
			.antMatchers("/*.html").hasAnyRole("ANONYMOUS")
            .anyRequest().authenticated()
            .and()
            .apply(new JwtConfigurer(jwtTokenProvider));
        //@formatter:on
    }


}

