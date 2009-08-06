private class XmlStreamSlicerScala(
  private val listener: XmlMessageProcessor,
  private val bufferCapacity: Int)
{
  private var message = new Array[Byte](bufferCapacity)
  private var index = 0
  private var depth = -1
  private var xmlStreamState: () => Unit = processSearchingTagState _
  private var previous: Byte = _
  private var current: Byte = _
  
  def process(bytes: Array[Byte]) {
    for (b <- bytes) {
      previous = current
      current = b
      xmlStreamState()
    }
  }
    
  private def append(b: Byte): Unit = {
    message(index) = b
    index += 1
  }

  private def processSearchingTagState: Unit = {
    if (current == '<') {
      if (depth == 0) {
        index = 0
      }
      depth += 1
      xmlStreamState = processParsingTagState _
    }
    append(current)
  }

  private def processParsingTagState: Unit = {
    append(current)
    if (current == '>') {
      xmlStreamState = processSearchingTagState _
    } else if (current == '/' && previous == '<') {
      xmlStreamState = processParsingEndTagState _
      depth -= 1
    } else if (current == '/' && previous != '<') {
      xmlStreamState = processParsingEndTagState _
    }
  }

  private def processParsingEndTagState: Unit = {
    append(current)
    if (current == '>') {
      if (depth == 1) {
        val xmlMessage = new Array[Byte](index)
        System.arraycopy(message, 0, xmlMessage, 0, index)
        listener.process(xmlMessage)
        index = 0
      }
      xmlStreamState = processSearchingTagState _
      depth -= 1
    }
  }
  
}
