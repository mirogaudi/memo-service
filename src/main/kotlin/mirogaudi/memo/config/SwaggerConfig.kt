package mirogaudi.memo.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Memo API")
                .description("Demo of memo service")
                .version("v1.0.0")
        )
        .externalDocs(
            ExternalDocumentation()
                .description("Source code on GitHub")
                .url("https://github.com/mirogaudi/memo-service")
        )
}
