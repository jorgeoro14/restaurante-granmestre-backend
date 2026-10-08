package org.example.restaurante.service;

import org.example.restaurante.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long expiracaoEmMinutos;

    public TokenService(JwtEncoder jwtEncoder,
                        @Value("${jwt.expiration-minutes}") long expiracaoEmMinutos) {
        this.jwtEncoder = jwtEncoder;
        this.expiracaoEmMinutos = expiracaoEmMinutos;
    }

    public String gerarToken(Usuario usuario) {

        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getUsuario())
                .issuedAt(agora)
                .expiresAt(agora.plus(expiracaoEmMinutos, ChronoUnit.MINUTES))
                .claim("tipo_usuario", usuario.getTipo_usuario().name())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
