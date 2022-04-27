package mirogaudi.memo.service

import mirogaudi.memo.domain.Memo
import mirogaudi.memo.repository.MemoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface MemoService {
    fun getAll(): List<Memo>
    fun getById(id: Long): Memo
    fun deleteById(id: Long)
    fun create(text: String, dueDate: LocalDateTime?, labelIds: Set<Long>): Memo
    fun update(id: Long, text: String, dueDate: LocalDateTime?, labelIds: Set<Long>): Memo
}

@Service
@Transactional
class MemoServiceImpl(
    val memoRepository: MemoRepository,
    val labelService: LabelService
) : MemoService {

    override fun getAll(): List<Memo> {
        return memoRepository.findAll()
            .sortedBy { it.id }
    }

    override fun getById(id: Long): Memo {
        return memoRepository.findById(id)
            .orElseThrow { memoNotFoundException(id) }
    }

    override fun deleteById(id: Long) {
        when {
            memoRepository.existsById(id) -> {
                memoRepository.deleteById(id)
            }
            else -> throw memoNotFoundException(id)
        }
    }

    override fun create(
        text: String,
        dueDate: LocalDateTime?,
        labelIds: Set<Long>
    ): Memo {
        return memoRepository.save(
            Memo(
                text = text,
                createdDate = LocalDateTime.now(),
                dueDate = dueDate,
                labels = labelIds.map { labelService.getById(it) }.toMutableSet()
            )
        )
    }

    override fun update(
        id: Long,
        text: String,
        dueDate: LocalDateTime?,
        labelIds: Set<Long>
    ): Memo {
        return when {
            memoRepository.existsById(id) -> {
                memoRepository.save(
                    Memo(
                        id = id,
                        text = text,
                        createdDate = getById(id).createdDate,
                        dueDate = dueDate,
                        labels = labelIds.map { labelService.getById(it) }.toMutableSet()
                    )
                )
            }
            else -> {
                throw memoNotFoundException(id)
            }
        }
    }

    private fun memoNotFoundException(id: Long) = NotFoundException("Memo with id='$id' not found")

    // TODO in create and update use label names, create not existing labels
    // TODO add fun searchByLabel(label: String): List<Memo>
    // TODO add tests
}
