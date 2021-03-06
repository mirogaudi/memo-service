package mirogaudi.memo.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

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
