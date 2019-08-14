package dev.thhs.contentiospring.controllers

import dev.thhs.contentiospring.repositories.SentenceRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * @property getSentence used in contentio-slides
 */

@RestController
@RequestMapping("sentences")
class SentencesController(val sentenceRepository: SentenceRepository) {

    @GetMapping("/{id}")
    fun getSentence(@PathVariable id: Long) = sentenceRepository.getOne(id)
}