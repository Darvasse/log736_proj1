import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class Message {

    public static final MessageFormatter BasicFormatter = new MessageFormatter() {

        private static String[] sanitizeMessage(String[] terms) {
            for(String term : terms) {
                term = term.trim();
                term = term.replaceAll("~", "");
                term = term.replaceAll("|", "");
                term = term.replaceAll("\n", "~");
                term = term.replaceAll(",", "|");
            }
            return terms;
        }

        private static String[] naturalizeMessage(String raw) {
            raw = raw.substring(1, raw.length() - 2);
            
            String[] terms = raw.split(",");
            for(String term : terms) {
                term = term.replaceAll("~", "\n");
                term = term.replaceAll("|", ",");
            }

            return terms;
        }

        @Override
        public Object toObject(Message msg) { 
            String[] terms = new String[]{
                Long.toString(msg.logicalTimestamp),
                msg.subject,
                msg.from.toString(),
                msg.to.toString(),
                msg.header,
                msg.content
            };

            return "[" + Arrays.stream(sanitizeMessage(terms)).collect(Collectors.joining(",")) + "]"; 
        }
        @Override
        public Message fromObject(Object msg) { return (Message) msg; }

        @Override
        public Message fromString(String msg) {
            String[] terms = naturalizeMessage(msg);
            Message message = new Message();
            message.logicalTimestamp = Long.parseLong(terms[0]);
            message.subject = terms[1];
            message.from = UUID.fromString(terms[2]);
            message.to = UUID.fromString(terms[3]);
            message.header = terms[4];
            message.content = terms[5];
            return message;
        }

        @Override
        public boolean isValidHeader(String header) {
            return true;
        }

        @Override
        public String extractHeader(Object header) {
            return header.toString();
        }

        @Override
        public boolean isValidContent(String content) {
            return true;
        }

        @Override
        public String extractContent(Object content) {
            return content.toString();
        }
        
    };

    protected UUID from;
    protected UUID to;
    protected long logicalTimestamp;

    protected String subject;
    protected String header;
    protected String content;
    protected MessageFormatter formatter;

    public Message() { this(null, 0); }
    public Message(long timestamp) { this(null, timestamp); }
    public Message(MessageFormatter format, long timestamp) {
        formatter = format;
        logicalTimestamp = timestamp;
    }
    public Message(String raw) { this(BasicFormatter.fromString(raw));}
    public Message(Message other) {
        from = other.from;
        to = other.to;
        logicalTimestamp = other.logicalTimestamp;
        subject = other.subject;
        header = other.header;
        content = other.content;
        formatter = other.formatter;
    }

    public String toString() { 
        MessageFormatter format = formatter != null ? formatter : BasicFormatter;
        return format.toObject(this).toString();
            
    }

    public Message fromString(String raw) {
        MessageFormatter format = formatter != null ? formatter : BasicFormatter;
        return format.fromString(raw);
    }
    public boolean isEmpty() { return header.isEmpty() && content.isEmpty(); }

    // Getters and Setters
    public void setTo(UUID to) { this.to = to; }
    public UUID getTo() { return to; }
    public void setFrom(UUID from) { this.from = from; }
    public UUID getFrom() { return from; }
    public void setTimestamp(long timestamp) { logicalTimestamp = timestamp; }
    public long getTimestamp() { return logicalTimestamp; }

    public void setSubject(String subject) { this.subject = subject; }
    public String getSubject() { return subject; }
    public void setHeader(String header) { this.header = header; }
    public String getHeader() { return header; }
    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }
    public void setFormatter(MessageFormatter format) { formatter = format; }
    public MessageFormatter getFormatter() { return formatter; }
}
