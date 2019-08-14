package dev.thhs.contentiospring.repositories

import dev.thhs.contentiospring.models.askreddit.AskredditProject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AskredditProjectRepository : JpaRepository<AskredditProject, Long> {
    fun findByPostId(postId: String): Optional<AskredditProject>
}