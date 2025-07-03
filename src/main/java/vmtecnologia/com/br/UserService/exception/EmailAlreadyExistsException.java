package vmtecnologia.com.br.UserService.exception;

/**
 * Exceção lançada quando já existe um usuário cadastrado com o mesmo e-mail.
 *<p>
 *É mapeada para resposta HTTP 409 Conflict.</p>
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Cria a exceção com mensagem indicando o e-mail duplicado.
     *
     * @param email e-mail que já está cadastrado no sistema
     */
    public EmailAlreadyExistsException(String email) {
        super("Já existe um usuário cadastrado com o e-mail: " + email);
    }
}

