package org.example.restaurante.dto;

public class QuantidadePorTipoDTO {

    private int tipoMarmitaId;
    private String tipoMarmitaNome;
    private long quantidade;

    public QuantidadePorTipoDTO() {
    }

    public QuantidadePorTipoDTO(int tipoMarmitaId, String tipoMarmitaNome, long quantidade) {
        this.tipoMarmitaId = tipoMarmitaId;
        this.tipoMarmitaNome = tipoMarmitaNome;
        this.quantidade = quantidade;
    }

    public int getTipoMarmitaId() {
        return tipoMarmitaId;
    }

    public String getTipoMarmitaNome() {
        return tipoMarmitaNome;
    }

    public long getQuantidade() {
        return quantidade;
    }
}
