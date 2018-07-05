package de.kevcodez.amazon.stock.api

import java.util.HashMap

class CartCreate(private val signedRequestHelper: SignedRequestHelper) {

    fun createCart(amazonIds: List<String>): String {
        val params = HashMap<String, String>()

        params["Service"] = "AWSECommerceService"
        params["Operation"] = "CartCreate"

        for (i in amazonIds.indices) {
            val amazonId = amazonIds[i]
            val number = i + 1

            params[String.format("Item.%d.ASIN", number)] = amazonId
            params[String.format("Item.%d.Quantity", number)] = MAX_QUANTITY.toString()
        }

        params["ResponseGroup"] = "Cart"

        return signedRequestHelper.readRequest(params)
    }

    companion object {

        const val MAX_QUANTITY = 999
    }

}
