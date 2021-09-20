package com.ntihs_fk.drawImage

import com.ntihs_fk.functions.randomString
import java.awt.Color
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun defaultDraw(text: String): String {
    val width = 960
    val top = 100
    val left = 100
    val fontSize = 64f
    val linesData = lines(text, width, left, fontSize.toInt())
    val height = top + (fontSize.toInt() + 10) * linesData.size + 100
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g2d = image.createGraphics()
    val fileName = Date().time.toString() + randomString()
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val classloader = Thread.currentThread().contextClassLoader
    val fontFile = classloader.getResource("font/sarasa-mono-sc-bolditalic.ttf")
    val font = Font.createFont(
        Font.TRUETYPE_FONT,
        // 該死的字體
        File(fontFile!!.toURI())
    )
    // 消除字體鋸齒
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT)
    // 載入字體
    ge.registerFont(font)
    // 背景顏色
    g2d.background = Color.black
    g2d.clearRect(0, 0, width, height)
    // 繪製內容
    drawString(g2d, top, left, linesData, font, fontSize.toInt())
    drawNowTime(g2d, height, width, font)
    // 寫入檔案
    g2d.dispose()
    ImageIO.write(image, "jpeg", File("./textImage/${fileName}.jpg"))

    return fileName
}