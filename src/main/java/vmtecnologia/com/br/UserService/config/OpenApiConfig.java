package vmtecnologia.com.br.UserService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "User Service API",
                version = "v1",
                description = "API de gerenciamento de usuários - CRUD e autenticação via JWT.\n\n"
                        + "Esta aplicação oferece:\n"
                        + "- Cadastro, atualização, consulta (ID/paginada) e exclusão de usuários.\n"
                        + "- Autenticação JWT para rotas protegidas.\n"
                        + "- Envio de e-mails de criação e atualização.\n\n"
                        + "Ferramentas e versões utilizadas:\n"
                        + "- Java 21\n"
                        + "- Spring Boot 3.3.13\n"
                        + "- Spring Data JPA, H2 runtime\n"
                        + "- Spring Security\n"
                        + "- springdoc-openapi-starter-webmvc-ui 2.6.0\n"
                        + "- JJWT 0.11.5\n"
                        + "- MapStruct 1.5.5.Final\n"
                        + "- Lombok 1.18.32\n"
                        + "- Maven 4.0.0 POM",
                contact = @Contact(
                        name = "Willian Betim",
                        email = "willian.mateus.betin@hotmail.com"
                )
        ),
        security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
)
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
