package mirogaudi.memo.domain

import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Simple memo entity.
 */
@Entity
@Table(name = "memo")
class Memo : BaseEntity() {

    @Column(name = "text", nullable = false)
    @NotNull
    @Size(min = 3, max = 1024)
    var text: String? = null

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "memo_label",
        joinColumns = [JoinColumn(name = "memo_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "label_id", referencedColumnName = "id")]
    )
    var labels: MutableSet<Label> = mutableSetOf()

    @CreatedDate
    @Column(nullable = false)
    var createdDate: LocalDateTime? = null

    @Column(nullable = true)
    var dueDate: LocalDateTime? = null
}
