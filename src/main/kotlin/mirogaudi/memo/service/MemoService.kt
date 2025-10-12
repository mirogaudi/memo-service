package mirogaudi.memo.service

import mirogaudi.memo.config.MemoServiceProperties
import mirogaudi.memo.domain.Memo
import mirogaudi.memo.domain.Priority
import mirogaudi.memo.repository.MemoRepository
import org.hibernate.Hibernate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface MemoService {
    fun getAll(): List<Memo>

    fun getById(id: Long): Memo

    fun deleteById(id: Long)

    fun create(
        text: String,
        priority: Priority?,
        dueDate: LocalDateTime?,
        labelIds: Set<Long>
    ): Memo

    fun update(
        id: Long,
        text: String,
        priority: Priority?,
        dueDate: LocalDateTime?,
        labelIds: Set<Long>
    ): Memo
}

@Service
@Transactional
class MemoServiceImpl(
    val memoServiceProperties: MemoServiceProperties,
    val memoRepository: MemoRepository,
    val labelService: LabelService
) : MemoService {

    override fun getAll(): List<Memo> {
        val memos = memoRepository.findAll()
        // initialize lazy relations if spring.jpa.open-in-view=false
        memos.forEach { Hibernate.initialize(it.labels) }
        return memos
    }

    override fun getById(id: Long): Memo {
        val memoOptional = memoRepository.findById(id)
        // initialize lazy relations if spring.jpa.open-in-view=false
        memoOptional.ifPresent { Hibernate.initialize(it.labels) }
        return memoOptional.orElseThrow { memoNotFoundException(id) }
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
        priority: Priority?,
        dueDate: LocalDateTime?,
        labelIds: Set<Long>
    ): Memo = memoRepository.save(
        Memo(
            text = text,
            priority = priority ?: memoServiceProperties.memoPriority ?: Priority.SHORT_TERM,
            createdDate = LocalDateTime.now(),
            dueDate = dueDate,
            labels = labelIds.map { labelService.getById(it) }.toMutableSet()
        )
    )

    override fun update(
        id: Long,
        text: String,
        priority: Priority?,
        dueDate: LocalDateTime?,
        labelIds: Set<Long>
    ): Memo = when {
        memoRepository.existsById(id) -> {
            memoRepository.save(
                Memo(
                    id = id,
                    text = text,
                    priority = priority,
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

    private fun memoNotFoundException(id: Long) = NotFoundException("Memo with id='$id' not found")

    // TODO add tests
}
