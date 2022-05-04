package mirogaudi.memo.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MemoServiceProperties::class)
class MemoServiceConfig
