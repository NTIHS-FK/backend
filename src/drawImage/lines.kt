package com.ntihs_fk.drawImage

fun lines(text: String, width: Int, left: Int, fontSize: Int): MutableList<MutableList<Char>> {
    val textLeft = width - left - 10
    val enChars = ('a'..'z') + ('0'..'9') + ('A'..'Z') + ' '
    val lines = mutableListOf<MutableList<Char>>()
    var thisLineWidth = 0
    var line = mutableListOf<Char>()

    for(i in (text.indices)) {
        val count = if (text[i] in enChars) fontSize / 2
        else fontSize
        if (thisLineWidth + count <= textLeft && text[i] != '\n') {
            thisLineWidth += count
            line.add(text[i])
            if (i == text.length - 1) lines.add(line)
        } else {
            thisLineWidth = if (text[i] != '\n') count
            else 0
            lines.add(line)
            line = mutableListOf(text[i])
        }
    }
    return lines

}