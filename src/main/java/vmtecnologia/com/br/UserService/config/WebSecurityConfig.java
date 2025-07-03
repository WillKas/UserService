package vmtecnologia.com.br.UserService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vmtecnologia.com.br.UserService.filter.AuthenticationFilter;
import vmtecnologia.com.br.UserService.service.JpaUserDetailsService;
import vmtecnologia.com.br.UserService.service.JwtService;

/**
 * Configuração de segurança da aplicação, definindo políticas de autenticação
 * e autorização para endpoints REST.
 *
 * <p>Desabilita CSRF, aplica sessãoless via JWT, e configura filtros
 * e rotas abertas (Swagger, login e cadastro).</p>
 */
@Configuration
public class WebSecurityConfig {

    /**
     * Define a cadeia de filtros de segurança (SecurityFilterChain) do Spring Security.
     *
     * @param http                  objeto de configuração HTTP oferecido pelo Spring
     * @param jwtService            serviço para manipulação de tokens JWT
     * @param jpaUserDetailsService serviço que carrega detalhes do usuário para autenticação
     * @return instância de {@link SecurityFilterChain} configurada
     * @throws Exception em caso de falha na criação do filtro
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            JpaUserDetailsService jpaUserDetailsService) throws Exception {

        // Instancia o filtro JWT antes do UsernamePasswordAuthenticationFilter
        AuthenticationFilter jwtFilter = new AuthenticationFilter(jwtService, jpaUserDetailsService);

        http
                // Desabilita proteção CSRF para APIs stateless
                .csrf(csrf -> csrf.disable())
                // Define politica de sessão sem estado (stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configura endpoints públicos e protegidos
                .authorizeHttpRequests(auth -> auth
                        // rotas de documentação e necessarias
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",   // se você usa o endpoint /v3
                                "/v1/api-docs/**",   // e/ou se configurou /v1/api-docs
                                "/auth/**",       // login para usuario ja cadastrado
                                "/user/api/v1/save"  // cadastro de usuario
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // Desabilita HTTP Basic
                .httpBasic(httpBasic -> httpBasic.disable())
                // Adiciona o filtro JWT antes do filtro de autenticação padrão
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Registra o {@link PasswordEncoder} usado para codificar senhas.
     *
     * @return instância de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura o {@link AuthenticationManager} baseado na configuração padrão do Spring.
     *
     * @param config configuração de autenticação do Spring
     * @return instância de {@link AuthenticationManager}
     * @throws Exception em caso de falha ao recuperar o AuthenticationManager
     */
    @Bean
    public AuthenticationManager authManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
