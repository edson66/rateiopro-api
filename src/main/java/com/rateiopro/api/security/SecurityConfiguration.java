package com.rateiopro.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private FiltroValidacaoToken filtroValidacaoToken;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(csfr -> csfr.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth-> {
                    auth.requestMatchers(HttpMethod.POST,"/auth/login").permitAll();
                    auth.requestMatchers(HttpMethod.POST,"/auth/cadastrar").permitAll();
                    auth.requestMatchers("/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**").permitAll();
                    auth.requestMatchers("/login/**","/github/**").permitAll();
                    auth.anyRequest().authenticated();
                })

                .addFilterBefore(filtroValidacaoToken, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager liberarAcesso(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder){
        return restClientBuilder.build();
    }
}
