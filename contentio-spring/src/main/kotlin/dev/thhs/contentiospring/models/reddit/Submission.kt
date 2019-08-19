package dev.thhs.contentiospring.models.reddit

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.thhs.contentiospring.models.Statement
import dev.thhs.contentiospring.models.askreddit.AskredditProject
import java.io.File
import java.util.*
import javax.persistence.*


enum class SubmissionType {
    POST, COMMENT
}

@Entity
data class Submission(
        @Id @Column(name = "id", length = 10)
        val id: String = "",

        val author: String,
        val score: Int,
        val created: Date,

        @ManyToOne
        @JoinColumn
        @JsonIgnore
        val project: AskredditProject,

        @Column(name = "_type")
        val type: SubmissionType,

        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "statement_id", referencedColumnName = "id")
        @JsonIgnore
        var statement: Statement? = null
) {

    fun createCategorySubmissionDir(category: String): File {
        val projectDir = File(project.projectPath)
        val categoryDir = File(projectDir, category)
        val submissionDir = File(categoryDir, id)
        submissionDir.mkdirs()
        return submissionDir
    }

    override fun toString(): String {
        return "Submission(id='$id', author='$author', score=$score, created=$created, project=$project, type=$type)"
    }
}