package vmtecnologia.com.br.UserService.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa um usuário de requisição")
public class UserModelRequest {

    @Schema(description = "Nome de usuário único", example = "usuario123", required = true)
    @NotBlank(message = "O username é obrigatório")
    private String username;

    @Schema(description = "Email do usuário", example = "email@dominio.com", required = true)
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @Schema(
            description = "Senha do usuário — mínimo 8 caracteres, ao menos uma letra maiúscula, um dígito e um caractere especial",
            example = "SenhaSegura1!",
            required = true
    )
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter ao menos 8 caracteres")
    @Pattern(
            regexp = "^(?=.{8,}$)(?=.*\\d)(?=.*[A-Z])(?=.*[^\\w\\s]).+$",
            message = "A senha deve conter ao menos uma letra maiúscula, um número e um caractere especial"
    )
    private String password;

    @Schema(description = "usuario habilitado", example = "true", required = false)
    private Boolean enabled;

}

