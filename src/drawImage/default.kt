package com.ntihs_fk.drawImage

import com.ntihs_fk.functions.randomString
import java.awt.Color
import java.awt.image.BufferedImage;
import java.io.File
import javax.imageio.ImageIO

fun defaultDraw() {
    val width = 364
    val height = 500
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g2d = image.createGraphics()
    g2d.background = Color.black
    g2d.clearRect(0, 0, width, height)
    g2d.drawString("鬼ㄅ", 10, 10)
    g2d.dispose()
    ImageIO.write(image, "png", File("./${randomString()}"))
}