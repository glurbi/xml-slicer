import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class XmlStreamSlicerTest {

    public void testElegantXmlStreamWithStreamShorterThanBuffer() throws Exception {
        final AtomicInteger index = new AtomicInteger();
        final String[] xmlMessages = new String[] { "<b>content</b>", "<c><b>content</b></c>",
                "<c><b>content</b><b>content<d>thing</d></b></c>", "<b>before<a/>after</b>", "<sometag/>",
                "<sometag with='attribute'/>", "<c with='attribute'><b>content</b><b>content<d>thing</d></b></c>",
                "<outer><innner/></outer>" };
        String xmlStream = "<a>" + xmlMessages[0] + xmlMessages[1] + xmlMessages[2] + xmlMessages[3] + xmlMessages[4]
                + xmlMessages[5] + xmlMessages[6] + "before" + xmlMessages[7] + "after";
        XmlMessageProcessor listener = new XmlMessageProcessor() {
            public void process(byte[] message) {
                assert Arrays.equals(xmlMessages[index.get()].getBytes(), message);
                index.incrementAndGet();
            }
        };
        XmlStreamSlicer stream = new XmlStreamSlicerElegant(listener, 512);
        stream.process(xmlStream.getBytes());
        assert xmlMessages.length == index.get();
    }

    public void testElegantXmlStreamWithStreamBiggerThanBuffer() throws Exception {
        final AtomicInteger index = new AtomicInteger();
        final String[] xmlMessages = new String[] { "<b>content</b>", "<c><b>content</b></c>",
                "<c><b>content</b><b>content<d>thing</d></b></c>", "<b>before<a/>after</b>", "<sometag/>",
                "<sometag with='attribute'/>", "<c with='attribute'><b>content</b><b>content<d>thing</d></b></c>",
                "<outer><innner/></outer>" };
        String xmlStream = "<a>" + xmlMessages[0] + xmlMessages[1] + xmlMessages[2] + xmlMessages[3] + xmlMessages[4]
                + xmlMessages[5] + xmlMessages[6] + "before" + xmlMessages[7] + "after";
        XmlMessageProcessor listener = new XmlMessageProcessor() {
            public void process(byte[] message) {
                assert Arrays.equals(xmlMessages[index.get()].getBytes(), message);
                index.incrementAndGet();
            }
        };
        XmlStreamSlicer stream = new XmlStreamSlicerElegant(listener, 64);
        stream.process(xmlStream.getBytes());
        assert xmlMessages.length == index.get();
    }

    public void testFastXmlStreamWithStreamBiggerThanBuffer() throws Exception {
        final AtomicInteger index = new AtomicInteger();
        final String[] xmlMessages = new String[] { "<b>content</b>", "<c><b>content</b></c>",
                "<c><b>content</b><b>content<d>thing</d></b></c>", "<b>before<a/>after</b>", "<sometag/>",
                "<sometag with='attribute'/>", "<c with='attribute'><b>content</b><b>content<d>thing</d></b></c>",
                "<outer><innner/></outer>" };
        String xmlStream = "<a>" + xmlMessages[0] + xmlMessages[1] + xmlMessages[2] + xmlMessages[3] + xmlMessages[4]
                + xmlMessages[5] + xmlMessages[6] + "before" + xmlMessages[7] + "after";
        XmlMessageProcessor listener = new XmlMessageProcessor() {
            public void process(byte[] message) {
                assert Arrays.equals(xmlMessages[index.get()].getBytes(), message);
                index.incrementAndGet();
            }
        };
        XmlStreamSlicer stream = new XmlStreamSlicerFast(listener, 64);
        stream.process(xmlStream.getBytes());
        assert xmlMessages.length == index.get();
    }

    public static void main(String[] args) throws Exception {
    	XmlStreamSlicerTest tester = new XmlStreamSlicerTest();
    	tester.testElegantXmlStreamWithStreamShorterThanBuffer();
    	tester.testElegantXmlStreamWithStreamBiggerThanBuffer();
    	tester.testFastXmlStreamWithStreamBiggerThanBuffer();
	}
    
}
