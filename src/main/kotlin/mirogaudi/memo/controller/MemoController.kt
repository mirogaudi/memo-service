package mirogaudi.memo.controller

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import mirogaudi.memo.domain.Memo
import mirogaudi.memo.service.MemoService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/memos")
@Tag(name = "Memos")
@Validated
class MemoController(val memoService: MemoService) {

    @GetMapping
    fun getAll(): List<Memo> {
        return memoService.getAll()
    }

    @GetMapping(value = ["/{id}"])
    fun getById(
        @Parameter(description = "Memo ID")
        @PathVariable id: Long
    ): Memo {
        return memoService.getById(id)
    }

    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "Memo ID")
        @PathVariable id: Long
    ) {
        memoService.deleteById(id)
    }

    // TODO add missing code
    // TODO add tests
}
