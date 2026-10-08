package org.example.restaurante.dto;

import org.example.restaurante.model.StatusItemCompra;

import java.math.BigDecimal;

public class ItemCompraResumoDTO {

    private int id;
    private String nome_produto;
    private String descricao_produto;
    private BigDecimal quantidade_produto;
    private StatusItemCompra status;

    public ItemCompraResumoDTO() {
    }

    public ItemCompraResumoDTO(int id, String nome_produto, String descricao_produto,
                               BigDecimal quantidade_produto, StatusItemCompra status) {
        this.id = id;
        this.nome_produto = nome_produto;
        this.descricao_produto = descricao_produto;
        this.quantidade_produto = quantidade_produto;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getNome_produto() {
        return nome_produto;
    }

    public String getDescricao_produto() {
        return descricao_produto;
    }

    public BigDecimal getQuantidade_produto() {
        return quantidade_produto;
    }

    public StatusItemCompra getStatus() {
        return status;
    }
}
