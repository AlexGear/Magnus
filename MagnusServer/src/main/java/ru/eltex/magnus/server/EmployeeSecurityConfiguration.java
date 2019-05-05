package ru.eltex.magnus.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.eltex.magnus.server.db.StoragesProvider;
import ru.eltex.magnus.server.db.dataclasses.Admin;
import ru.eltex.magnus.server.db.dataclasses.Viewer;

@Configuration
@EnableWebSecurity
public class EmployeeSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/somepage.html").hasAnyRole("VIEWER", "ADMIN")
                .antMatchers("/whatpage.html").hasRole("ADMIN")
                //.antMatchers("/addNewEmployee").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll()
                .and()
                .logout().permitAll();

        http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
        Admin admin = StoragesProvider.getAdminStorage().getAdmin();
        authenticationMgr.inMemoryAuthentication()
                .withUser(admin.getLogin()).password(admin.getPassword()).roles("ADMIN")
                .and().passwordEncoder(new BCryptPasswordEncoder());

        for (Viewer viewer : StoragesProvider.getViewersStorage().getAllViewers()) {
            authenticationMgr.inMemoryAuthentication()
                    .withUser(viewer.getLogin()).password(viewer.getPassword()).roles("VIEWER")
                    .and().passwordEncoder(new BCryptPasswordEncoder());
        }
    }
}
