package vmtecnologia.com.br.UserService.exception;

/**
 * Exceção lançada quando a senha não é informada.
 *<p>
 *É mapeada para resposta HTTP 400 Bad Request.</p>
 */
public class PasswordNotProvidedException extends RuntimeException {

    /**
     * Cria a exceção informando que a senha não pode ser nula ou vazia.
     */
    public PasswordNotProvidedException() {
        super("A senha não pode ser nula ou vazia.");
    }
}
