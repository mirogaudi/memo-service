package mirogaudi.memo

import io.swagger.v3.oas.models.OpenAPI
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
internal class MemoServiceApplicationIntegrationTest {

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun contextLoads() {
        assertNotNull(context.getBean(OpenAPI::class.java))
    }
}
