package br.com.pedrohenrick.agendatech.service;

// Importa as classes que vamos usar
import br.com.pedrohenrick.agendatech.dto.AuthRequestDTO;
import br.com.pedrohenrick.agendatech.model.Usuario;
import br.com.pedrohenrick.agendatech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service // Marca esta classe como um "Serviço" (onde fica a regra de negócio)
         // O Spring vai gerenciá-la automaticamente.
public class AuthService {

    // --- Injeção de Dependência ---
    // Nós pedimos ao Spring para "injetar" (fornecer) as instâncias
    // dos objetos que precisamos. Não precisamos dar "new" neles.

    @Autowired // Pede ao Spring o 'UsuarioRepository' que criamos
    private UsuarioRepository usuarioRepository;

    @Autowired // Pede ao Spring o 'PasswordEncoder' (BCrypt) que definimos no SecurityConfig
    private PasswordEncoder passwordEncoder;

    // --- Fim da Injeção ---


    /**
     * Este é o método principal que o Controller (próximo passo) vai chamar.
     * Ele recebe o DTO (formulário) e retorna o novo Usuário salvo.
     */
    public Usuario register(AuthRequestDTO authRequestDTO) {
        
        // 1. Verificar se o email já existe no banco
        // Usamos o método 'existsByEmail' que criamos no Repository
        if (usuarioRepository.existsByEmail(authRequestDTO.getEmail())) {
            
            // 2. Se o email já existir, nós lançamos uma exceção.
            // Isso vai parar a execução e enviar um erro para o Controller.
            throw new RuntimeException("Erro: Email já cadastrado!");
        }

        // 3. Se o email estiver livre, criamos um novo objeto 'Usuario' (o do 'model')
        Usuario novoUsuario = new Usuario();

        // 4. Preenchemos os dados do novo usuário com base nos dados do DTO
        novoUsuario.setNome(authRequestDTO.getNome());
        novoUsuario.setEmail(authRequestDTO.getEmail());
        novoUsuario.setRole(authRequestDTO.getRole()); // CLIENTE ou PROFISSIONAL

        // 5. O PASSO MAIS IMPORTANTE: Criptografar a senha
        // Nós NUNCA salvamos a senha pura (authRequestDTO.getSenha())
        // Nós usamos o 'passwordEncoder' para gerar um "hash" seguro.
        String senhaCriptografada = passwordEncoder.encode(authRequestDTO.getSenha());
        novoUsuario.setSenha(senhaCriptografada);

        // 6. Salvar o novo usuário no banco de dados
        // Usamos o método 'save()' (que já vem no JpaRepository)
        // e retornamos o usuário que foi salvo (ele já vem com o ID).
        return usuarioRepository.save(novoUsuario);
    }
}