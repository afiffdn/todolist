package com.example.entities

data class NoteResponse<T> (
    val data: T,
    val success: Boolean
)