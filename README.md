# Amazon Stock

This library allows retrieving the current stock from an amazon offer.

## Example

```kotlin
val stockservice = StockService(
    AmazonConfig(
        endpoint = "webservices.amazon.de",
        accessKeyId = "xxx",
        secretKey = "yyy",
        associateTag = "<your-associate-tag>"
    )
)

val stock = stockservice.getStockInformation(listOf("B01BPJK4S2"))
println(stock)
```

> [StockInformation(price=12.9, stock=9, asin=B01BPJK4S2)]

## How does it work?

To retrieve the stock, an API call to the [Product Advertising API](https://docs.aws.amazon.com/AWSECommerceService/latest/DG/Welcome.html) is done. 
By entering an usually high amount of 999 as requested stock, Amazon will return the available stock.

## Getting API access

Go to [Sign up for the Product Advertising API](https://docs.aws.amazon.com/AWSECommerceService/latest/DG/becomingDev.html) and get your access token and secret key.