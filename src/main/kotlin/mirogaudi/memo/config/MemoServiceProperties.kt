package mirogaudi.memo.config

import mirogaudi.memo.domain.Priority
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ms")
class MemoServiceProperties {
    var memoPriority: Priority? = null
}
