package mirogaudi.memo.domain

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "memo")
data class Memo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "text", nullable = false)
    @NotNull
    @Size(min = 3, max = 1024)
    var text: String? = null,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "memo_label",
        joinColumns = [JoinColumn(name = "memo_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "label_id", referencedColumnName = "id")]
    )
    @JsonIgnore
    var labels: MutableSet<Label> = mutableSetOf(),

    @CreatedDate
    @Column(nullable = false)
    var createdDate: LocalDateTime? = null,

    @Column(nullable = true)
    var dueDate: LocalDateTime? = null

) {
    @JsonGetter
    fun labelNames(): List<String> {
        return labels.map { it.name.toString() }.sorted()
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
        return this::class.simpleName + "(id = $id , text = $text , createdDate = $createdDate , dueDate = $dueDate )"
    }
}