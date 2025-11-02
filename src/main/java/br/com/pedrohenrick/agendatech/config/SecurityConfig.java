package br.com.pedrohenrick.agendatech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// IMPORTANTE: Nova importação adicionada
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean que ensina o Spring a como encriptar e verificar senhas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =======================================================
    // ===== NOVO BEAN ADICIONADO PARA O LOGIN =====
    // =======================================================
    /**
     * Este Bean "expõe" o Gerenciador de Autenticação do Spring Security
     * para que o nosso AuthService possa usá-lo para processar o login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    // =======================================================
    // ================= FIM DO NOVO BEAN =================
    // =======================================================


    /**
     * Bean que configura o "firewall" da nossa API.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            
            .authorizeHttpRequests(authorize -> authorize
                // Nossas rotas públicas de /auth/ (registro e login)
                .requestMatchers("/auth/**").permitAll() 
                
                // QUALQUER OUTRA requisição DEVE ser autenticada
                .anyRequest().authenticated() 
            );

        return http.build();
    }
}