package vmtecnologia.com.br.UserService.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import vmtecnologia.com.br.UserService.entity.UserEntity;
import vmtecnologia.com.br.UserService.exception.*;
import vmtecnologia.com.br.UserService.mapper.UserMapper;
import vmtecnologia.com.br.UserService.model.PageModel;
import vmtecnologia.com.br.UserService.model.request.UserModelRequest;
import vmtecnologia.com.br.UserService.model.response.UserModelResponse;
import vmtecnologia.com.br.UserService.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private UserService userService;

    private UserModelRequest validRequest;
    private UserEntity validEntity;
    private UserModelResponse validResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validRequest = UserModelRequest.builder()
                .username("usuario")
                .email("usuario@ex.com")
                .password("SenhaValida1!")
                .enabled(true)
                .build();
        validEntity = UserEntity.builder()
                .id(1L)
                .username("usuario")
                .email("usuario@ex.com")
                .password("hashed")
                .enabled(true)
                .build();
        validResponse = UserModelResponse.builder()
                .id(1L)
                .username("usuario")
                .email("usuario@ex.com")
                .enabled(true)
                .build();
    }

    @Nested
    @DisplayName("createUser")
    class CreateUser {
        @Test
        @DisplayName("deve lançar EmailAlreadyExistsException se email já existe")
        void emailExists() {
            given(userRepository.findByEmail(validRequest.getEmail()))
                    .willReturn(Optional.of(validEntity));

            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(EmailAlreadyExistsException.class)
                    .hasMessageContaining(validRequest.getEmail());
        }

        @Test
        @DisplayName("deve lançar InvalidPasswordException se senha inválida")
        void invalidPassword() {
            validRequest.setPassword("fraca");
            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(InvalidPasswordException.class);
        }

        @Test
        @DisplayName("deve lançar UsernameNotProvidedException se username em branco")
        void missingUsername() {
            validRequest.setUsername("");
            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(UsernameNotProvidedException.class);
        }

        @Test
        @DisplayName("deve lançar InvalidEmailException se email inválido")
        void invalidEmail() {
            validRequest.setEmail("invalid-email");
            assertThatThrownBy(() -> userService.createUser(validRequest))
                    .isInstanceOf(InvalidEmailException.class);
        }

        @Test
        @DisplayName("deve criar usuário e retornar resposta no cenário de sucesso")
        void success() {
            given(userRepository.findByEmail(validRequest.getEmail())).willReturn(Optional.empty());
            given(encoder.encode(validRequest.getPassword())).willReturn("hashed");
            given(userMapper.toEntity(validRequest)).willReturn(validEntity);
            given(userRepository.save(validEntity)).willReturn(validEntity);
            given(userMapper.toResponse(validEntity)).willReturn(validResponse);

            UserModelResponse result = userService.createUser(validRequest);

            then(userRepository).should().findByEmail(validRequest.getEmail());
            then(encoder).should().encode("SenhaValida1!");
            then(emailService).should().sendUserCreationEmail(validEntity.getEmail(), validEntity.getUsername());
            assertThat(result).isEqualToComparingFieldByField(validResponse);
        }
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {
        @Test
        @DisplayName("deve lançar UserNotFoudException se usuário não encontrado")
        void notFound() {
            given(userRepository.findByEmail(validRequest.getEmail())).willReturn(Optional.empty());
            assertThatThrownBy(() -> userService.updateUser(validRequest))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("deve atualizar usuário e retornar resposta no cenário de sucesso")
        void success() {
            given(userRepository.findByEmail(validRequest.getEmail())).willReturn(Optional.of(validEntity));
            given(encoder.encode(validRequest.getPassword())).willReturn("hashed2");
            UserEntity updatedEntity = UserEntity.builder()
                    .id(1L)
                    .username("usuario")
                    .email("usuario@ex.com")
                    .password("hashed2")
                    .enabled(true)
                    .build();
            given(userMapper.toEntity(validRequest)).willReturn(updatedEntity);
            given(userRepository.save(updatedEntity)).willReturn(updatedEntity);
            given(userMapper.toResponse(updatedEntity)).willReturn(validResponse);

            UserModelResponse result = userService.updateUser(validRequest);

            then(emailService).should().sendUserUpdateEmail(updatedEntity.getEmail(), updatedEntity.getUsername());
            assertThat(result).isEqualToComparingFieldByField(validResponse);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("deve lançar UserNotFoudException quando não encontrar")
        void notFound() {
            given(userRepository.findById(2L)).willReturn(Optional.empty());
            assertThatThrownBy(() -> userService.findById(2L))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("deve retornar UserModelResponse quando encontrar")
        void success() {
            given(userRepository.findById(1L)).willReturn(Optional.of(validEntity));
            given(userMapper.toResponse(validEntity)).willReturn(validResponse);

            UserModelResponse response = userService.findById(1L);

            assertThat(response).isEqualToComparingFieldByField(validResponse);
        }
    }

    @Nested
    @DisplayName("findAllUsers")
    class FindAllUsers {
        @Test
        @DisplayName("deve retornar PageModel com itens")
        void success() {
            Page<UserEntity> page = new PageImpl<>(List.of(validEntity), PageRequest.of(0, 10), 1);
            given(userRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(page);
            given(userMapper.toResponse(validEntity)).willReturn(validResponse);

            PageModel<UserModelResponse> result = userService.findAllUsers(null, null, null, 0, 10);

            assertThat(result.getItems()).containsExactly(validResponse);
            assertThat(result.getTotalContent()).isEqualTo(1);
            assertThat(result.getPage()).isEqualTo(0);
            assertThat(result.getPageSize()).isEqualTo(10);
            assertThat(result.getTotalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("deve retornar PageModel vazio quando não encontrar usuários")
        void empty() {
            Page<UserEntity> page = new PageImpl<>(List.of(), PageRequest.of(1, 5), 0);
            given(userRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(page);

            PageModel<UserModelResponse> result = userService.findAllUsers("x", "y@ex.com", true, 1, 5);

            assertThat(result.getItems()).isEmpty();
            assertThat(result.getTotalContent()).isZero();
            assertThat(result.getPage()).isEqualTo(1);
            assertThat(result.getPageSize()).isEqualTo(5);
            assertThat(result.getTotalPages()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {
        @Test
        @DisplayName("deve lançar EntityNotFoundException quando não existe")
        void notFound() {
            given(userRepository.existsById(99L)).willReturn(false);
            assertThatThrownBy(() -> userService.deleteUser(99L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("deve deletar usuário quando existir")
        void success() {
            given(userRepository.existsById(1L)).willReturn(true);
            willDoNothing().given(userRepository).deleteById(1L);

            userService.deleteUser(1L);

            then(userRepository).should().deleteById(1L);
        }
    }
}
