package vmtecnologia.com.br.UserService.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo para requisição de login")
public class LoginRequestModel {

    @Schema(description = "Email do usuário para login", example = "email@dominio.com", required = true)
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Schema(description = "Senha do usuário para login", example = "SenhaSegura1!", required = true)
    @NotBlank(message = "A senha é obrigatória")
    private String password;

}
