package dev.thhs.contentiospring.controllers

import dev.thhs.contentiospring.models.SentenceMediaStatus
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.services.MediaStatusService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * @property getSentence used in contentio-slides
 */

@RestController
@RequestMapping("sentences")
class SentencesController(val sentenceRepository: SentenceRepository, val mediaStatusService: MediaStatusService) {

    @GetMapping("/{id}")
    fun getSentence(@PathVariable id: Long) = sentenceRepository.getOne(id)

    @GetMapping("/{id}/mediastatus")
    fun getSentenceMediaStatus(@PathVariable id: Long): ResponseEntity<SentenceMediaStatus> {
        val sentence = try {
            sentenceRepository.findById(id).orElseThrow()
        } catch (err: NoSuchElementException) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(mediaStatusService.mediaStatus(sentence))
    }


}