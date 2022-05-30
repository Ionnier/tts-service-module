package org.example.merger

import org.example.eventbus.Event
import org.example.eventbus.EventBus
import org.example.eventbus.TYPES
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream
import java.lang.Exception
import java.util.*

const val WRITE_PATH = "/app/audioFiles/"
data class AudioFile(val letter: String, val path: String) {
    companion object {
        fun extractLetter(aux: String): String {
            return aux.split(".")[0]
        }
    }
    val data: ByteArray

    init {
        FileInputStream(path).use {
            data = it.readAllBytes()
        }
    }
}

data class ProcessedAudio(val content: String, val id: String = UUID.randomUUID().toString())

object AudioManager {
    private val PATH by lazy {
        "/app/voices"
    }

    private val audioFiles: List<AudioFile>?
    private val processedFiles = hashMapOf<String, ProcessedAudio>()

    init {
        audioFiles = mutableListOf()

        for (x in getAllFiles(PATH)){
            audioFiles.add(AudioFile(AudioFile.extractLetter(x), File(PATH, x).path))
        }
    }

    private fun getAllFiles(path: String = PATH): List<String> {
        val aux = mutableListOf<String>()
        File(path).list().forEach { aux.add(it) }
        return aux
    }

    private fun calculateLetters(originalString: String): MutableList<String> {
        val letters = mutableListOf<String>()
        val string = originalString.lowercase()
        for (i in string.indices) {
            audioFiles?.let { it ->
                it.firstOrNull { elem -> elem.letter == string[i].toString() }?.let {
                    letters.add(it.letter)
                }
            }
        }
        return letters
    }

    private fun createAudioMerge(originalString: String): Unit {
        val letters = calculateLetters(originalString)
        val byteArrays = mutableListOf<ByteArray>()
        audioFiles?.let {
            for (x in letters) {
                it.firstOrNull { elem -> elem.letter == x }?.let { audioFile ->
                    byteArrays.add(audioFile.data.clone())
                }
            }
        }
        var processedAudio = ProcessedAudio(originalString)
        while (true) {
            val asd = processedFiles[processedAudio.id]
            if (asd != null) {
                processedAudio = ProcessedAudio(originalString)
                continue
            }
            break
        }
        FileOutputStream("$WRITE_PATH/${processedAudio.id}.mp3").use {
            for (x in byteArrays){
                it.write(x)
            }
            addFile(processedAudio)
        }
    }

    private fun addFile(processedAudio: ProcessedAudio){
        processedFiles[processedAudio.id] = processedAudio
        try{
            EventBus.sendEvent(Event(TYPES.CREATED.type, processedAudio.content))
        } catch (e: Exception) {
            println("Couldn't send event")
        }
    }

    fun getFile(content: String, justCreate: Boolean = false): ByteArray? {
        processedFiles.entries.stream().filter { it.value.content == content }.findFirst().let{
            if (justCreate) {
                if (it.isEmpty) {
                    createAudioMerge(content)
                }
                return null
            }
            var byteArray: ByteArray? = null
            it.ifPresent {
                FileInputStream("$WRITE_PATH/${it.value.id}.mp3").use { file ->
                    byteArray = file.readAllBytes()
                    return@ifPresent
                }
            }
            if (byteArray != null) {
                return byteArray as ByteArray
            }
            createAudioMerge(content)
            return getFile(content)
        }
    }
}