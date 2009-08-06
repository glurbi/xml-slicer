object Test {
  
  def main(args : Array[String]) : Unit = {
    slice
    logStatistics
  }

  private val count = new java.util.concurrent.atomic.AtomicLong
  private val stuff =
    "<c with='attribute'><b>content</b><b>content<d>thing</d></b></c>".getBytes
    
  private def slice {
    new Thread(new Runnable {
      def run {
        val byteCounter = new XmlMessageProcessor {
          def process(bytes: Array[Byte]) {
            count.addAndGet(bytes.length)
          }
        }
        val slicer = new XmlStreamSlicerScala(byteCounter, 65536)
        slicer.process("<startTag>".getBytes)
        while (true) {
          slicer.process(stuff)
        }
      }
    }).start
  }
    
  private def logStatistics {
    while (true) {
      Thread.sleep(1000)
      System.out.println("" + 1.0 * count.getAndSet(0) / 1024)
    }
  }
    
}
