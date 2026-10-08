package org.example.restaurante.impressao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(name = "impressora.modo", havingValue = "simulada", matchIfMissing = true)
public class ImpressoraSimulada implements Impressora {

    private static final Logger log = LoggerFactory.getLogger(ImpressoraSimulada.class);

    private final Path arquivo;
    private final boolean falhar;

    public ImpressoraSimulada(
            @Value("${impressora.simulada.pasta:impressoes}") String pasta,
            @Value("${impressora.simulada.falhar:false}") boolean falhar) {
        this.arquivo = Path.of(pasta, "cupons-simulados.txt");
        this.falhar = falhar;
    }

    @Override
    public synchronized void imprimir(Cupom cupom) throws ImpressoraException {

        // permite testar o cenario "impressora indisponivel" sem ter impressora
        if (falhar) {
            throw new ImpressoraException("Impressora indisponível (simulação de falha ativa)");
        }

        String texto = "[" + LocalDateTime.now() + "]\n"
                + cupom.getTitulo() + "\n"
                + cupom.getCorpo() + "\n\n\n";

        try {
            Files.createDirectories(arquivo.getParent());
            Files.writeString(arquivo, texto, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new ImpressoraException("Não foi possível gravar o cupom simulado", e);
        }

        log.info("Cupom simulado gravado em {}", arquivo.toAbsolutePath());
    }
}
