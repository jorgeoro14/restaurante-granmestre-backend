package org.example.restaurante.impressao;

import org.example.restaurante.dto.ItemPedidoResponseDTO;
import org.example.restaurante.dto.PedidoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Monta o texto do cupom da cozinha (RN003): numero, horario, tipo de marmita,
 * acompanhamentos e observacoes.
 */
@Component
public class CupomFormatter {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final int colunas;

    // 48 colunas para papel de 80 mm, 32 para papel de 58 mm
    public CupomFormatter(@Value("${impressora.colunas:48}") int colunas) {
        this.colunas = colunas;
    }

    public Cupom formatar(PedidoResponseDTO pedido, boolean reimpressao) {

        String separador = "-".repeat(colunas);
        StringBuilder corpo = new StringBuilder();

        if (reimpressao) {
            corpo.append(centralizar("*** REIMPRESSAO ***")).append("\n");
        }
        corpo.append(separador).append("\n");
        corpo.append("Data: ").append(DATA.format(pedido.getData_hora()))
                .append("   Hora: ").append(HORA.format(pedido.getData_hora())).append("\n");
        if (pedido.getAtendente() != null) {
            corpo.append("Atendente: ").append(pedido.getAtendente()).append("\n");
        }
        corpo.append(separador).append("\n");

        corpo.append("MARMITA: ").append(pedido.getTipoMarmitaNome()).append("\n");
        corpo.append(separador).append("\n");

        corpo.append("ACOMPANHAMENTOS:\n");
        if (pedido.getItens().isEmpty()) {
            corpo.append("  (nenhum)\n");
        }
        for (ItemPedidoResponseDTO item : pedido.getItens()) {
            corpo.append("  ").append(item.getQuantidade()).append("x ")
                    .append(item.getAcompanhamentoNome()).append("\n");
        }
        corpo.append(separador).append("\n");

        corpo.append("OBSERVACAO:\n");
        String observacao = pedido.getObservacao();
        corpo.append(observacao == null || observacao.isBlank()
                ? "  (sem observacao)"
                : quebrarLinhas(observacao.trim()));
        corpo.append("\n").append(separador);

        return new Cupom("PEDIDO " + pedido.getNumero_pedido(), corpo.toString());
    }

    private String centralizar(String texto) {
        int espacos = Math.max(0, (colunas - texto.length()) / 2);
        return " ".repeat(espacos) + texto;
    }

    // quebra o texto para nao ser cortado na borda do papel
    private String quebrarLinhas(String texto) {

        StringBuilder resultado = new StringBuilder();
        StringBuilder linha = new StringBuilder();

        for (String palavra : texto.split("\\s+")) {
            while (palavra.length() > colunas) {
                if (linha.length() > 0) {
                    resultado.append(linha).append("\n");
                    linha.setLength(0);
                }
                resultado.append(palavra, 0, colunas).append("\n");
                palavra = palavra.substring(colunas);
            }
            if (linha.length() > 0 && linha.length() + 1 + palavra.length() > colunas) {
                resultado.append(linha).append("\n");
                linha.setLength(0);
            }
            if (linha.length() > 0) {
                linha.append(" ");
            }
            linha.append(palavra);
        }
        resultado.append(linha);

        return resultado.toString();
    }
}
