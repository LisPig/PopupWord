package com.solooxy.popupword.util

import android.content.Context
import android.content.res.Resources
import com.solooxy.popupword.models.WordDefinition
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStreamReader
class JsonUtils {
    companion object {
        fun loadWordListFromRawResource(context: Context, resourceId: Int): List<WordDefinition> {
            try {
                val inputStream = context.resources.openRawResource(resourceId)
                val reader = InputStreamReader(inputStream, Charsets.UTF_8)
                val typeToken = object : TypeToken<List<WordDefinition>>() {}.type
                val gson = Gson()
                return gson.fromJson(reader, typeToken)
            } catch (e: Resources.NotFoundException) {
                throw IllegalArgumentException("Resource not found with ID: $resourceId", e)
            } catch (e: JsonSyntaxException) {
                throw IllegalStateException("Failed to parse JSON from resource with ID: $resourceId", e)
            } catch (e: IOException) {
                throw IOException("Error reading resource with ID: $resourceId", e)
            }
        }

        /*fun loadWordListFromRawResource(context: Context, resourceId: Int): WordList {
            return context.resources.openRawResource(resourceId).use { inputStream ->
                InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                    val gson = Gson()
                    gson.fromJson(reader, WordList::class.java)
                }
            }
        }*/
    }
}

