package mirogaudi.memo.domain

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

@Entity
@Table(name = "memo")
data class Memo(

    // to be generated by DB
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "text", nullable = false)
    @field:NotEmpty
    @field:Size(min = 3, max = 1024)
    var text: String,

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    var priority: Priority? = null,

    @Column(name = "created_date", nullable = false)
    var createdDate: LocalDateTime? = null,

    @Column(name = "due_date")
    var dueDate: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "memo_label",
        joinColumns = [JoinColumn(name = "memo_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "label_id", referencedColumnName = "id")]
    )
    @JsonIgnore
    var labels: MutableSet<Label> = mutableSetOf()

) {
    @JsonGetter
    fun labelNames(): List<String> {
        return labels.map { it.name }
            .sorted()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Memo

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName +
            "(id = $id , text = $text , priority = $priority , createdDate = $createdDate , dueDate = $dueDate )"
    }
}

enum class Priority {
    LONG_TERM,
    MID_TERM,
    SHORT_TERM
}
