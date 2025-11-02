package br.com.pedrohenrick.agendatech.config;

import br.com.pedrohenrick.agendatech.repository.UsuarioRepository;
import br.com.pedrohenrick.agendatech.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marca esta classe como um "componente" gerenciado pelo Spring
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService; // Nosso serviço de "passaportes"

    @Autowired
    private UsuarioRepository usuarioRepository; // Nosso repositório de usuários

    /**
     * Este método é o "guarda" em si. Ele é chamado para CADA requisição
     * que chega na nossa API.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Tenta recuperar o token da requisição
        var token = this.recoverToken(request);

        // 2. Se um token foi enviado...
        if (token != null) {
            // 3. ...valida o token usando nosso TokenService
            var email = tokenService.validateToken(token); // Retorna o email (subject) se for válido

            // 4. Se o email for válido, busca o usuário no banco
            UserDetails usuario = usuarioRepository.findByEmail(email)
                    .orElse(null); // Retorna o usuário ou null

            // 5. Se encontramos o usuário...
            if (usuario != null) {
                // 6. ...criamos uma "Autenticação" para o Spring Security
                var authentication = new UsernamePasswordAuthenticationToken(
                        usuario, // O usuário (principal)
                        null,    // As credenciais (não precisamos aqui)
                        usuario.getAuthorities() // As permissões (ex: ROLE_CLIENTE)
                );
                
                // 7. "Colocamos o usuário para dentro": Salvamos a autenticação no contexto
                //    do Spring. A partir daqui, o Spring sabe quem está logado.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        // 8. Independentemente de ter logado ou não, passa a requisição para o próximo filtro
        //    (Se não logou, o Spring vai barrar ele nas rotas protegidas)
        filterChain.doFilter(request, response);
    }

    /**
     * Método auxiliar para extrair o token do Header "Authorization".
     * O padrão é vir como: "Bearer eyJhbGciOi...[token]..."
     */
    private String recoverToken(HttpServletRequest request) {
        // 1. Pega o valor do header "Authorization"
        var authHeader = request.getHeader("Authorization");
        
        // 2. Se não veio nada, retorna null
        if (authHeader == null) {
            return null;
        }

        // 3. Se veio, o token é o que vem depois de "Bearer "
        //    (Note o espaço depois de Bearer)
        return authHeader.replace("Bearer ", "");
    }
}