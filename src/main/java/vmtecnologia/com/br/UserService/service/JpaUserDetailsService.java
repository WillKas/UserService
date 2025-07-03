package vmtecnologia.com.br.UserService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vmtecnologia.com.br.UserService.entity.UserEntity;

/**
 * Implementação de {@link UserDetailsService} que carrega
 * dados do usuário a partir do serviço de domínio {@link UserService}.
 */
@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * Carrega os detalhes de um usuário com base no e-mail (username).
     *
     * @param username e-mail do usuário
     * @return {@link UserDetails} contendo credenciais e autoridades
     * @throws UsernameNotFoundException se não houver usuário habilitado com esse e-mail
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity u = userService.findByEmailAndEnabled(username, Boolean.TRUE)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return User.withUsername(u.getEmail())
                .password(u.getPassword()) // ou BCrypt
                .authorities("USER")
                .build();
    }
}
