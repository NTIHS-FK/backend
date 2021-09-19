package com.ntihs_fk.drawImage

import java.awt.Font
import java.awt.Graphics2D

fun drawString(graphics: Graphics2D,top: Int, left: Int, lines: MutableList<MutableList<Char>>, font: Font, fontSize: Int) {
    graphics.font = font.deriveFont(fontSize.toFloat())
    for (i in (0 until lines.size)) {
        graphics.drawString(lines[i].joinToString(""), left, top + ((fontSize + 10) * i))
    }
}