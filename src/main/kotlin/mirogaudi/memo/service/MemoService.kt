package mirogaudi.memo.service

import mirogaudi.memo.domain.Memo
import mirogaudi.memo.repository.MemoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface MemoService {
    fun getAll(): List<Memo>
    fun getById(id: Long): Memo
    fun deleteById(id: Long)
    fun create(memo: Memo): Memo
    fun update(id: Long, memo: Memo): Memo
}

@Service
@Transactional
class MemoServiceImpl(val memoRepository: MemoRepository) : MemoService {

    override fun getAll(): List<Memo> {
        return memoRepository.findAll()
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

    override fun create(memo: Memo): Memo {
        return memoRepository.save(memo)
    }

    override fun update(id: Long, memo: Memo): Memo {
        return when {
            memoRepository.existsById(id) -> {
                memoRepository.save(
                    Memo(
                        id = memo.id,
                        text = memo.text,
                        labels = memo.labels,
                        dueDate = memo.dueDate
                    )
                )
            }
            else -> {
                throw memoNotFoundException(id)
            }
        }
    }

    private fun memoNotFoundException(id: Long) = NotFoundException("Memo with id='$id' not found")

    // TODO add fun searchByLabel
    // TODO add tests
}
