package br.com.pedrohenrick.agendatech.controller;

// Importa as classes que vamos usar
import br.com.pedrohenrick.agendatech.dto.AuthRequestDTO;
import br.com.pedrohenrick.agendatech.model.Usuario;
import br.com.pedrohenrick.agendatech.service.AuthService;
import jakarta.validation.Valid; // Importante para ativar as validações do DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Anotação que combina @Controller e @ResponseBody. Diz ao Spring que esta classe é um "porteiro" que fala JSON.
@RequestMapping("/auth") // Diz ao Spring que TODOS os endpoints dentro desta classe começarão com a URL "/auth"
public class AuthController {

    // 1. Injeta o "cérebro" (o AuthService)
    @Autowired
    private AuthService authService;

    /**
     * Este método será o nosso endpoint de registro.
     * @PostMapping("/register") -> Diz que ele responde a requisições HTTP POST na URL "/auth/register"
     * @return ResponseEntity<?> -> É a resposta HTTP completa (Status + Corpo)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        // @RequestBody -> Pega o JSON do corpo da requisição e transforma no nosso 'AuthRequestDTO'
        // @Valid -> ATIVA as validações (@NotBlank, @Email) que colocamos no DTO.
        //           Se alguma regra falhar, o Spring nem chama nosso método e já retorna um erro 400.
        
        try {
            // 2. Se a validação passar, tentamos chamar o serviço de registro
            Usuario novoUsuario = authService.register(authRequestDTO);
            
            // 3. Se der certo, retornamos o usuário recém-criado e o Status HTTP 201 (CREATED)
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);

        } catch (RuntimeException e) {
            // 4. Se o 'authService.register()' lançar a exceção (ex: "Email já cadastrado!")
            //    nós a capturamos aqui.
            
            // 5. Retornamos uma resposta de erro HTTP 400 (BAD_REQUEST) com a mensagem do erro.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // (Aqui no futuro, vamos adicionar o @PostMapping("/login") )
}