package vmtecnologia.com.br.UserService.exception;

/**
 * Exceção lançada quando a senha não atende aos critérios de complexidade:
 * mínimo de 8 caracteres, ao menos um dígito, uma letra maiúscula e um caractere especial.
 *<p>
 *É mapeada para resposta HTTP 400 Bad Request.</p>
 */
public class InvalidPasswordException extends RuntimeException {

    /**
     * Cria a exceção informando o critério mínimo de segurança para senha.
     */
    public InvalidPasswordException() {
        super("A senha deve ter pelo menos 8 caracteres, conter ao menos um número, uma letra maiúscula e um caractere especial.");
    }
}
