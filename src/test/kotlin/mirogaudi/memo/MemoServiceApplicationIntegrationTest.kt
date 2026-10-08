package mirogaudi.memo

import io.swagger.v3.oas.models.OpenAPI
import mirogaudi.memo.config.MemoServiceProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
@ActiveProfiles("test")
internal class MemoServiceApplicationIntegrationTest {

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun contextLoads() {
        // app config

        val memoServiceProps = context.getBean(MemoServiceProperties::class.java)
        assertNotNull(memoServiceProps.memoPriority)

        // swagger
        assertNotNull(context.getBean(OpenAPI::class.java))
    }
}
