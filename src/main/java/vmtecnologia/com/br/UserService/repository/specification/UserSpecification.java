package vmtecnologia.com.br.UserService.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vmtecnologia.com.br.UserService.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Constrói especificações JPA dinâmicas para filtragem de {@link UserEntity}
 * com base em parâmetros opcionais: nome de usuário, e-mail e status de habilitado.
 *
 * <p>Se nenhum parâmetro for fornecido (todos nulos ou vazios), retorna <code>null</code>,
 * o que equivale a não aplicar nenhum critério de filtro.</p>
 */
public interface UserSpecification {

    /**
     * Cria uma {@link Specification} que adiciona predicados conforme parâmetros não nulos:
     * <ul>
     *   <li>username — busca insensível a maiúsculas por correspondência parcial no campo <code>username</code>;</li>
     *   <li>email — busca insensível a maiúsculas por correspondência parcial no campo <code>email</code>;</li>
     *   <li>enabled — filtra pelo valor booleano exato do campo <code>enabled</code>.</li>
     * </ul>
     *
     * @param username termo de busca parcial para o nome de usuário (insensitive); se nulo ou vazio, não filtra por username
     * @param email    termo de busca parcial para o e-mail (insensitive); se nulo ou vazio, não filtra por email
     * @param enabled  flag para filtrar usuários habilitados/desabilitados; se nulo, não filtra por enabled
     * @return uma {@link Specification} combinando todos os predicados com <code>AND</code>, ou <code>null</code>
     *         se não houver predicados a aplicar (equivalente a "sem filtro")
     */
    static Specification<UserEntity> withFilters(String username, String email, Boolean enabled) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            try {

                // Adiciona o filtro para o nome de usuário
                if (username != null && !username.isEmpty()) {
                    String pattern = "%" + username.toLowerCase() + "%";
                    predicates.add(cb.like(cb.lower(root.get("username")), pattern));
                }

                // Adiciona o filtro para o email
                if (email != null && !email.isEmpty()) {
                    String pattern = "%" + email.toLowerCase() + "%";
                    predicates.add(cb.like(cb.lower(root.get("email")), pattern));
                }

                // Adiciona o filtro para o status "enabled"
                if (enabled != null) {
                    predicates.add(cb.equal(root.get("enabled"), enabled));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!predicates.isEmpty()) {
                return cb.and(predicates.toArray(new Predicate[0]));
            } else {
                // Se não houver predicados, retorne nulo (sem filtro)
                return null;
            }

        };
    }
}
