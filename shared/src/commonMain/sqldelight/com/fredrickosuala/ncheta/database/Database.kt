package com.fredrickosuala.ncheta.database

import app.cash.sqldelight.ColumnAdapter
import com.fredrickosuala.ncheta.shared.model.GeneratedContent
import com.fredrickosuala.ncheta.shared.model.InputSourceType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Database(databaseDriverFactory: DatabaseDriverFactory) {

    private val inputSourceTypeAdapter = object : ColumnAdapter<InputSourceType, String> {
        override fun decode(databaseValue: String): InputSourceType {
            return InputSourceType.valueOf(databaseValue)
        }
        override fun encode(value: InputSourceType): String {
            return value.name
        }
    }

    private val generatedContentAdapter = object : ColumnAdapter<GeneratedContent, String> {
        override fun decode(databaseValue: String): GeneratedContent {
            return Json.decodeFromString<GeneratedContent>(databaseValue)
        }
        override fun encode(value: GeneratedContent): String {
            return Json.encodeToString(value)
        }
    }


    private val database = NchetaDatabase(
        driver = databaseDriverFactory.createDriver(),
        NchetaEntryAdapter = NchetaEntry.Adapter(
            inputSourceTypeAdapter = inputSourceTypeAdapter,
            contentAdapter = generatedContentAdapter
        )
    )

    val queries = database.nchetaEntryQueries
}