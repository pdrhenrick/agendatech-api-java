package br.com.pedrohenrick.agendatech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive; // Garante que o número é > 0
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ServicoRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    // A descrição é opcional, por isso não tem @NotBlank
    private String descricao;

    @NotNull(message = "A duração é obrigatória")
    @Positive(message = "A duração deve ser um número positivo (em minutos)") // ex: 30
    private Integer duracaoMinutos;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser positivo") // ex: 50.00
    private BigDecimal preco;

    //
    // NOTA: O ID do profissional NÃO é pedido aqui.
    // Vamos pegar o profissional que está LOGADO pelo Token (Passaporte).
    // Faremos isso no 'Service'.
    //
}