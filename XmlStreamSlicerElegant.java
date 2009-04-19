public class XmlStreamSlicerElegant implements XmlStreamSlicer {

    private interface XmlStreamState {
        public void process();
    }

    private byte[] message;
    private int index = 0;
    private int depth = -1;
    private XmlStreamState xmlStreamState;
    private XmlMessageProcessor listener;
    private byte previous;
    private byte current;

    public XmlStreamSlicerElegant(XmlMessageProcessor listener, int bufferCapacity) {
        this.xmlStreamState = searchingTagState;
        this.message = new byte[bufferCapacity];
        this.listener = listener;
    }

    public void process(byte[] bytes) {
        for (byte b : bytes) {
            previous = current;
            current = b;
            xmlStreamState.process();
        }
    }
    
    private void append(byte b) {
        message[index] = b;
        index++;
    }
    
    private XmlStreamState searchingTagState = new XmlStreamState() {
        public void process() {
            if (current == (byte) '<') {
                if (depth == 0) {
                    index = 0;
                }
                depth++;
                xmlStreamState = parsingTagState;
            }
            append(current);
        }
    };

    private XmlStreamState parsingTagState = new XmlStreamState() {
        public void process() {
            append(current);
            if (current == (byte) '>') {
                xmlStreamState = searchingTagState;
            } else if (current == (byte) '/' && previous == (byte) '<') {
                xmlStreamState = parsingEndTagState;
                depth--;
            } else if (current == (byte) '/' && previous != (byte) '<') {
                xmlStreamState = parsingEndTagState;
            }
        }
    };

    private XmlStreamState parsingEndTagState = new XmlStreamState() {
        public void process() {
            append(current);
            if (current == (byte) '>') {
                if (depth == 1) {
                    byte[] xmlMessage = new byte[index];
                    System.arraycopy(message, 0, xmlMessage, 0, index);
                    listener.process(xmlMessage);
                    index = 0;
                }
                xmlStreamState = searchingTagState;
                depth--;
            }
        }
    };

}
