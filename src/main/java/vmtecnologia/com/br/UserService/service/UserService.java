package vmtecnologia.com.br.UserService.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vmtecnologia.com.br.UserService.entity.UserEntity;
import vmtecnologia.com.br.UserService.exception.*;
import vmtecnologia.com.br.UserService.mapper.UserMapper;
import vmtecnologia.com.br.UserService.model.PageModel;
import vmtecnologia.com.br.UserService.model.request.UserModelRequest;
import vmtecnologia.com.br.UserService.model.response.UserModelResponse;
import vmtecnologia.com.br.UserService.repository.UserRepository;
import vmtecnologia.com.br.UserService.repository.specification.UserSpecification;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Serviço de negócio para gerenciamento de usuários, encapsulando operações
 * de criação, atualização, consulta, listagem com filtros e paginação,
 * e remoção de usuários.
 *
 * <p>Utiliza transações para garantir consistência em operações
 * de escrita e leitura.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final EmailService emailService;

    /**
     * Cria um novo usuário a partir dos dados fornecidos.
     *
     * <p>Valida unicidade do e-mail, regras de negócio (senha, username e e-mail),
     * codifica a senha, persiste a entidade e envia e-mail de boas-vindas.</p>
     *
     * @param userModelRequest DTO contendo username, e-mail, senha e flag de habilitado
     * @return DTO {@link UserModelResponse} com dados do usuário recém-criado
     * @throws EmailAlreadyExistsException    se já existir usuário com o mesmo e-mail
     * @throws PasswordNotProvidedException   se a senha não for informada
     * @throws InvalidPasswordException       se a senha não cumprir o padrão mínimo
     * @throws UsernameNotProvidedException   se o username estiver nulo ou em branco
     * @throws InvalidEmailException          se o e-mail não atender ao formato válido
     */
    @Transactional
    public UserModelResponse createUser(UserModelRequest userModelRequest) {
        // checa se já existe
        userRepository.findByEmail(userModelRequest.getEmail())
                .ifPresent(u -> { throw new EmailAlreadyExistsException(userModelRequest.getEmail()); });

        validateUser(userModelRequest);
        String encode = encoder.encode(userModelRequest.getPassword());
        userModelRequest.setPassword(encode);

        UserEntity userEntity = userMapper.toEntity(userModelRequest);
        UserEntity savedUser = userRepository.save(userEntity);

        emailService.sendUserCreationEmail(savedUser.getEmail(), savedUser.getUsername());
        return userMapper.toResponse(savedUser);
    }

    /**
     * Atualiza um usuário existente com base nos dados fornecidos.
     *
     * <p>Valida regras de negócio, verifica existência por e-mail, atualiza a entidade,
     * persiste as alterações e envia e-mail de notificação.</p>
     *
     * @param userModelRequest DTO contendo username, e-mail, senha e flag de habilitado
     * @return DTO {@link UserModelResponse} com dados do usuário atualizado
     * @throws UserNotFoundException          se não houver usuário cadastrado com o e-mail fornecido
     * @throws PasswordNotProvidedException   se a senha não for informada
     * @throws InvalidPasswordException       se a senha não cumprir o padrão mínimo
     * @throws UsernameNotProvidedException   se o username estiver nulo ou em branco
     * @throws InvalidEmailException          se o e-mail não atender ao formato válido
     */
    @Transactional
    public UserModelResponse updateUser(UserModelRequest userModelRequest) {
        validateUser(userModelRequest);

        Optional<UserEntity> userExist = userRepository.findByEmail(userModelRequest.getEmail());

        if (userExist.isEmpty()) {
            throw new UserNotFoundException(userModelRequest.getEmail());
        }

        UserEntity userEntity = userMapper.toEntity(userModelRequest);
        userEntity.setId(userExist.get().getId());
        userEntity.setPassword(encoder.encode(userModelRequest.getPassword()));

        UserEntity updatedUser = userRepository.save(userEntity);
        emailService.sendUserUpdateEmail(updatedUser.getEmail(), updatedUser.getUsername());
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Busca um usuário pelo seu identificador.
     *
     * @param id identificador único do usuário
     * @return DTO {@link UserModelResponse} com dados do usuário encontrado
     * @throws UserNotFoundException se não existir usuário com o ID informado
     */
    @Transactional(readOnly = true)
    public UserModelResponse findById(Long id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if (userEntity.isEmpty()) {
            log.error("findById() -> Usuario com ID {} não encontrado.", id);
            throw new UserNotFoundException(id.toString());
        }
        log.info("findById() -> Usuario com email {} encontrado.", userEntity.get().getEmail());
        return userMapper.toResponse(userEntity.get());
    }

    /**
     * Busca uma entidade de usuário por username e flag de habilitado.
     *
     * @param username nome de usuário cadastrado
     * @param enabled  indica se retorna apenas habilitados (true) ou desabilitados (false)
     * @return {@link Optional} contendo {@link UserEntity} encontrado, ou vazio se não existir
     */
    @Transactional(readOnly = true)
    public Optional<UserEntity> findByUsernameAndEnabled(String username, Boolean enabled) {
        return userRepository.findByUsernameAndEnabled(username, enabled);
    }

    /**
     * Busca uma entidade de usuário por e-mail e flag de habilitado.
     *
     * @param email   e-mail cadastrado do usuário
     * @param enabled indica se retorna apenas habilitados (true) ou desabilitados (false)
     * @return {@link Optional} contendo {@link UserEntity} encontrado, ou vazio se não existir
     */
    @Transactional(readOnly = true)
    public Optional<UserEntity> findByEmailAndEnabled(String email, Boolean enabled) {
        return userRepository.findByEmailAndEnabled(email, enabled);
    }

    /**
     * Lista usuários de forma paginada e com filtros opcionais.
     *
     * <p>Suporta filtragem por username (partial match), e-mail (partial match)
     * e flag de habilitado, retornando um modelo de página com metadados.</p>
     *
     * @param username   termo parcial para busca no campo username (ignorado se nulo/vazio)
     * @param email      termo parcial para busca no campo e-mail (ignorado se nulo/vazio)
     * @param enabled    flag para filtrar habilitados/desabilitados (ignorado se nulo)
     * @param pageNumber número da página (0-based)
     * @param pageSize   quantidade de itens por página
     * @return {@link PageModel} contendo lista de {@link UserModelResponse} e metadata de paginação
     */
    @Transactional(readOnly = true)
    public PageModel<UserModelResponse> findAllUsers(String username, String email, Boolean enabled, Integer pageNumber, Integer pageSize) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Specification<UserEntity> specification = UserSpecification.withFilters(username, email, enabled);

        Page<UserEntity> entities = userRepository.findAll(specification, pageRequest);

        if (entities.isEmpty()) {
            log.info("findAllUsers() -> Nenhum usuário encontrado com os filtros fornecidos.");
        }

        log.info("findAllUsers() -> Encontrados {} usuários com os filtros fornecidos.", entities.getTotalElements());

        List<UserModelResponse> userModelResponses = entities.stream()
                .map(userMapper::toResponse)
                .toList();

        return PageModel.<UserModelResponse>builder()
                .page(entities.getNumber())
                .pageSize(entities.getSize())
                .totalPages(entities.getTotalPages())
                .totalContent(entities.getTotalElements())
                .items(userModelResponses)
                .build();
    }

    /**
     * Remove um usuário existente.
     *
     * @param userId identificador do usuário a ser deletado
     * @throws EntityNotFoundException se não existir usuário com o ID informado
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("deleteUser() -> Usuario com ID {} não encontrado.", userId);
            throw new EntityNotFoundException("Usuário não encontrado");
        }
        userRepository.deleteById(userId);
        log.info("deleteUser() -> Usuario com ID {} deletado com sucesso.", userId);
    }

    /**
     * Valida regras básicas de negócio para criação e atualização de usuário:
     * senha (não nula, mínimo 8 caracteres, dígito, maiúscula e caractere especial),
     * username não vazio, e e-mail com formato válido.
     *
     * @param user modelo de requisição contendo dados de usuário
     * @throws PasswordNotProvidedException se a senha for nula
     * @throws InvalidPasswordException     se a senha não cumprir o padrão exigido
     * @throws UsernameNotProvidedException se o username estiver nulo ou em branco
     * @throws InvalidEmailException        se o e-mail não atender ao regex definido
     */
    private void validateUser(UserModelRequest user) {
        String password = user.getPassword();
        if (password == null) {
            throw new PasswordNotProvidedException();
        }

        // Regex: mínimo 8, ao menos 1 dígito, 1 maiúscula, 1 especial, sem espaços
        String pwdPattern = "^(?=.{8,}$)(?=.*\\d)(?=.*[A-Z])(?=.*[^\\w\\s]).+$";
        if (!password.matches(pwdPattern)) {
            throw new InvalidPasswordException();
        }

        if (Objects.isNull(user.getUsername()) || user.getUsername().isBlank()) {
            throw new UsernameNotProvidedException();
        }

        String email = user.getEmail();
        if (email == null || !email.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$")) {
            throw new InvalidEmailException(email);
        }
    }



}
