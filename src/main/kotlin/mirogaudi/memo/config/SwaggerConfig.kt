package mirogaudi.memo.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Memo API")
                    .description("Demo of memo service")
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .email("mirogaudi@ya.ru")
                            .url("https://github.com/mirogaudi")
                    )
                    .license(
                        License()
                            .name("Apache-2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                    )
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Source code on GitHub")
                    .url("https://github.com/mirogaudi/memo-service")
            )
    }
}
