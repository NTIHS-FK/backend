package com.ntihs_fk.drawImage

import com.ntihs_fk.functions.randomString
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage;
import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun defaultDraw(text: String): String {
    val width = 364
    val top = 40
    val left = 20
    val fontSize = 20f
    val linesData = lines(text, width, left, fontSize.toInt())
    val height = top + 30 * linesData.size
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g2d = image.createGraphics()
    val fileName = Date().time.toString() + randomString()
    val font = Font.createFont(
        Font.TRUETYPE_FONT,
        File("./sarasa-mono-sc-light.ttf")
    ).deriveFont(Font.PLAIN).deriveFont(fontSize)

    g2d.font = font
    g2d.background = Color.black
    g2d.clearRect(0, 0, width, height)
    drawString(g2d, top, left, linesData)
    g2d.dispose()
    ImageIO.write(image, "png", File("./${fileName}.png"))

    return fileName
}