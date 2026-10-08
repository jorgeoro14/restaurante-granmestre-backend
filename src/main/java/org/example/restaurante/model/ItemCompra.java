package org.example.restaurante.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_compra", schema = "transacoes")
public class ItemCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_compras")
    private Compra compra;
    private String nome_produto;
    private String descricao_produto;
    private BigDecimal quantidade_produto;

    @Enumerated(EnumType.STRING)
    private StatusItemCompra status;

    public ItemCompra() {
    }

    public int getId() {
        return id;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public String getNome_produto() {
        return nome_produto;
    }

    public void setNome_produto(String nome_produto) {
        this.nome_produto = nome_produto;
    }

    public String getDescricao_produto() {
        return descricao_produto;
    }

    public void setDescricao_produto(String descricao_produto) {
        this.descricao_produto = descricao_produto;
    }

    public BigDecimal getQuantidade_produto() {
        return quantidade_produto;
    }

    public void setQuantidade_produto(BigDecimal quantidade_produto) {
        this.quantidade_produto = quantidade_produto;
    }

    public StatusItemCompra getStatus() {
        return status;
    }

    public void setStatus(StatusItemCompra status) {
        this.status = status;
    }
}
