package com.ntihs_fk.drawImage

import java.awt.Font
import java.awt.Graphics2D
import java.text.SimpleDateFormat
import java.util.*


fun drawNowTime(graphics: Graphics2D, height: Int, width: Int, font: Font) {

    graphics.font = font.deriveFont(14f)

    val sdfDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //dd/MM/yyyy
    val now = Date()
    val strDate = sdfDate.format(now)
    graphics.drawString(strDate, width - 7 * 20, height - 30)
}