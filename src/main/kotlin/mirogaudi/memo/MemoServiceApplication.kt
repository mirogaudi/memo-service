package mirogaudi.memo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MemoServiceApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<MemoServiceApplication>(*args)
}
