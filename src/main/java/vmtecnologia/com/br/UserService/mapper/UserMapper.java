package vmtecnologia.com.br.UserService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import vmtecnologia.com.br.UserService.entity.UserEntity;
import vmtecnologia.com.br.UserService.model.request.UserModelRequest;
import vmtecnologia.com.br.UserService.model.response.UserModelResponse;

/**
 * Mapper responsável por converter entre os modelos de requisição/resposta
 * de usuário e a entidade persistente {@link UserEntity}.
 *
 * Utiliza MapStruct para gerar implementações de mapeamento de forma
 * eficiente e declarativa.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    /**
     * Converte um DTO(Model) de requisição {@link UserModelRequest} para a entidade
     * {@link UserEntity}, preservando campos correspondentes.
     *
     * @param request objeto de requisição contendo dados de usuário (username, email, password, enabled)
     * @return instância de {@link UserEntity} pronta para persistência
     */
    UserEntity toEntity(UserModelRequest request);

    /**
     * Converte uma entidade {@link UserEntity} para um DTO(Model) de resposta
     * {@link UserModelResponse}, expondo apenas campos necessários.
     *
     * @param entity entidade persistida contendo todos os campos do usuário
     * @return objeto de resposta contendo dados essenciais (id, username, email, enabled)
     */
    UserModelResponse toResponse(UserEntity entity);

}
