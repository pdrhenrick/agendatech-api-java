package br.com.pedrohenrick.agendatech.model;

// Importa as classes que vamos usar
import br.com.pedrohenrick.agendatech.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.util.UUID; // Vamos usar UUID como ID para ser mais seguro

@Data // Anotação do Lombok (cria getters, setters, equals, hashcode, toString)
@Entity // Indica que esta classe é uma entidade JPA (uma tabela)
@Table(name = "TB_USUARIOS") // Define o nome exato da tabela no banco
public class Usuario implements Serializable {
    // Serializable é uma "boa prática" para entidades
    private static final long serialVersionUID = 1L;

    @Id // Marca este campo como a Chave Primária (Primary Key)
    @GeneratedValue(strategy = GenerationType.AUTO) // O banco de dados vai gerar o ID automaticamente
    private UUID id;

    @Column(nullable = false, length = 150) // 'nullable=false' significa que é OBRIGATÓRIO
    private String nome;

    @Column(nullable = false, unique = true, length = 100) // 'unique=true' proíbe emails repetidos
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING) // Diz ao JPA para gravar o Enum como Texto (ex: "CLIENTE") e não como um número
    private Role role;
}