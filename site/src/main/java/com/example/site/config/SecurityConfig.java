package com.example.site.config;

import com.example.site.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, proxyTargetClass = true)
@ComponentScan(basePackages = "com.example.site")
@Order(200)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource(name="userService")
    private UserService userService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().accessDeniedPage("/403");
        http.authorizeRequests()
            .antMatchers("/login", "/register", "/reg").permitAll()
            .antMatchers("/*").authenticated();

        http.formLogin()
            .loginPage("/login").permitAll()
            .usernameParameter("user_email")
            .passwordParameter("user_password")
            .loginProcessingUrl("/auth").permitAll()
            .failureUrl("/login?error=Incorrect login")
            .defaultSuccessUrl("/");

        http.logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login").permitAll();
    }
}