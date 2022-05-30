package org.example.eventbus

enum class TYPES(val type: String) {
    PROCESS_FILE("IMPORTANT_MESSAGE"),
    CREATED("CREATED"),
}

data class Event(val type: String, val data: String)