package br.com.pedrohenrick.agendatech.model;

import br.com.pedrohenrick.agendatech.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Collection; // <-- NOVA IMPORTAÇÃO
import java.util.List; // <-- NOVA IMPORTAÇÃO
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority; // <-- NOVA IMPORTAÇÃO
import org.springframework.security.core.authority.SimpleGrantedAuthority; // <-- NOVA IMPORTAÇÃO
import org.springframework.security.core.userdetails.UserDetails; // <-- NOVA IMPORTAÇÃO

@Data
@Entity
@Table(name = "TB_USUARIOS")
// 1. MUDANÇA AQUI: Adicionamos a interface 'UserDetails'
public class Usuario implements Serializable, UserDetails {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    // =======================================================
    // ===== 2. MUDANÇA AQUI: NOVOS MÉTODOS DA INTERFACE 'UserDetails' =====
    // =======================================================
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Aqui definimos os "papéis" (Roles) do usuário
        if (this.role == Role.ADMIN) {
            // Se for ADMIN, ele tem as permissões de ADMIN, PROFISSIONAL e CLIENTE
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_PROFISSIONAL"),
                    new SimpleGrantedAuthority("ROLE_CLIENTE")
            );
        } else if (this.role == Role.PROFISSIONAL) {
            // Se for PROFISSIONAL, tem as permissões de PROFISSIONAL e CLIENTE
            return List.of(
                    new SimpleGrantedAuthority("ROLE_PROFISSIONAL"),
                    new SimpleGrantedAuthority("ROLE_CLIENTE")
            );
        } else {
            // Se for CLIENTE, tem apenas as permissões de CLIENTE
            return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
        }
    }

    @Override
    public String getPassword() {
        // O Spring Security vai chamar este método para pegar a senha criptografada
        return this.senha;
    }

    @Override
    public String getUsername() {
        // O Spring Security vai chamar este método para pegar o "login" (que é o email)
        return this.email;
    }

    // --- Para este projeto, vamos deixar os outros como 'true' ---

    @Override
    public boolean isAccountNonExpired() {
        return true; // A conta não expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // A conta não está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // As credenciais (senha) não expiram
    }

    @Override
    public boolean isEnabled() {
        return true; // A conta está habilitada
    }
}