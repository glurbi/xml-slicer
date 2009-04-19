import java.util.concurrent.atomic.AtomicLong;

public class XmlSlicerPerformanceTester {

    private static AtomicLong count = new AtomicLong();
    private static final byte[] stuff =
    	"<c with='attribute'><b>content</b><b>content<d>thing</d></b></c>".getBytes();
    
    public static void slice() {
    	new Thread(new Runnable() {
			@Override public void run() {
		        XmlMessageProcessor byteCounter = new XmlMessageProcessor() {
		        	public void process(byte[] bytes) {
		        		count.addAndGet(bytes.length);
		        	}
		        };
		    	XmlStreamSlicer slicer =
		    		(XmlStreamSlicer) new XmlStreamSlicerFast(byteCounter, 65536);
		    	slicer.process("<startTag>".getBytes());
		    	while (true) {
		    		slicer.process(stuff);
		    	}
			}
    	}).start();
    }
    
    private static void logStatistics() throws Exception {
        while (true) {
            Thread.sleep(1000);
            System.out.println("" + 1.0 * count.getAndSet(0) / 1024);
        }
    }
    
    public static void main(String[] args) throws Exception {
    	slice();
        logStatistics();
    }
    
}
