package vmtecnologia.com.br.UserService.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vmtecnologia.com.br.UserService.service.JwtService;

import java.io.IOException;

/**
 * Filtro de autenticação JWT que intercepta requisições HTTP
 * para extrair e validar o token JWT presente no cabeçalho
 * Authorization.
 *
 * <p>Para cada requisição, verifica se o token começa com "Bearer ".
 * Se presente e válido, carrega o usuário associado e preenche
 * o contexto de segurança do Spring com a autenticação.</p>
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsSvc;

    /**
     * Construtor do filtro, recebendo dependências de JWT e serviço de usuário.
     *
     * @param jwtService      serviço para operações com JWT (extração e validação)
     * @param uds  serviço que carrega detalhes do usuário a partir do username (e-mail)
     */
    public AuthenticationFilter(JwtService jwtService, UserDetailsService uds) {
        this.jwtService = jwtService;
        this.userDetailsSvc = uds;
    }

    /**
     * Executado para cada requisição HTTP. Tenta extrair o token JWT do cabeçalho
     * "Authorization" e, se válido, autentica o usuário no contexto de segurança.
     *
     * @param req   objeto de requisição HTTP contendo cabeçalhos e dados da requisição
     * @param res   objeto de resposta HTTP para envio de dados ao cliente
     * @param chain cadeia de filtros para delegar a próxima etapa do processamento
     * @throws ServletException em caso de erro no processamento do filtro
     * @throws IOException      em caso de falha de I/O ao ler ou escrever dados
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = jwtService.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsSvc.loadUserByUsername(username);
                if (jwtService.validateToken(token, user)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        // Continua a cadeia de filtros mesmo que não haja autenticação
        chain.doFilter(req, res);
    }
}

