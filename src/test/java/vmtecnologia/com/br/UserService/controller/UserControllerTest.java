package vmtecnologia.com.br.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import vmtecnologia.com.br.UserService.filter.AuthenticationFilter;
import vmtecnologia.com.br.UserService.model.PageModel;
import vmtecnologia.com.br.UserService.model.request.UserModelRequest;
import vmtecnologia.com.br.UserService.model.response.UserModelResponse;
import vmtecnologia.com.br.UserService.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        // não escanear o AuthenticationFilter
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthenticationFilter.class
        ),
        // desativar TODA a auto-configuração de segurança
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)  // desabilita filtros de segurança
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("POST /user/api/v1/save")
    class SaveUserTests {
        @Test
        @DisplayName("– Sucesso: retorna 200 com o usuário criado")
        void saveSuccess() throws Exception {
            UserModelRequest req = UserModelRequest.builder()
                    .username("joao")
                    .email("joao@ex.com")
                    .password("SenhaSegura1!")
                    .build();
            UserModelResponse resp = UserModelResponse.builder()
                    .id(1L).username("joao").email("joao@ex.com").enabled(true).build();
            given(userService.createUser(any(UserModelRequest.class))).willReturn(resp);

            mvc.perform(post("/user/api/v1/save")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.username").value("joao"));
        }

        @Test
        @DisplayName("– Service retorna null -> 400")
        void saveServiceNull() throws Exception {
            UserModelRequest req = UserModelRequest.builder()
                    .username("joao")
                    .email("joao@ex.com")
                    .password("SenhaSegura1!")
                    .build();
            given(userService.createUser(any())).willReturn(null);

            mvc.perform(post("/user/api/v1/save")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /user/api/v1/update")
    class UpdateUserTests {
        @Test
        @DisplayName("– Sucesso: retorna 200 com o usuário atualizado")
        void updateSuccess() throws Exception {
            UserModelRequest req = UserModelRequest.builder()
                    .username("maria")
                    .email("maria@ex.com")
                    .password("OutraSenha1!")
                    .build();
            UserModelResponse resp = UserModelResponse.builder()
                    .id(2L).username("maria").email("maria@ex.com").enabled(false).build();
            given(userService.updateUser(any())).willReturn(resp);

            mvc.perform(put("/user/api/v1/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.enabled").value(false));
        }

        @Test
        @DisplayName("– Service retorna null -> 400")
        void updateServiceNull() throws Exception {
            UserModelRequest req = UserModelRequest.builder()
                    .username("maria")
                    .email("maria@ex.com")
                    .password("OutraSenha1!")
                    .build();
            given(userService.updateUser(any())).willReturn(null);

            mvc.perform(put("/user/api/v1/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /user/api/v1/delete")
    class DeleteUserTests {
        @Test
        @DisplayName("– Sucesso: retorna 200")
        void deleteSuccess() throws Exception {
            mvc.perform(delete("/user/api/v1/delete")
                            .param("id", "10"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("– ID inválido (<=0 ou null) -> 400")
        void deleteBadRequest() throws Exception {
            mvc.perform(delete("/user/api/v1/delete")
                            .param("id", "0"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("– Service lança EntityNotFoundException -> 404")
        void deleteNotFound() throws Exception {
            willThrow(new jakarta.persistence.EntityNotFoundException())
                    .given(userService).deleteUser(999L);

            mvc.perform(delete("/user/api/v1/delete")
                            .param("id", "999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /user/api/v1/findAll")
    class FindAllTests {
        @Test
        @DisplayName("– Sucesso: retorna 200 com página")
        void findAllSuccess() throws Exception {
            PageModel<UserModelResponse> page = new PageModel<>(
                    0, 10, 1L, 1, List.of(
                    UserModelResponse.builder()
                            .id(1L).username("x").email("x@ex.com").enabled(true).build()
            )
            );
            given(userService.findAllUsers(isNull(), isNull(), isNull(), eq(0), eq(10)))
                    .willReturn(page);

            mvc.perform(get("/user/api/v1/findAll"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.totalContent").value(1));
        }

        @Test
        @DisplayName("– Service lança EntityNotFoundException -> 204")
        void findAllNoContent() throws Exception {
            given(userService.findAllUsers(any(), any(), any(), anyInt(), anyInt()))
                    .willThrow(new jakarta.persistence.EntityNotFoundException());

            mvc.perform(get("/user/api/v1/findAll"))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("GET /user/api/v1/findById")
    class FindByIdTests {
        @Test
        @DisplayName("– Sucesso: retorna 200 com usuário")
        void findByIdSuccess() throws Exception {
            UserModelResponse resp = UserModelResponse.builder()
                    .id(5L).username("ana").email("ana@ex.com").enabled(true).build();
            given(userService.findById(5L)).willReturn(resp);

            mvc.perform(get("/user/api/v1/findById")
                            .param("id", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("ana"));
        }

        @Test
        @DisplayName("– ID inválido (<=0) -> 400")
        void findByIdBadRequest() throws Exception {
            mvc.perform(get("/user/api/v1/findById")
                            .param("id", "-1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("– Service retorna null -> 404")
        void findByIdNotFound() throws Exception {
            given(userService.findById(123L)).willReturn(null);

            mvc.perform(get("/user/api/v1/findById")
                            .param("id", "123"))
                    .andExpect(status().isNotFound());
        }
    }
}
