package vmtecnologia.com.br.UserService.exception;

/**
 * Exceção lançada quando o e-mail fornecido não corresponde ao formato válido.
 *<p>
 *É mapeada para resposta HTTP 400 Bad Request.</p>
 */
public class InvalidEmailException extends RuntimeException {

    /**
     * Cria a exceção com mensagem indicando o e-mail inválido.
     *
     * @param email e-mail que não passou na validação de formato
     */
    public InvalidEmailException(String email) {
        super("E-mail inválido: " + email);
    }
}
