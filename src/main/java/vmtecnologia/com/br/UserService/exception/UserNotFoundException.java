package vmtecnologia.com.br.UserService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando não é encontrado um usuário com o e-mail ou username informado.
 *<p>
 *É mapeada para resposta HTTP 401 Unauthorized.</p>
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotFoundException extends RuntimeException {

    /**
     * Cria a exceção com mensagem padrão contendo o identificador buscado.
     *
     * @param username e-mail ou nome de usuário que não foi encontrado
     */
    public UserNotFoundException(String username) {
        super("Usuário não encontrado: " + username);
    }
}
