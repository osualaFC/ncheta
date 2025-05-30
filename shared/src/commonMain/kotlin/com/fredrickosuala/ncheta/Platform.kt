package com.fredrickosuala.ncheta

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform