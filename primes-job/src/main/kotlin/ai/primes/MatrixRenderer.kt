package ai.primes

import org.apache.commons.lang3.StringUtils


const val PAD_SIZE_CELL = 8
const val PAD_SIZE_ROW_LABEL = PAD_SIZE_CELL

object MatrixRenderer {

  fun dump(matrix: Array<IntArray>, writer: StringBuilder = StringBuilder()): String {
    writer.append(StringUtils.center("Frequency", 10 * PAD_SIZE_CELL + PAD_SIZE_ROW_LABEL))
    dumpBorder(writer)

    dumpLabel("", writer)
    for (i in 0 until 10)
      dumpCell(i.toString(), writer)
    dumpBorder(writer)

    for(i in 0 until 10) {
      dumpLabel((i+1).toString(), writer)
      for (j in 0 until 10)
        dumpCell(matrix[i][j].toString(), writer)
      dumpBorder(writer)
    }

    dumpLabel("TOTAL", writer)
    for(j in 0 until 10) {
      var total = 0;
      for (i in 0 until 10)
        total += matrix[i][j]
      dumpCell(total.toString(), writer)
    }
    dumpBorder(writer)
    return writer.toString()
  }

  fun dumpCell(cell: String = "", writer: StringBuilder) {
    writer.append(StringUtils.leftPad("$cell |", PAD_SIZE_CELL, ' '))
  }

  fun dumpLabel(label: String = "", writer: StringBuilder) {
    writer.append(StringUtils.leftPad("$label |", PAD_SIZE_ROW_LABEL, ' '))
  }

  fun dumpBorder(writer: StringBuilder) {
    writer.append("\n")
    writer.append(IntRange(0, 10 * PAD_SIZE_CELL + PAD_SIZE_ROW_LABEL - 1).map { "-" }.joinToString("") { it })
    writer.append("\n")
  }

}
