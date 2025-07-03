package vmtecnologia.com.br.UserService.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade que representa um usuário no banco de dados.
 *
 * Mapeada para a tabela <code>users</code>, com restrição de exclusividade
 * no campo <code>email</code> e <code>username</code>.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class UserEntity {

    /**
     * Identificador único gerado automaticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Nome de usuário único, usado para login e exibição.
     */
    @Basic
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * Endereço de e-mail único do usuário, usado como login.
     */
    @Basic
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Senha criptografada do usuário.
     */
    @Basic
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Indica se o usuário está habilitado para acessar o sistema.
     */
    @Basic
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

}
