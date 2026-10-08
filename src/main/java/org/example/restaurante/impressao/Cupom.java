package org.example.restaurante.impressao;

/**
 * Texto de um cupom: o titulo sai em destaque (grande e centralizado)
 * e o corpo sai em tamanho normal.
 */
public class Cupom {

    private final String titulo;
    private final String corpo;

    public Cupom(String titulo, String corpo) {
        this.titulo = titulo;
        this.corpo = corpo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCorpo() {
        return corpo;
    }
}
