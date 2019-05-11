package ru.eltex.magnus.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
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
public class ViewersSecurityConfiguration extends WebSecurityConfigurerAdapter {

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
        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> builder =
                authenticationMgr.inMemoryAuthentication();

        Admin admin = StoragesProvider.getAdminStorage().getAdmin();
        builder.withUser(admin.getLogin()).password(admin.getPassword()).roles("ADMIN");

        for (Viewer viewer : StoragesProvider.getViewersStorage().getAllViewers()) {
            builder.withUser(viewer.getLogin()).password(viewer.getPassword()).roles("VIEWER");
        }

        builder.passwordEncoder(new BCryptPasswordEncoder());
    }
}
