package de.kevcodez.amazon.stock.parser

import java.io.ByteArrayInputStream
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.Optional

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

import org.w3c.dom.Document
import org.w3c.dom.NodeList

class XmlParser {

    fun parseString(stringToParse: String): Document {
        val builder = factory.newDocumentBuilder()

        return builder.parse(ByteArrayInputStream(stringToParse.toByteArray(StandardCharsets.UTF_8)))
    }

    fun evaluateNumber(document: Any, xpathExpression: String): Optional<BigDecimal> {
        val xpath = xPathfactory.newXPath()
        val expr = xpath.compile(xpathExpression)
        val evalutedObject = expr.evaluate(document, XPathConstants.NUMBER)
        return if (evalutedObject == null || evalutedObject.toString().isEmpty() || evalutedObject.toString() == "NaN") {
            Optional.empty()
        } else Optional.of(BigDecimal(evalutedObject.toString()))

    }

    fun evaluatePrice(document: Any, xpathExpression: String): Optional<BigDecimal> {
        val number = evaluateNumber(document, xpathExpression)

        return if (number.isPresent) Optional.of(number.get().divide(ONE_HUNDRED)) else Optional.empty()
    }

    fun evaluateString(document: Any, xpathExpression: String): String? {
        val xpath = xPathfactory.newXPath()
        val expr = xpath.compile(xpathExpression)

        return expr.evaluate(document, XPathConstants.STRING) as String?
    }

    fun evaluateNodeList(document: Any, xpathExpression: String): NodeList {
        val xpath = xPathfactory.newXPath()
        val expr = xpath.compile(xpathExpression)

        return expr.evaluate(document, XPathConstants.NODESET) as NodeList
    }

    companion object {

        private val xPathfactory = XPathFactory.newInstance()
        private val factory = DocumentBuilderFactory.newInstance()

        private val ONE_HUNDRED = BigDecimal(100L)
    }

}
