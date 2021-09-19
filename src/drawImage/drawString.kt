package com.ntihs_fk.drawImage

import java.awt.Graphics2D

fun drawString(graphics: Graphics2D,top: Int, left: Int, lines: MutableList<MutableList<Char>>) {
    for (i in (0 until lines.size)) {
        graphics.drawString(lines[i].joinToString(""), left, top + 30 * i)
    }
}