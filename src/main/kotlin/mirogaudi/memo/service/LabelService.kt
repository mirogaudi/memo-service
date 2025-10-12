package mirogaudi.memo.service

import mirogaudi.memo.domain.Label
import mirogaudi.memo.repository.LabelRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface LabelService {
    fun getAll(): List<Label>

    fun getById(id: Long): Label

    fun deleteById(id: Long)

    fun create(label: Label): Label

    fun update(
        id: Long,
        label: Label
    ): Label
}

@Service
@Transactional
class LabelServiceImpl(val labelRepository: LabelRepository) : LabelService {

    override fun getAll(): List<Label> = labelRepository.findAll()

    override fun getById(id: Long): Label = labelRepository.findById(id)
        .orElseThrow { labelNotFoundException(id) }

    override fun deleteById(id: Long) {
        when {
            labelRepository.existsById(id) -> {
                labelRepository.deleteById(id)
            }

            else -> throw labelNotFoundException(id)
        }
    }

    override fun create(label: Label): Label = labelRepository.save(
        Label(
            name = label.name
        )
    )

    override fun update(
        id: Long,
        label: Label
    ): Label = when {
        labelRepository.existsById(id) -> {
            labelRepository.save(
                Label(
                    id = id,
                    name = label.name
                )
            )
        }

        else -> {
            throw labelNotFoundException(id)
        }
    }

    private fun labelNotFoundException(id: Long) = NotFoundException("Label with id='$id' not found")

    // TODO add tests
}
