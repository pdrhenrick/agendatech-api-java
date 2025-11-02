package br.com.pedrohenrick.agendatech.config;

import org.springframework.beans.factory.annotation.Autowired; // <-- NOVA IMPORTAÇÃO
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // <-- NOVA IMPORTAÇÃO
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // <-- NOVA IMPORTAÇÃO
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // <-- NOVA IMPORTAÇÃO

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // =======================================================
    // ===== NOVA INJEÇÃO DO NOSSO FILTRO =====
    // =======================================================
    @Autowired
    private SecurityFilter securityFilter; // Injeta o "guarda" que criamos
    // =======================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Bean que configura o "firewall" da nossa API.
     * ESTE MÉTODO FOI MUITO MODIFICADO.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita o CSRF (padrão para APIs REST)
            .csrf(csrf -> csrf.disable()) 
            
            // =======================================================
            // ===== MUDANÇA IMPORTANTE (STATELESS) =====
            // =======================================================
            // Diz ao Spring que a API é "Stateless". Não vamos usar Sessões.
            // Cada requisição deve se autenticar (com o token).
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // =======================================================

            .authorizeHttpRequests(authorize -> authorize
                // Nossas rotas públicas de /auth/ (registro e login)
                // Permitimos POST para /auth/login e /auth/register
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                
                // QUALQUER OUTRA requisição DEVE ser autenticada
                .anyRequest().authenticated() 
            )
            
            // =======================================================
            // ===== MUDANÇA IMPORTANTE (CONTRATANDO O "GUARDA") =====
            // =======================================================
            // Diz ao Spring para adicionar nosso "guarda" (SecurityFilter)
            // ANTES do filtro padrão de login (UsernamePasswordAuthenticationFilter)
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
            // =======================================================

        return http.build();
    }
}