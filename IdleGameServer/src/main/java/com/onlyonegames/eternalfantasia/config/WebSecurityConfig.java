package com.onlyonegames.eternalfantasia.config;

import com.onlyonegames.eternalfantasia.security.jwt.JwtAuthEntryPoint;
import com.onlyonegames.eternalfantasia.security.jwt.filter.JwtAuthTokenFilter;
import com.onlyonegames.eternalfantasia.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests().antMatchers("/auth/Login").permitAll()
                .antMatchers("/Temp").permitAll().antMatchers(HttpMethod.POST, "/api/User").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/ClearInfo").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/GiftReset").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/ResetGoodfeelingAllHero").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/ResetGoodfeelingByHero").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/ResetLinkforce").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/LinkWeaponClear").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/AddCharacterPiece").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/ResetMyCharacters").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/SpendFatigability").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/MyMailBox/SendMail").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/AddFatigability").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/Test/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/Test/**").permitAll()
//                .antMatchers("/").permitAll()
//                .antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**").permitAll()
//                .anyRequest().authenticated().and().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
//                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                .antMatchers("/auth/Tool/Login").permitAll()
                .antMatchers("/Temp").permitAll().antMatchers(HttpMethod.POST, "/api/User").permitAll()
                .antMatchers(HttpMethod.GET, "/api/ClearInfo").permitAll()
                .antMatchers(HttpMethod.POST, "/api/GiftReset").permitAll()
                .antMatchers(HttpMethod.POST, "/api/ResetGoodfeelingAllHero").permitAll()
                .antMatchers(HttpMethod.POST, "/api/ResetGoodfeelingByHero").permitAll()
                .antMatchers(HttpMethod.POST, "/api/ResetLinkforce").permitAll()
                .antMatchers(HttpMethod.POST, "/api/LinkWeaponClear").permitAll()
                .antMatchers(HttpMethod.POST, "/api/AddCharacterPiece").permitAll()
                .antMatchers(HttpMethod.POST, "/api/ResetMyCharacters").permitAll()
                .antMatchers(HttpMethod.POST, "/api/SpendFatigability").permitAll()
                .antMatchers(HttpMethod.POST, "/api/MyMailBox/SendMail").permitAll()
                .antMatchers(HttpMethod.POST, "/api/AddFatigability").permitAll()
                .antMatchers(HttpMethod.POST, "/api/Test/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/Test/**").permitAll()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated().and().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}