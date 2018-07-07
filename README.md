# Amazon Stock

This library allows retrieving the current stock from an amazon offer.

![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/de/kevcodez/amazon/stock/maven-metadata.xml.svg) [![Build Status](https://travis-ci.org/kevcodez/amazon-stock.svg?branch=master)](https://travis-ci.org/kevcodez/amazon-stock) [![GitHub license](https://img.shields.io/github/license/kevcodez/amazon-stock.svg)](https://github.com/kevcodez/amazon-stock/blob/master/LICENSE)

## Getting started

## Maven
```xml
<dependency>
    <groupId>de.kevcodez.amazon</groupId>
    <artifactId>stock</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Gradle

```groovy
compile "de.kevcodez.amazon:stock:0.1.0"
```

### Example

```kotlin
val stockService = StockService(
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
By entering an usually high amount of 999 as requested stock during a CartCreate-Call, the available stock is returned.

## Getting API access

Go to [Sign up for the Product Advertising API](https://docs.aws.amazon.com/AWSECommerceService/latest/DG/becomingDev.html) and get your access token/secret key.