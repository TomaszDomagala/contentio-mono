package dev.thhs.contentiospring.models.askreddit

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
data class AskredditProject(
        val url: String,
        val postId: String,
        val projectPath: String,
        val minDuration: Int,
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0
)