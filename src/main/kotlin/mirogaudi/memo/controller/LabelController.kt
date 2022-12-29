package mirogaudi.memo.controller

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import mirogaudi.memo.domain.Label
import mirogaudi.memo.service.LabelService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/labels")
@Tag(name = "Labels")
@Validated
class LabelController(val labelService: LabelService) {

    @GetMapping
    fun getAll(): List<Label> {
        return labelService.getAll()
    }

    @GetMapping(value = ["/{id}"])
    fun getById(
        @Parameter(description = "Label ID")
        @PathVariable
        id: Long
    ): Label {
        return labelService.getById(id)
    }

    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "Label ID")
        @PathVariable
        id: Long
    ) {
        labelService.deleteById(id)
    }

    @PostMapping
    fun create(
        @Parameter(description = "Label")
        @RequestBody
        @Valid
        label: Label
    ): Label {
        return labelService.create(label)
    }

    @PutMapping(value = ["/{id}"])
    fun update(
        @Parameter(description = "Label ID")
        @PathVariable
        id: Long,

        @Parameter(description = "Label")
        @RequestBody
        @Valid
        label: Label
    ): Label {
        return labelService.update(id, label)
    }

    // TODO add tests
}
