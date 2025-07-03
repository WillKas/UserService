package vmtecnologia.com.br.UserService.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vmtecnologia.com.br.UserService.entity.UserEntity;

import java.util.Optional;

/**
 * Repositório de acesso a dados para {@link UserEntity}, estendendo tanto
 * operações CRUD básicas (via {@link JpaRepository}) quanto a capacidade de
 * construir consultas dinâmicas (via {@link JpaSpecificationExecutor}).
 *
 * <p>Permite buscar usuários por e-mail, usuário e status de habilitação,
 * aproveitando o cache de transações de leitura.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    /**
     * Busca um usuário pelo seu e-mail.
     *
     * @param email o e-mail do usuário a ser consultado; não deve ser nulo
     * @return um {@link Optional} contendo {@link UserEntity} caso exista usuário
     *         com o e-mail fornecido ou vazio se não encontrado
     */
    @Transactional(readOnly = true)
    Optional<UserEntity> findByEmail(String email);

    /**
     * Busca um usuário pelo seu nome de usuário e pelo seu estado de habilitação.
     *
     * @param username o nome de usuário único cadastrado; não deve ser nulo
     * @param enabled  indica se deve retornar apenas usuários habilitados (true)
     *                 ou desabilitados (false)
     * @return um {@link Optional} contendo {@link UserEntity} caso exista um registro
     *         com estes dados, ou vazio se não encontrado
     */
    @Transactional(readOnly = true)
    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.enabled = :enabled")
    Optional<UserEntity> findByUsernameAndEnabled(String username, Boolean enabled);

    /**
     * Busca um usuário pelo seu e-mail e pelo seu estado de habilitação.
     *
     * @param email   o e-mail do usuário a ser consultado; não deve ser nulo
     * @param enabled indica se deve retornar apenas usuários habilitados (true)
     *                ou desabilitados (false)
     * @return um {@link Optional} contendo {@link UserEntity} caso exista um registro
     *         com estes dados, ou vazio se não encontrado
     */
    @Transactional(readOnly = true)
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.enabled = :enabled")
    Optional<UserEntity> findByEmailAndEnabled(String email, Boolean enabled);

}
