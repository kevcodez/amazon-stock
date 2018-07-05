package de.kevcodez.amazon.stock

import java.math.BigDecimal

data class Stock(
    var price: BigDecimal,
    var stock: Int,
    var asin: String
)