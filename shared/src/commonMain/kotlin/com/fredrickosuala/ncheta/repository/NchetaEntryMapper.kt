package com.fredrickosuala.ncheta.repository

import com.fredrickosuala.ncheta.data.model.NchetaEntry as DomainNchetaEntry
import com.fredrickosuala.ncheta.database.NchetaEntry as DbNchetaEntry

fun DbNchetaEntry.toDomain(): DomainNchetaEntry {
    return DomainNchetaEntry(
        id = this.id,
        title = this.title,
        sourceText = this.sourceText,
        createdAt = this.createdAt,
        lastPracticedAt = this.lastPracticedAt,
        inputSourceType = this.inputSourceType,
        content = this.content
    )
}