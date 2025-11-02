package br.com.pedrohenrick.agendatech.model;

import br.com.pedrohenrick.agendatech.enums.StatusAgendamento;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime; // Usamos LocalDateTime para guardar Data e Hora (ex: 2025-10-30T14:30:00)
import java.util.UUID;

@Data
@Entity
@Table(name = "TB_AGENDAMENTOS")
public class Agendamento implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(nullable = false)
    private LocalDateTime dataHoraFim;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAgendamento status; // Ex: AGENDADO

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente; // O cliente do agendamento

    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Usuario profissional; // O profissional que vai atender

    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico; // O serviço que foi agendado
}