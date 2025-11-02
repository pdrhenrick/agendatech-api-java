package br.com.pedrohenrick.agendatech.config;

// Importa as classes que vamos usar
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Diz ao Spring que esta é uma classe de configuração
@EnableWebSecurity // Habilita a segurança web do Spring
public class SecurityConfig {

    /**
     * Este 'Bean' (um objeto gerenciado pelo Spring) ensina o Spring
     * a como encriptar e verificar senhas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Estamos dizendo para usar o algoritmo BCrypt, que é o padrão de mercado.
        return new BCryptPasswordEncoder();
    }

    /**
     * Este 'Bean' configura o "firewall" da nossa API.
     * É aqui que dizemos quais rotas são públicas e quais são privadas.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita o CSRF. Isso é uma configuração de segurança para sites
            // que usam "sessions", mas não é necessário para nossa API REST.
            .csrf(csrf -> csrf.disable()) 
            
            // Define as regras de autorização
            .authorizeHttpRequests(authorize -> authorize
                
                // 1. Regra Pública:
                // Dizemos ao Spring para PERMITIR qualquer requisição (GET, POST, etc.)
                // que comece com o caminho "/auth/**"
                // (Ex: /auth/register, /auth/login)
                .requestMatchers("/auth/**").permitAll() 
                
                // 2. Regra Privada:
                // QUALQUER OUTRA requisição (anyRequest) que não seja a de cima
                // (ex: /agendamentos, /servicos) DEVE estar autenticada.
                .anyRequest().authenticated() 
            );

        // Constrói e retorna as regras de segurança configuradas
        return http.build();
    }
}