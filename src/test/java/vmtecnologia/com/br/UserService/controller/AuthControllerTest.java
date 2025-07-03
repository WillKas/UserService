package vmtecnologia.com.br.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import vmtecnologia.com.br.UserService.exception.IncorrectPasswordException;
import vmtecnologia.com.br.UserService.exception.UserNotFoundException;
import vmtecnologia.com.br.UserService.filter.AuthenticationFilter;
import vmtecnologia.com.br.UserService.model.LoginRequestModel;
import vmtecnologia.com.br.UserService.model.TokenModel;
import vmtecnologia.com.br.UserService.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthenticationFilter.class
        ),
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AuthService authService;

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        @DisplayName("– Sucesso: retorna 200 com token JWT")
        void loginSuccess() throws Exception {
            LoginRequestModel req = new LoginRequestModel();
            req.setEmail("usuario@ex.com");
            req.setPassword("SenhaSegura1!");

            TokenModel token = TokenModel.builder()
                    .token("jwt-token-exemplo")
                    .build();

            given(authService.authenticate(any(LoginRequestModel.class)))
                    .willReturn(token);

            mvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token-exemplo"));
        }

        @Test
        @DisplayName("– Falha: credenciais inválidas -> 401")
        void loginBadCredentials() throws Exception {
            LoginRequestModel req = new LoginRequestModel();
            req.setEmail("invalido@ex.com");
            req.setPassword("SenhaErrada1!");

            given(authService.authenticate(any(LoginRequestModel.class)))
                    .willThrow(new UserNotFoundException("invalido@ex.com"));

            mvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("– Falha: senha incorreta -> 401")
        void loginIncorrectPassword() throws Exception {
            LoginRequestModel req = new LoginRequestModel();
            req.setEmail("usuario@ex.com");
            req.setPassword("SenhaErrada1!");

            given(authService.authenticate(any(LoginRequestModel.class)))
                    .willThrow(new IncorrectPasswordException());

            mvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("– Falha: payload mal formado -> 400")
        void loginBadRequest() throws Exception {
            // envia JSON sem campos obrigatórios
            mvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }
}
