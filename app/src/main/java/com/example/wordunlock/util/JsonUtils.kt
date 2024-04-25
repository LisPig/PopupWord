package com.example.wordunlock.util

import android.content.Context
import com.example.wordunlock.models.WordList
import com.google.gson.Gson
import java.io.InputStreamReader
class JsonUtils {
    companion object {
        fun loadWordListFromRawResource(context: Context, resourceId: Int): WordList {
            val inputStream = context.resources.openRawResource(resourceId)
            val reader = InputStreamReader(inputStream, Charsets.UTF_8)
            val gson = Gson()
            return gson.fromJson(reader, WordList::class.java)
        }
    }
}

