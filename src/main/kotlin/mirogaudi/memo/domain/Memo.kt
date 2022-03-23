package mirogaudi.memo.domain

import org.hibernate.Hibernate
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Simple Memo (note) entity.
 */
@Entity
data class Memo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    @NotNull @Size(min = 3, max = 255)
    var text: String? = null,

    @CreatedDate
    @Column(nullable = false)
    var createdDate: LocalDateTime? = null,

    @Column(nullable = true)
    var dueDate: LocalDateTime? = null

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Memo

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , text = $text , createdDate = $createdDate , dueDate = $dueDate )"
    }
}
