package mirogaudi.memo.controller

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import mirogaudi.memo.domain.Memo
import mirogaudi.memo.domain.Priority
import mirogaudi.memo.service.MemoService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/memos")
@Tag(name = "Memos")
@Validated
class MemoController(val memoService: MemoService) {

    @GetMapping
    fun getAll(): List<Memo> = memoService.getAll()

    @GetMapping(value = ["/{id}"])
    fun getById(
        @Parameter(description = "Memo ID")
        @PathVariable
        id: Long
    ): Memo = memoService.getById(id)

    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "Memo ID")
        @PathVariable
        id: Long
    ) {
        memoService.deleteById(id)
    }

    @PostMapping
    fun create(
        @Parameter(description = "Memo text")
        @RequestParam
        text: String,
        @Parameter(description = "Memo priority")
        @RequestParam(required = false)
        priority: Priority?,
        @Parameter(description = "Memo due date")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        dueDate: LocalDateTime?,
        @Parameter(description = "Memo labels IDs")
        @RequestParam
        labelIds: Set<Long>?
    ): Memo = memoService.create(text, priority, dueDate, labelIds ?: mutableSetOf())

    @PutMapping(value = ["/{id}"])
    fun update(
        @Parameter(description = "Memo ID")
        @PathVariable
        id: Long,
        @Parameter(description = "Memo text")
        @RequestParam
        text: String,
        @Parameter(description = "Memo priority")
        @RequestParam(required = false)
        priority: Priority?,
        @Parameter(description = "Memo due date")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        dueDate: LocalDateTime?,
        @Parameter(description = "Memo labels IDs")
        @RequestParam
        labelIds: Set<Long>?
    ): Memo = memoService.update(id, text, priority, dueDate, labelIds ?: mutableSetOf())

    // TODO add tests
}
