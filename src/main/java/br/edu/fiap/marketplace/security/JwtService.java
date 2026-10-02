package br.edu.fiap.marketplace.security;

import br.edu.fiap.marketplace.dto.TokenResponse;
import br.edu.fiap.marketplace.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * TODO implementar a montagem do JWT com JwtEncoder.
 * A configuração criptográfica já está pronta em JwtConfig.
 */
@Component
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }


    public TokenResponse gerarToken(Usuario usuario){
        // Registra o momento atual em UTC e calcula quando o token expirará
        Instant emitidoEm = Instant.now();
        Instant expiraEm = emitidoEm.plusSeconds(expirationSeconds);

        // Cria o header informando que o objeto é um JWT assinado com HS256
        JwsHeader header= JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        //Monta o payload com claim padrao do JWT e dados minimos do usuario
        JwtClaimsSet claim = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(emitidoEm)
                .expiresAt(expiraEm)
                .subject(usuario.getEmail())
                .claim("usuarioId", usuario.getId())
                .claim("nome", usuario.getNome())
                .build();

        //Codifica header e payload, calcula a assinatura e monta o header.payload.signature
        String token = jwtEncoder
                .encode(JwtEncoderParameters.from(header,claim))
                .getTokenValue();

        return new TokenResponse(token, "Bearer", expirationSeconds, expiraEm);
    }
}
