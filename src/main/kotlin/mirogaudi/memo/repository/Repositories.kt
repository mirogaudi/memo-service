package mirogaudi.memo.repository

import mirogaudi.memo.domain.Label
import mirogaudi.memo.domain.Memo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LabelRepository : JpaRepository<Label, Long>

@Repository
interface MemoRepository : JpaRepository<Memo, Long>
