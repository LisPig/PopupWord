package com.example.wordunlock.models

data class WordList(val name: String, val words: List<Word>,var isSelected: Boolean = false)
