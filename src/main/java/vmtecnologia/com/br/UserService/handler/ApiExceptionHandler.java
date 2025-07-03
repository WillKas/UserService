package vmtecnologia.com.br.UserService.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vmtecnologia.com.br.UserService.exception.*;
import vmtecnologia.com.br.UserService.model.response.GenericErrorModelResponse;

import java.time.Instant;

/**
 * Intercepta exceções lançadas pelos controllers e serviços,
 * mapeando-as para respostas HTTP padronizadas com corpo do tipo
 * {@link GenericErrorModelResponse}.
 *
 * Cada método trata uma exceção específica e define o status HTTP,
 * a mensagem de erro e o caminho da requisição.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Trata falha de autenticação por credenciais inválidas.
     *
     * @param ex exceção lançada pelo Spring Security ao validar credenciais
     * @return resposta 401 Unauthorized com detalhes do erro
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GenericErrorModelResponse> onBadCreds(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new GenericErrorModelResponse(
                        Instant.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Credenciais inválidas",
                        ex.getMessage(),
                        null
                ));
    }

    /**
     * Trata exceção de usuário não encontrado ou entidade ausente.
     *
     * @param ex      exceção personalizada {@link UserNotFoundException}
     * @param request objeto HTTP para recuperar o URI da requisição
     * @return resposta 404 Not Found com detalhes do erro e caminho da requisição
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GenericErrorModelResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        GenericErrorModelResponse response = new GenericErrorModelResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Usuario/Entidade não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Trata erros de validação de parâmetros anotados com @Valid.
     *
     * @param ex      exceção lançada pelo Spring ao validar argumentos
     * @param request objeto HTTP para recuperar o URI da requisição
     * @return resposta 400 Bad Request com detalhes do erro e caminho da requisição
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericErrorModelResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        GenericErrorModelResponse response = new GenericErrorModelResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Trata caso em que a senha não foi informada.
     *
     * @param ex      exceção personalizada {@link PasswordNotProvidedException}
     * @param request objeto HTTP para recuperar o URI da requisição
     * @return resposta 400 Bad Request indicando a ausência da senha
     */
    @ExceptionHandler(PasswordNotProvidedException.class)
    public ResponseEntity<GenericErrorModelResponse> handlePasswordNotProvided(
            PasswordNotProvidedException ex, HttpServletRequest request) {

        GenericErrorModelResponse response = new GenericErrorModelResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Senha inválida",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Trata e-mail inválido informado no request.
     *
     * @param ex  exceção personalizada {@link InvalidEmailException}
     * @param req objeto HTTP para recuperar o URI da requisição
     * @return resposta 400 Bad Request indicando e-mail com formato inválido
     */
    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<GenericErrorModelResponse> handleInvalidEmail(
            InvalidEmailException ex, HttpServletRequest req) {
        GenericErrorModelResponse response = new GenericErrorModelResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "E-mail inválido",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Trata falta de nome de usuário ou campo em branco.
     *
     * @param ex  exceção personalizada {@link UsernameNotProvidedException}
     * @param req objeto HTTP para recuperar o URI da requisição
     * @return resposta 400 Bad Request indicando username inválido
     */
    @ExceptionHandler(UsernameNotProvidedException.class)
    public ResponseEntity<GenericErrorModelResponse> handleUsernameNotProvided(
            UsernameNotProvidedException ex, HttpServletRequest req) {
        GenericErrorModelResponse response = new GenericErrorModelResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Nome de usuário inválido",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Trata senha que não atende aos critérios de complexidade.
     *
     * @param ex  exceção personalizada {@link InvalidPasswordException}
     * @param req objeto HTTP para recuperar o URI da requisição
     * @return resposta 400 Bad Request indicando senha inválida
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<GenericErrorModelResponse> handleInvalidPassword(
            InvalidPasswordException ex, HttpServletRequest req) {

        GenericErrorModelResponse response = new GenericErrorModelResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Senha inválida",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Trata tentativa de cadastro com e-mail já existente.
     *
     * @param ex  exceção personalizada {@link EmailAlreadyExistsException}
     * @param req objeto HTTP para recuperar o URI da requisição
     * @return resposta 409 Conflict indicando duplicidade de e-mail
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<GenericErrorModelResponse> handleEmailExists(
            EmailAlreadyExistsException ex, HttpServletRequest req) {

        GenericErrorModelResponse response = new GenericErrorModelResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "E-mail já cadastrado",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Trata senha incorreta durante autenticação.
     *
     * @param ex  exceção personalizada {@link IncorrectPasswordException}
     * @param req objeto HTTP para recuperar o URI da requisição
     * @return resposta 401 Unauthorized indicando senha incorreta
     */
    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<GenericErrorModelResponse> handleIncorrectPassword(
            IncorrectPasswordException ex, HttpServletRequest req) {

        GenericErrorModelResponse response = new GenericErrorModelResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Senha incorreta",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Tratamento genérico para outras exceções não mapeadas.
     *
     * @param ex      exceção genérica
     * @param request objeto HTTP para recuperar o URI da requisição
     * @return resposta 500 Internal Server Error com detalhes do erro
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericErrorModelResponse> handleAll(Exception ex, HttpServletRequest request) {
        GenericErrorModelResponse response = new GenericErrorModelResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
