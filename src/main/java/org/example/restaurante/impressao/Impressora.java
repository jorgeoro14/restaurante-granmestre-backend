package org.example.restaurante.impressao;

/**
 * A impressora da cozinha. O resto do sistema so conhece esta interface,
 * entao trocar o modelo ou a forma de conexao nao mexe nas regras de pedido.
 *
 * Implementacoes escolhidas pela propriedade impressora.modo:
 * - simulada (padrao): grava o cupom em arquivo, para desenvolvimento e testes
 * - rede: envia comandos ESC/POS por TCP para uma impressora termica
 */
public interface Impressora {

    void imprimir(Cupom cupom) throws ImpressoraException;
}
