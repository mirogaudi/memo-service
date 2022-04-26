package mirogaudi.memo.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Memo label entity.
 */
@Entity
@Table(name = "label")
class Label : BaseEntity() {

    @Column(name = "name", nullable = false, unique = true)
    @NotNull
    @Size(min = 3, max = 128)
    var name: String? = null

    override fun toString(): String {
        return "Label(id=$id, name=$name)"
    }
}
