package br.com.pedrohenrick.agendatech.service;

import br.com.pedrohenrick.agendatech.dto.ServicoRequestDTO;
import br.com.pedrohenrick.agendatech.dto.ServicoResponseDTO;
import br.com.pedrohenrick.agendatech.enums.Role;
import br.com.pedrohenrick.agendatech.model.Servico;
import br.com.pedrohenrick.agendatech.model.Usuario;
import br.com.pedrohenrick.agendatech.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;

    /**
     * Método auxiliar para pegar o usuário (profissional) que está logado
     * através do token (que o nosso "guarda" SecurityFilter já validou).
     */
    private Usuario getLoggedUser() {
        // Pega a autenticação que o SecurityFilter colocou no "contexto"
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * REGRA DE NEGÓCIO 1: Criar um novo serviço
     */
    public ServicoResponseDTO createServico(ServicoRequestDTO dto) {
        // 1. Descobre quem está logado
        Usuario profissional = getLoggedUser();

        // 2. Verificação de Segurança (Regra de Negócio)
        // Somente usuários com a role "PROFISSIONAL" podem criar serviços
        if (profissional.getRole() != Role.PROFISSIONAL) {
            throw new RuntimeException("Acesso negado: Apenas PROFISSIONAIS podem criar serviços.");
        }

        // 3. Cria o novo objeto Servico (do 'model')
        Servico novoServico = new Servico();
        novoServico.setNome(dto.getNome());
        novoServico.setDescricao(dto.getDescricao());
        novoServico.setDuracaoMinutos(dto.getDuracaoMinutos());
        novoServico.setPreco(dto.getPreco());
        
        // 4. Linka o serviço ao profissional que está logado
        novoServico.setProfissional(profissional); 

        // 5. Salva no banco
        Servico servicoSalvo = servicoRepository.save(novoServico);
        
        // 6. Retorna o DTO de Resposta
        return new ServicoResponseDTO(servicoSalvo);
    }

    /**
     * REGRA de NEGÓCIO 2: Listar apenas os serviços do profissional logado
     */
    public List<ServicoResponseDTO> getMeusServicos() {
        // 1. Descobre quem está logado
        Usuario usuario = getLoggedUser();
        
        // 2. Usa o método customizado do repositório para buscar
        List<Servico> servicos = servicoRepository.findByProfissionalId(usuario.getId());

        // 3. Converte a Lista de 'Servico' para uma Lista de 'ServicoResponseDTO'
        return servicos.stream()
                .map(ServicoResponseDTO::new) // (servico -> new ServicoResponseDTO(servico))
                .collect(Collectors.toList());
    }

    /**
     * REGRA DE NEGÓCIO 3: Atualizar um serviço (verificando se o profissional é o dono)
     */
    public ServicoResponseDTO updateServico(UUID id, ServicoRequestDTO dto) {
        // 1. Descobre quem está logado
        Usuario profissional = getLoggedUser();
        
        // 2. Busca o serviço no banco
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + id));

        // 3. Verificação de Segurança (Propriedade)
        // O ID do profissional dono do serviço é igual ao ID do profissional logado?
        if (!servico.getProfissional().getId().equals(profissional.getId())) {
            throw new RuntimeException("Acesso negado: Você não é o proprietário deste serviço.");
        }

        // 4. Se ele é o dono, atualiza os dados
        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setDuracaoMinutos(dto.getDuracaoMinutos());
        servico.setPreco(dto.getPreco());
        
        // 5. Salva as alterações
        Servico servicoAtualizado = servicoRepository.save(servico);
        return new ServicoResponseDTO(servicoAtualizado);
    }

    /**
     * REGRA DE NEGÓCIO 4: Deletar um serviço (verificando se o profissional é o dono)
     */
    public void deleteServico(UUID id) {
        // 1. Descobre quem está logado
        Usuario profissional = getLoggedUser();

        // 2. Busca o serviço no banco
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + id));

        // 3. Verificação de Segurança (Propriedade)
        if (!servico.getProfissional().getId().equals(profissional.getId())) {
            throw new RuntimeException("Acesso negado: Você não é o proprietário deste serviço.");
        }

        // 4. Se for o dono, deleta
        servicoRepository.delete(servico);
    }
}