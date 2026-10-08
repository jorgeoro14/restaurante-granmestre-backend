package org.example.restaurante.dto;

import java.util.List;

public class PedidoRequestDTO {

    private Integer tipoMarmitaId;
    private String observacao;
    private List<ItemPedidoRequestDTO> itens;

    public PedidoRequestDTO() {
    }

    public Integer getTipoMarmitaId() {
        return tipoMarmitaId;
    }

    public void setTipoMarmitaId(Integer tipoMarmitaId) {
        this.tipoMarmitaId = tipoMarmitaId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItemPedidoRequestDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequestDTO> itens) {
        this.itens = itens;
    }
}
