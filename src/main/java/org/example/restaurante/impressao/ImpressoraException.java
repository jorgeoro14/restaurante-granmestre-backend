package org.example.restaurante.impressao;

public class ImpressoraException extends Exception {

    public ImpressoraException(String mensagem) {
        super(mensagem);
    }

    public ImpressoraException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
