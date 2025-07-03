package vmtecnologia.com.br.UserService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vmtecnologia.com.br.UserService.entity.UserEntity;
import vmtecnologia.com.br.UserService.exception.IncorrectPasswordException;
import vmtecnologia.com.br.UserService.exception.UserNotFoundException;
import vmtecnologia.com.br.UserService.model.LoginRequestModel;
import vmtecnologia.com.br.UserService.model.TokenModel;

import java.util.Objects;
import java.util.Optional;

/**
 * Serviço responsável pela autenticação de usuários,
 * verificando credenciais e gerando tokens JWT.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder encoder;

    /**
     * Autentica um usuário com base em e-mail e senha,
     * retornando um token JWT quando credenciais válidas.
     *
     * @param loginRequestModel objeto contendo e-mail e senha
     * @return {@link TokenModel} com o JWT gerado
     * @throws IllegalArgumentException   se e-mail ou senha não forem fornecidos
     * @throws UserNotFoundException      se não existir usuário habilitado com o e-mail informado
     * @throws IncorrectPasswordException se a senha estiver incorreta
     */
    public TokenModel authenticate(LoginRequestModel loginRequestModel) {
        if(Objects.isNull(loginRequestModel.getEmail()) || Objects.isNull(loginRequestModel.getPassword())) {
            throw new IllegalArgumentException("email e senha devem ser fornecidos");
        }

        Optional<UserEntity> user = userService.findByEmailAndEnabled(loginRequestModel.getEmail(), Boolean.TRUE);

        if(user.isEmpty()) {
            throw new UserNotFoundException(loginRequestModel.getEmail());
        }

        boolean matches = encoder.matches(loginRequestModel.getPassword(), user.get().getPassword());

        if(!matches) {
            throw new IncorrectPasswordException();
        }

        String token = jwtService.generateToken(user.get());

        log.info("Usuário autenticado com sucesso: {}", user.get().getEmail());

        return TokenModel.builder()
                .token(token)
                .build();

    }
}
