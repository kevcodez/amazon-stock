package de.kevcodez.amazon.stock.config

data class AmazonConfig(
    val endpoint: String,
    val accessKeyId: String,
    val secretKey: String,
    val associateTag: String
)
