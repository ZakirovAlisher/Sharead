package com.example.admin.config;

import com.example.site.service.UserService;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, proxyTargetClass = true)
@ComponentScan(basePackages = "com.example.admin")
public class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource(name="userService")
               private UserService userService;

               @Resource(name="passwordEncoder")
               public BCryptPasswordEncoder passwordEncoder;
               @Override
               protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().accessDeniedPage("/403");
        http.authorizeRequests()
            .antMatchers("/login").permitAll()
            .antMatchers("/*").authenticated();


        http.formLogin()
            .loginPage("/login").permitAll()

            .usernameParameter("user_email")
            .passwordParameter("user_password")
            .loginProcessingUrl("/auth").permitAll()
            .failureUrl("/login?error=Incorrect login")
            .defaultSuccessUrl("/profile");
        http.logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login").permitAll()


        ;

        //   http.csrf().disable();
    }
}