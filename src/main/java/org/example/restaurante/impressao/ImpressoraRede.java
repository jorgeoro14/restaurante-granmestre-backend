package org.example.restaurante.impressao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;

/**
 * Impressora termica de cupom (Elgin, Bematech e similares) que entende ESC/POS
 * e aceita conexao TCP direta, normalmente na porta 9100.
 *
 * ATENCAO: ainda nao foi testada com impressora fisica, so contra um servidor TCP
 * que captura os bytes. Os comandos usados sao os mais comuns do ESC/POS.
 */
@Component
@ConditionalOnProperty(name = "impressora.modo", havingValue = "rede")
public class ImpressoraRede implements Impressora {

    private static final byte ESC = 0x1B;
    private static final byte GS = 0x1D;

    private final String host;
    private final int porta;
    private final int timeoutMs;
    private final boolean removerAcentos;
    private final int tabelaCaracteres;

    public ImpressoraRede(
            @Value("${impressora.host:}") String host,
            @Value("${impressora.porta:9100}") int porta,
            @Value("${impressora.timeout-ms:3000}") int timeoutMs,
            @Value("${impressora.remover-acentos:true}") boolean removerAcentos,
            @Value("${impressora.tabela-caracteres:3}") int tabelaCaracteres) {

        if (host == null || host.isBlank()) {
            throw new IllegalStateException("Defina impressora.host quando impressora.modo=rede");
        }
        this.host = host;
        this.porta = porta;
        this.timeoutMs = timeoutMs;
        this.removerAcentos = removerAcentos;
        this.tabelaCaracteres = tabelaCaracteres;
    }

    @Override
    public void imprimir(Cupom cupom) throws ImpressoraException {

        byte[] comandos = montarComandos(cupom);

        // conectar ja confirma que a impressora esta ao alcance antes de enviar
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, porta), timeoutMs);
            socket.setSoTimeout(timeoutMs);

            OutputStream saida = socket.getOutputStream();
            saida.write(comandos);
            saida.flush();

        } catch (SocketTimeoutException e) {
            throw new ImpressoraException("A impressora não respondeu (" + host + ":" + porta + ")", e);
        } catch (IOException e) {
            throw new ImpressoraException("Impressora indisponível em " + host + ":" + porta, e);
        }
    }

    private byte[] montarComandos(Cupom cupom) {

        // sem acentos funciona em qualquer modelo; com acentos usa a tabela IBM860 (portugues)
        Charset charset = removerAcentos ? StandardCharsets.US_ASCII : Charset.forName("IBM860");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bytes.writeBytes(new byte[]{ESC, 0x40});                          // inicializa a impressora
        if (!removerAcentos) {
            bytes.writeBytes(new byte[]{ESC, 0x74, (byte) tabelaCaracteres}); // escolhe a tabela de caracteres
        }

        bytes.writeBytes(new byte[]{ESC, 0x61, 0x01});                    // centralizado
        bytes.writeBytes(new byte[]{GS, 0x21, 0x11});                     // fonte grande (2x largura e altura)
        bytes.writeBytes(codificar(cupom.getTitulo() + "\n", charset));
        bytes.writeBytes(new byte[]{GS, 0x21, 0x00});                     // fonte normal
        bytes.writeBytes(new byte[]{ESC, 0x61, 0x00});                    // alinhado a esquerda

        bytes.writeBytes(codificar(cupom.getCorpo() + "\n\n\n\n", charset));
        bytes.writeBytes(new byte[]{GS, 0x56, 0x42, 0x00});               // corte parcial

        return bytes.toByteArray();
    }

    private byte[] codificar(String texto, Charset charset) {
        String tratado = removerAcentos
                ? Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                : texto;
        return tratado.getBytes(charset);
    }
}
