package org.example.restaurante.dto;

import org.example.restaurante.model.StatusPedido;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponseDTO {

    private int id;
    private int numero_pedido;
    private LocalDateTime data_hora;
    private StatusPedido status;
    private int tipoMarmitaId;
    private String tipoMarmitaNome;
    private String observacao;
    private String atendente;
    private boolean impresso;
    private List<ItemPedidoResponseDTO> itens;

    public PedidoResponseDTO() {
    }

    public PedidoResponseDTO(int id, int numero_pedido, LocalDateTime data_hora, StatusPedido status,
                             int tipoMarmitaId, String tipoMarmitaNome, String observacao,
                             String atendente, boolean impresso, List<ItemPedidoResponseDTO> itens) {
        this.id = id;
        this.numero_pedido = numero_pedido;
        this.data_hora = data_hora;
        this.status = status;
        this.tipoMarmitaId = tipoMarmitaId;
        this.tipoMarmitaNome = tipoMarmitaNome;
        this.observacao = observacao;
        this.atendente = atendente;
        this.impresso = impresso;
        this.itens = itens;
    }

    public int getId() {
        return id;
    }

    public int getNumero_pedido() {
        return numero_pedido;
    }

    public LocalDateTime getData_hora() {
        return data_hora;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public int getTipoMarmitaId() {
        return tipoMarmitaId;
    }

    public String getTipoMarmitaNome() {
        return tipoMarmitaNome;
    }

    public String getObservacao() {
        return observacao;
    }

    public String getAtendente() {
        return atendente;
    }

    public boolean isImpresso() {
        return impresso;
    }

    public List<ItemPedidoResponseDTO> getItens() {
        return itens;
    }
}
