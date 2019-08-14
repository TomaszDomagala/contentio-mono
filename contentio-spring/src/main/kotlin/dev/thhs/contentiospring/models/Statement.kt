package dev.thhs.contentiospring.models

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.thhs.contentiospring.models.reddit.Submission
import javax.persistence.*

@Entity
@Table(name = "statement")
data class Statement(
        @OneToOne(mappedBy = "statement", cascade = [CascadeType.ALL])
        @JsonIgnore
        val submission: Submission,

        @Lob
        @Column(length = 100000)
        val text: String,

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "id")
        val id: Long = 0
) {
    override fun toString(): String {
        return "Statement(text='$text', id=$id)"
    }
}