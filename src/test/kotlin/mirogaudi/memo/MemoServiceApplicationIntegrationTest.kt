package mirogaudi.memo

import io.swagger.v3.oas.models.OpenAPI
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class MemoServiceApplicationIntegrationTest {

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun contextLoads() {
        Assertions.assertNotNull(context.getBean(OpenAPI::class.java))
    }
}
