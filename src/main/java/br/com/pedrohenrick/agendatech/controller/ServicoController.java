package br.com.pedrohenrick.agendatech.controller;

import br.com.pedrohenrick.agendatech.dto.ServicoRequestDTO;
import br.com.pedrohenrick.agendatech.dto.ServicoResponseDTO;
import br.com.pedrohenrick.agendatech.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/servicos") // URL base: http://localhost:8080/servicos
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    /**
     * Endpoint 1: Criar Serviço
     * URL: POST http://localhost:8080/servicos
     * (Protegido pelo SecurityFilter, pois NÃO está em /auth/**)
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ServicoRequestDTO dto) {
        try {
            // 1. Tenta chamar o serviço para criar
            ServicoResponseDTO response = servicoService.createServico(dto);
            // 2. Retorna 201 CREATED com o serviço criado
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            // 3. Se o serviço der erro (ex: "Acesso negado"), retorna 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint 2: Listar MEUS Serviços (do profissional logado)
     * URL: GET http://localhost:8080/servicos
     * (Protegido pelo SecurityFilter)
     */
    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> getMeusServicos() {
        // 1. Chama o serviço (que descobre quem está logado)
        List<ServicoResponseDTO> servicos = servicoService.getMeusServicos();
        // 2. Retorna 200 OK com a lista de serviços
        return ResponseEntity.ok(servicos);
    }

    /**
     * Endpoint 3: Atualizar um Serviço
     * URL: PUT http://localhost:8080/servicos/{idDoServico}
     * (Protegido pelo SecurityFilter)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody ServicoRequestDTO dto) {
        // @PathVariable -> Pega o 'id' que vem na URL
        try {
            // 1. Tenta chamar o serviço de atualização
            ServicoResponseDTO response = servicoService.updateServico(id, dto);
            // 2. Retorna 200 OK com o serviço atualizado
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // 3. Se der erro (ex: "Serviço não encontrado" ou "Acesso negado")
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint 4: Deletar um Serviço
     * URL: DELETE http://localhost:8080/servicos/{idDoServico}
     * (Protegido pelo SecurityFilter)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            // 1. Tenta chamar o serviço de deleção
            servicoService.deleteServico(id);
            // 2. Retorna 204 No Content (Sucesso, mas sem corpo de resposta)
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // 3. Se der erro (ex: "Serviço não encontrado" ou "Acesso negado")
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}