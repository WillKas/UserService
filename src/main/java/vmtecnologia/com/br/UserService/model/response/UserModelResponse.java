package vmtecnologia.com.br.UserService.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo que representa um usuário de resposta")
public class UserModelResponse {

    @Schema(description = "ID do usuário", example = "1", required = true)
    private Long id;

    @Schema(description = "usuario habilitado", example = "true", required = false)
    private Boolean enabled;

    @Schema(description = "Nome de usuário único", example = "usuario123", required = true)
    @NotBlank(message = "O username é obrigatório")
    private String username;

    @Schema(description = "Email do usuário", example = "email@dominio.com", required = true)
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

}
