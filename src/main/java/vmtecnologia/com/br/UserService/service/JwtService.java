package vmtecnologia.com.br.UserService.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import vmtecnologia.com.br.UserService.entity.UserEntity;

import java.util.Date;

/**
 * Componente responsável por operações de geração e validação de tokens JWT.
 *
 * <p>Utiliza o segredo configurado em <code>app.jwt.secret</code> e tempo de
 * expiração em <code>app.jwt.expiration</code> para assinatura e verificação
 * de tokens.</p>
 */
@Component
public class JwtService {

    /**
     * Chave secreta para assinatura dos tokens JWT, definida em properties.
     */
    @Value("${app.jwt.secret}")
    private String secret;

    /**
     * Tempo de expiração do token em milissegundos (por exemplo, 3600000 = 1h).
     */
    @Value("${app.jwt.expiration}") // ex: 3600000ms = 1h
    private long expiration;

    /**
     * Gera um token JWT para o usuário fornecido.
     *
     * <p>Define o <strong>subject</strong> como o e-mail do usuário, a data de emissão
     * e a data de expiração calculada a partir do tempo atual mais o valor configurado.</p>
     *
     * @param user entidade de usuário, cujo e-mail será usado como subject
     * @return token JWT assinado em formato compacto (String)
     */
    public String generateToken(UserEntity user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida se um token JWT é válido para o usuário fornecido.
     *
     * @param token string do token JWT a ser validado
     * @param user  detalhes do usuário (username e credenciais) para comparar o subject
     * @return <code>true</code> se o subject do token corresponder ao username do usuário
     *         e o token não estiver expirado; <code>false</code> caso contrário
     */
    public boolean validateToken(String token, UserDetails user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Extrai o nome de usuário (subject) de um token JWT.
     *
     * @param token string do token JWT
     * @return valor do <code>subject</code> contido no token (neste caso, e-mail)
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Verifica se um token JWT está expirado.
     *
     * @param token string do token JWT a ser verificado
     * @return <code>true</code> se a data de expiração do token for anterior
     *         à data atual; <code>false</code> caso contrário
     */
    private boolean isTokenExpired(String token) {
        Date exp = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return exp.before(new Date());
    }


}
