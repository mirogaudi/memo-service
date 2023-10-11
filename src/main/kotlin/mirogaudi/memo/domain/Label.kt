package mirogaudi.memo.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.hibernate.Hibernate
import java.util.Objects

@Suppress("ktlint:standard:no-blank-line-in-list")
@Entity
@Table(name = "label")
data class Label(

    // to be generated by DB
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "name", nullable = false, unique = true)
    @field:NotEmpty
    @field:Size(min = 3, max = 128)
    var name: String,

    // non-owning side of bidirectional relation
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "labels")
    @JsonIgnore
    var memos: MutableSet<Memo> = mutableSetOf()

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Label

        return id != null && id == other.id
    }

    // use name since it is unique
    override fun hashCode(): Int = Objects.hash(name)

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name )"
    }
}
