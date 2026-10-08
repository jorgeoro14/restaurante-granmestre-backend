package org.example.restaurante.dto;

public class ItemPedidoResponseDTO {

    private int id;
    private int acompanhamentoId;
    private String acompanhamentoNome;
    private int quantidade;

    public ItemPedidoResponseDTO() {
    }

    public ItemPedidoResponseDTO(int id, int acompanhamentoId, String acompanhamentoNome, int quantidade) {
        this.id = id;
        this.acompanhamentoId = acompanhamentoId;
        this.acompanhamentoNome = acompanhamentoNome;
        this.quantidade = quantidade;
    }

    public int getId() {
        return id;
    }

    public int getAcompanhamentoId() {
        return acompanhamentoId;
    }

    public String getAcompanhamentoNome() {
        return acompanhamentoNome;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
