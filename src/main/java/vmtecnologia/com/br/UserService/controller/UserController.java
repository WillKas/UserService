package vmtecnologia.com.br.UserService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vmtecnologia.com.br.UserService.model.PageModel;
import vmtecnologia.com.br.UserService.model.request.UserModelRequest;
import vmtecnologia.com.br.UserService.model.response.UserModelResponse;
import vmtecnologia.com.br.UserService.service.UserService;

import java.util.Objects;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/user/api/v1")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Cria um novo usuário",
            description = "Cria um usuário baseado nas informações fornecidas no corpo da requisição.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso",
                            content = @Content(schema = @Schema(implementation = UserModelRequest.class))),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida")
            }
    )
    @PostMapping("/save")
    public ResponseEntity<UserModelResponse> saveUser(@RequestBody UserModelRequest userModelRequest) {
        log.info("saveUser() -> Recebendo email {} para criação de usuario.", userModelRequest.getEmail());

        UserModelResponse user = userService.createUser(userModelRequest);
        if (Objects.isNull(user)) {
            log.error("saveUser() -> Erro ao criar usuario com email {}", userModelRequest.getEmail());
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(
            summary = "Atualiza um usuário existente",
            description = "Atualiza os dados de um usuário cadastrado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                            content = @Content(schema = @Schema(implementation = UserModelRequest.class))),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida")
            }
    )
    @PutMapping("/update")
    public ResponseEntity<UserModelResponse> updateUser(@RequestBody UserModelRequest userModelRequest) {
        log.info("saveUser() -> Recebendo email {} para atualização de usuario.", userModelRequest);

        UserModelResponse user = userService.updateUser(userModelRequest);
        if (Objects.isNull(user)) {
            log.error("saveUser() -> Erro ao atualizar usuario com email {}", userModelRequest.getEmail());
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(
            summary = "Exclui um usuário",
            description = "Remove um usuário com o ID informado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
                    @ApiResponse(responseCode = "400", description = "ID inválido"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestParam Long id) {
        log.info("deleteUser() -> Recebendo id {} para exclusão de usuario.", id);

        if (Objects.isNull(id) || id <= 0) {
            log.error("deleteUser() -> ID inválido: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Lista usuários paginados",
            description = "Busca usuários com filtros opcionais e paginação.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada",
                            content = @Content(schema = @Schema(implementation = PageModel.class))),
                    @ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado"),
                    @ApiResponse(responseCode = "500", description = "Erro no servidor")
            }
    )
    @GetMapping("/findAll")
    public ResponseEntity<PageModel<UserModelResponse>> findAllUsers(@RequestParam(required = false) String username,
                                                                    @RequestParam(required = false) String email,
                                                                    @RequestParam(required = false) Boolean enabled,
                                                                    @RequestParam(defaultValue = "0") Integer pageNumber,
                                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("findAllUsers() -> Buscando listagem de usuarios.");

        try {
            PageModel<UserModelResponse> pageModel = userService.findAllUsers(username, email, enabled, pageNumber, pageSize);
            return ResponseEntity.ok(pageModel);

        } catch (EntityNotFoundException ex) {
            return ResponseEntity.noContent().build(); //capturando EntityNotFoundException antes do handler
        }
    }

    @Operation(
            summary = "Busca usuário por ID",
            description = "Retorna o usuário correspondente ao ID informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                            content = @Content(schema = @Schema(implementation = UserModelRequest.class))),
                    @ApiResponse(responseCode = "400", description = "ID inválido"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
            }
    )
    @GetMapping("/findById")
    public ResponseEntity<UserModelResponse> findById(@RequestParam Long id) {
        log.info("findById() -> Buscando usuario com ID {}", id);

        if (Objects.isNull(id) || id <= 0) {
            log.error("findById() -> ID inválido: {}", id);
            return ResponseEntity.badRequest().build();
        }

        UserModelResponse user = userService.findById(id);
        if (user == null) {
            log.warn("findById() -> Usuario com ID {} não encontrado.", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
