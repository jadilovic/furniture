package com.avlija.furniture.configuration;

//import com.avlija.furniture.service.MyUserDetailsService;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private DataSource dataSource;
    
    private final String USERS_QUERY = "select user_name, password, active from users where user_name=?";
    private final String ROLES_QUERY = "select u.user_name, r.role from users u inner join user_role ur on (u.user_id = ur.user_id) inner join roles r on (ur.role_id=r.role_id) where u.user_name=?";

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	  auth.jdbcAuthentication()
    	   .usersByUsernameQuery(USERS_QUERY)
    	   .authoritiesByUsernameQuery(ROLES_QUERY)
    	   .dataSource(dataSource)
    	   .passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
        String[] staticResources  =  {
                "/css/**",
                "/images/**",
                "/fonts/**",
                "/scripts/**",
            };

        http.
                authorizeRequests()
                .antMatchers(staticResources).permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/forgot").permitAll()
                .antMatchers("/reset").permitAll()
                .antMatchers("/home/**").hasAnyAuthority("ADMIN", "CLIENT")
                .antMatchers("/admin/**").hasAuthority("ADMIN").anyRequest()
                .authenticated().and().csrf().disable().formLogin()
                .loginPage("/login").failureUrl("/login?error=true")
                .defaultSuccessUrl("/home/home", true)
                .usernameParameter("user_name")
                .passwordParameter("password")
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .and().rememberMe().alwaysRemember(true)
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(60*60)
                .and().exceptionHandling()
                .accessDeniedPage("/access_denied");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }
    
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
     JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
     db.setDataSource(dataSource);
     
     return db;
    }

}