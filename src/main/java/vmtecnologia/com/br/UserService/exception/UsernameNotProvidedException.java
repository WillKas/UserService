package vmtecnologia.com.br.UserService.exception;

/**
 * Exceção lançada quando o nome de usuário não é fornecido ou está em branco.
 *<p>
 *É mapeada para resposta HTTP 400 Bad Request.</p>
 */
public class UsernameNotProvidedException extends RuntimeException {

    /**
     * Cria a exceção informando que o username é obrigatório.
     */
    public UsernameNotProvidedException() {
        super("O nome de usuário não pode ficar em branco.");
    }
}
