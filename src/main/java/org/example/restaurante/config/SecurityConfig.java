package org.example.restaurante.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * RN02 - O acesso as funcionalidades depende do tipo de usuario.
 *
 * O login devolve um token JWT com o tipo_usuario dentro. Em cada requisicao
 * o front envia "Authorization: Bearer <token>" e as regras abaixo decidem
 * se o perfil (ADMIN, ATENDENTE ou COZINHEIRO) pode acessar o endpoint.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecretKey chave;

    public SecurityConfig(@Value("${jwt.secret}") String segredo) {
        this.chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // publico
                        .requestMatchers("/login", "/error").permitAll()

                        // somente ADMIN
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        // tipos de marmita e acompanhamentos: ATENDENTE so consulta
                        .requestMatchers(HttpMethod.GET, "/tipos-marmita/**", "/acompanhamentos/**")
                        .hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers("/tipos-marmita/**", "/acompanhamentos/**").hasRole("ADMIN")

                        // pedidos: todos consultam. COZINHEIRO so muda o STATUS, nunca itens, valores
                        // ou acompanhamentos. Os itens nascem junto com o pedido; /itens-pedido so escreve o ADMIN
                        .requestMatchers(HttpMethod.GET, "/pedidos/**", "/itens-pedido/**")
                        .hasAnyRole("ADMIN", "ATENDENTE", "COZINHEIRO")
                        .requestMatchers(HttpMethod.POST, "/pedidos/**")
                        .hasAnyRole("ADMIN", "ATENDENTE")
                        .requestMatchers(HttpMethod.PUT, "/pedidos/*/status")
                        .hasAnyRole("ADMIN", "ATENDENTE", "COZINHEIRO")
                        .requestMatchers("/pedidos/**", "/itens-pedido/**").hasRole("ADMIN")

                        // compras: COZINHEIRO gerencia
                        .requestMatchers("/compras/**", "/itens-compra/**")
                        .hasAnyRole("ADMIN", "COZINHEIRO")

                        // dashboard: pedidos para todos os perfis, compras so para quem gerencia compras
                        .requestMatchers(HttpMethod.GET, "/dashboard/resumo-pedidos")
                        .hasAnyRole("ADMIN", "ATENDENTE", "COZINHEIRO")
                        .requestMatchers(HttpMethod.GET, "/dashboard/resumo-compras")
                        .hasAnyRole("ADMIN", "COZINHEIRO")

                        // qualquer outra rota fica bloqueada
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenResolver(resolvedorDeToken())
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(conversorDePerfil())));

        return http.build();
    }

    // O /login nunca le o token: se o front mandar um token vencido ou invalido nele,
    // o Spring recusaria com 401 antes de o login ser processado.
    private BearerTokenResolver resolvedorDeToken() {
        DefaultBearerTokenResolver padrao = new DefaultBearerTokenResolver();
        return request -> "/login".equals(request.getServletPath()) ? null : padrao.resolve(request);
    }

    // Transforma o claim "tipo_usuario" do token em ROLE_ADMIN, ROLE_ATENDENTE ou ROLE_COZINHEIRO
    private JwtAuthenticationConverter conversorDePerfil() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("tipo_usuario");
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chave));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
