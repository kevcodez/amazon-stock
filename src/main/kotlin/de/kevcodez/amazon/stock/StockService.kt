package de.kevcodez.amazon.stock

import de.kevcodez.amazon.stock.exception.AmazonStockException
import de.kevcodez.amazon.stock.config.AmazonConfig
import java.util.ArrayList

import de.kevcodez.amazon.stock.api.CartCreate
import de.kevcodez.amazon.stock.api.SignedRequestHelper
import de.kevcodez.amazon.stock.parser.XmlParser

class StockService(amazonConfig: AmazonConfig) {

    var cartCreate: CartCreate

    init {
        val signedRequestHelper = SignedRequestHelper(amazonConfig)
        cartCreate = CartCreate(signedRequestHelper)
    }

    private val xmlParser = XmlParser()

    fun getStock(amazonIds: List<String>): List<Stock> {
        val cartResponse = cartCreate.createCart(amazonIds)

        val document = xmlParser.parseString(cartResponse)
        val cartItems = xmlParser.evaluateNodeList(document, "//CartItem")

        val errorCode = xmlParser.evaluateString(document, "//Error/Code")
        if (errorCode != null && "AWS.ECommerceService.InvalidQuantity" != errorCode) {
            throw AmazonStockException("Error occured while getting stock information: $errorCode")
        }

        val stockInformation = ArrayList<Stock>()
        for (i in 0 until cartItems.length) {
            val baseXpath = String.format("//CartItem[%d]/", i + 1)
            val price = xmlParser.evaluatePrice(document, baseXpath + "Price/Amount")
            val amazonId = xmlParser.evaluateString(document, baseXpath + "ASIN")
            val stock = xmlParser.evaluateNumber(document, baseXpath + "Quantity").get().toInt()

            stockInformation.add(Stock(asin = amazonId!!, price = price.get(), stock = stock))
        }

        return stockInformation
    }

    fun getStock(amazonId: String): Stock {
        return getStock(listOf(amazonId))[0]
    }
}
