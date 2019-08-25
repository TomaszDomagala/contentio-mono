package dev.thhs.contentiospring.models.askreddit

import com.fasterxml.jackson.annotation.JsonIgnore
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
        var allCommentsUsed: Boolean = false,
        @JsonIgnore
        var videoPath: String = "",
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0
)