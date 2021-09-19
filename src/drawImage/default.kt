package com.ntihs_fk.drawImage

import com.ntihs_fk.functions.randomString
import java.awt.Color
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
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
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val font = Font.createFont(
        Font.PLAIN,
        // 該死的字體
        File("./sarasa-mono-sc-bolditalic.ttf")
    ).deriveFont(fontSize)
    // 消除字體鋸齒
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_DEFAULT)
    // 載入字體
    ge.registerFont(font)
    g2d.font = font
    // 背景顏色
    g2d.background = Color.black
    g2d.clearRect(0, 0, width, height)
    drawString(g2d, top, left, linesData)
    g2d.dispose()
    // 寫入檔案
    ImageIO.write(image, "png", File("./${fileName}.png"))

    return fileName
}