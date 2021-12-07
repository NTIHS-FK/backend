package com.ntihs_fk.drawImage

import java.awt.Font
import java.awt.Graphics2D
import java.text.SimpleDateFormat
import java.util.*

/**
 * 繪製發文時間
 */
fun drawNowTime(graphics: Graphics2D, height: Int, width: Int, font: Font, date: Date) {
    val sdfDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //dd/MM/yyyy
    val strDate = sdfDate.format(date)

    graphics.font = font.deriveFont(40f)
    graphics.drawString(strDate, width - 20 * 20, height - 20)
}