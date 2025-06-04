package com.fredrickosuala.ncheta.services

sealed class GenerationResult {
    data class Success(val content: String) : GenerationResult()
    data class Error(val message: String) : GenerationResult()
}