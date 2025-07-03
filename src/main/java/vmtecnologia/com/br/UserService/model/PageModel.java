package vmtecnologia.com.br.UserService.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo para paginação de resultados")
public class PageModel<I> {

    @Schema(description = "Número da página atual", example = "0", required = true)
    private Integer page;

    @Schema(description = "Quantidade de itens por página", example = "10", required = true)
    private Integer pageSize;

    @Schema(description = "Total de itens encontrados", example = "100", required = true)
    private Long totalContent;

    @Schema(description = "Total de páginas disponíveis", example = "10", required = true)
    private Integer totalPages;

    @Schema(description = "Lista de itens da página atual")
    private List<I> items;

}
