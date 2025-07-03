package vmtecnologia.com.br.UserService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vmtecnologia.com.br.UserService.model.LoginRequestModel;
import vmtecnologia.com.br.UserService.model.TokenModel;
import vmtecnologia.com.br.UserService.service.AuthService;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Realiza login do usuário",
            description = "Recebe credenciais e retorna o token JWT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login bem sucedido",
                            content = @Content(schema = @Schema(implementation = TokenModel.class))),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TokenModel> login(@Valid @RequestBody LoginRequestModel loginRequestModel) {
        TokenModel authenticate = authService.authenticate(loginRequestModel);
        return ResponseEntity.ok(authenticate);
    }

}
