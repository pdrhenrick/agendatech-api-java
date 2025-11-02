package br.com.pedrohenrick.agendatech.service;

import br.com.pedrohenrick.agendatech.model.Usuario;
import io.jsonwebtoken.Jwts; // <- Mudou
import io.jsonwebtoken.security.Keys; // <- Mudou
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey; // <- Mudou
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date; // <- Mudou

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.issuer}")
    private String issuer;

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("-03:00"); // Fuso de Brasília

    /**
     * Método 1: GERAR O TOKEN (usando a biblioteca jjwt)
     */
    public String generateToken(Usuario usuario) {
        try {
            // 1. Pega a chave secreta e transforma em um objeto 'Key' seguro
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            // 2. Define a data de expiração
            Date expirationDate = Date.from(generateExpirationDate());

            // 3. Constrói o token
            return Jwts.builder()
                    .issuer(issuer)
                    .subject(usuario.getEmail()) // Define o "dono" do token
                    .issuedAt(new Date()) // Define quando foi criado
                    .expiration(expirationDate) // Define a validade
                    // Você pode adicionar "claims" (dados extras) aqui:
                    // .claim("role", usuario.getRole().toString())
                    .signWith(key) // 4. Assina com a chave
                    .compact(); // 5. Constrói a String do token

        } catch (Exception e) {
            throw new RuntimeException("Erro: Não foi possível gerar o token JWT.", e);
        }
    }

    /**
     * Método 2: VALIDAR O TOKEN (usando a biblioteca jjwt)
     */
    public String validateToken(String token) {
        try {
            // 1. Pega a mesma chave secreta
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            // 2. Cria o "parser" (analisador) do token
            return Jwts.parser()
                    .verifyWith(key) // 3. Define a chave para verificar a assinatura
                    .requireIssuer(issuer) // Verifica se o "emissor" é o mesmo
                    .build()
                    .parseSignedClaims(token) // 4. Tenta analisar o token
                    .getPayload()
                    .getSubject(); // 5. Se tudo der certo, retorna o "dono" (o email)

        } catch (Exception e) {
            // 6. Se a verificação falhar (token inválido, expirado, etc.)
            return ""; 
        }
    }

    /**
     * Método Auxiliar: Define a data de validade do token (2 horas)
     */
    private Instant generateExpirationDate() {
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZONE_OFFSET);
    }
}