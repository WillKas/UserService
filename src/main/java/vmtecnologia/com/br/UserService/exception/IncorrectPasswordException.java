package vmtecnologia.com.br.UserService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando a senha fornecida está incorreta durante autenticação.
 *<p>
 *É mapeada para resposta HTTP 401 Unauthorized.</p>
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class IncorrectPasswordException extends RuntimeException {

    /**
     * Cria a exceção com mensagem padrão de senha incorreta.
     */
    public IncorrectPasswordException() {
        super("Senha incorreta. Por favor, tente novamente.");
    }
}
