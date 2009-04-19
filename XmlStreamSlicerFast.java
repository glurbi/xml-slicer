// Compared to the "elegant" solution, we basically remove any method call when processing the byte array.
public class XmlStreamSlicerFast implements XmlStreamSlicer {

    // We could use an enum but it appears that it is more costly in the switch.
    private final int searchingTagState = 1;
    private final int parsingTagState = 2;
    private final int ParsingEndTagState = 3;

    private byte[] message;
    private int index = 0;
    private int depth = -1;
    private int xmlStreamState;
    private XmlMessageProcessor listener;
    private byte previous;

    public XmlStreamSlicerFast(XmlMessageProcessor listener, int bufferCapacity) {
        this.xmlStreamState = searchingTagState;
        this.message = new byte[bufferCapacity];
        this.listener = listener;
    }

    public void process(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            switch (xmlStreamState) {
            case searchingTagState:
                if (bytes[i] == (byte) '<') {
                    if (depth == 0) {
                        index = 0;
                    }
                    depth++;
                    xmlStreamState = parsingTagState;
                }
                message[index++] = bytes[i];
                break;
            case parsingTagState:
                message[index++] = bytes[i];
                if (bytes[i] == (byte) '>') {
                    xmlStreamState = searchingTagState;
                } else if (bytes[i] == (byte) '/' && previous == (byte) '<') {
                    xmlStreamState = ParsingEndTagState;
                    depth--;
                } else if (bytes[i] == (byte) '/' && previous != (byte) '<') {
                    xmlStreamState = ParsingEndTagState;
                }
                break;
            case ParsingEndTagState:
                message[index++] = bytes[i];
                if (bytes[i] == (byte) '>') {
                    if (depth == 1) {
                        byte[] xmlMessage = new byte[index];
                        System.arraycopy(message, 0, xmlMessage, 0, index);
                        listener.process(xmlMessage);
                        index = 0;
                    }
                    xmlStreamState = searchingTagState;
                    depth--;
                }
                break;
            }
            previous = bytes[i];
        }
    }

}
