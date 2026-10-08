package org.example.restaurante.dto;

public class ItemPedidoRequestDTO {

    private Integer acompanhamentoId;
    private int quantidade;

    public ItemPedidoRequestDTO() {
    }

    public Integer getAcompanhamentoId() {
        return acompanhamentoId;
    }

    public void setAcompanhamentoId(Integer acompanhamentoId) {
        this.acompanhamentoId = acompanhamentoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
