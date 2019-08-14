package dev.thhs.contentiospring.models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class Sentence(
        @ManyToOne
        @JoinColumn(name = "statement_id")
        @JsonIgnore
        val statement: Statement,

        val index: Int,

        @Lob
        @Column(length = 100000)
        val text: String = "",
        val paragraph: Int,

        var duration: Float = 0f,

        @JsonIgnore
        var audioPath: String = "",

        @JsonIgnore
        var slidePath: String = "",

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0

)