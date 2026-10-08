package org.example.restaurante.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Troca as mensagens tecnicas do Spring (que mostram nomes de classes Java)
 * por mensagens que o front pode exibir. O formato da resposta e o mesmo dos
 * demais erros: timestamp, status, error, message e path.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    // Ex.: {"status": "BANANA"} no corpo da requisicao
    private static final Pattern VALORES_ACEITOS = Pattern.compile("Enum class: \\[(.*?)\\]");
    private static final Pattern CAMPO = Pattern.compile("\\[\"(\\w+)\"\\]\\)");
    private static final Pattern VALOR_ENVIADO = Pattern.compile("from String \"(.*?)\"");

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> corpoInvalido(HttpMessageNotReadableException e,
                                                             HttpServletRequest request) {

        String detalhe = String.valueOf(e.getMostSpecificCause().getMessage())
                + " " + String.valueOf(e.getMessage());

        Matcher aceitos = VALORES_ACEITOS.matcher(detalhe);
        if (aceitos.find()) {
            Matcher campo = CAMPO.matcher(detalhe);
            Matcher enviado = VALOR_ENVIADO.matcher(detalhe);

            String nomeCampo = campo.find() ? "'" + campo.group(1) + "'" : "enviado";
            String valor = enviado.find() ? " \"" + enviado.group(1) + "\"" : "";

            return erro(request, "Valor" + valor + " inválido para o campo " + nomeCampo
                    + ". Valores aceitos: " + aceitos.group(1));
        }

        return erro(request, "Corpo da requisição inválido. Confira o formato do JSON e os tipos dos campos.");
    }

    // Ex.: GET /pedidos?status=BANANA ou ?data=ontem
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> parametroInvalido(MethodArgumentTypeMismatchException e,
                                                                 HttpServletRequest request) {

        Class<?> tipo = e.getRequiredType();

        if (tipo != null && tipo.isEnum()) {
            return erro(request, "Valor \"" + e.getValue() + "\" inválido para o parâmetro '" + e.getName()
                    + "'. Valores aceitos: " + String.join(", ",
                    Arrays.stream(tipo.getEnumConstants()).map(Object::toString).toList()));
        }

        return erro(request, "Valor \"" + e.getValue() + "\" inválido para o parâmetro '" + e.getName() + "'");
    }

    private ResponseEntity<Map<String, Object>> erro(HttpServletRequest request, String mensagem) {

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now().toString());
        corpo.put("status", HttpStatus.BAD_REQUEST.value());
        corpo.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        corpo.put("message", mensagem);
        corpo.put("path", request.getRequestURI());

        return ResponseEntity.badRequest().body(corpo);
    }
}
