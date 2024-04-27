package com.example.wordunlock.models

data class WordList(val name: String, val words: List<WordDefinition>, var isSelected: Boolean = false)
