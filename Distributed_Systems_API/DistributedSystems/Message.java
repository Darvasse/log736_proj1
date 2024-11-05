import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents a message with a logical timestamp, subject, sender, receiver, header, and content.
 * Implements the Comparable interface to allow comparison based on the logical timestamp.
 */
public class Message implements Comparable<Message> {

    /**
     * A basic formatter for the Message class that provides methods to convert
     * a Message object to a string representation and vice versa.
     */
    public static final MessageFormatter BasicFormatter = new MessageFormatter() {

        /**
         * Sanitizes the message terms by trimming whitespace and replacing certain characters.
         *
         * @param terms the array of message terms to sanitize
         * @return the sanitized array of message terms
         */
        private static String[] sanitizeMessage(String[] terms) {
            for(int i = 0; i < terms.length; ++i) {
                String term = terms[i].trim();
                term = term.replaceAll("\\~", "");
                term = term.replaceAll("\\|", "");
                term = term.replaceAll("\\\n", "~");
                term = term.replaceAll("\\,", "|");
                terms[i] = term;
            }
            return terms;
        }

        /**
         * Converts a raw string representation of a message back into an array of terms.
         *
         * @param raw the raw string representation of the message
         * @return the array of message terms
         */
        private static String[] naturalizeMessage(String raw) {
            raw = raw.substring(1, raw.length() - 1);
            
            String[] terms = raw.split(",");
            for(int i = 0; i < terms.length; ++i) {
                String term = terms[i].replaceAll("\\~", "\n");
                term = term.replaceAll("\\|", ",");
                terms[i] = term;
            }

            return terms;
        }

        /**
         * Converts a Message object to its string representation.
         *
         * @param msg the Message object to convert
         * @return the string representation of the Message object
         */
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

        /**
         * Converts an object back into a Message object.
         *
         * @param msg the object to convert
         * @return the Message object
         */
        @Override
        public Message fromObject(Object msg) { return (Message) msg; }

        /**
         * Converts a string representation of a message back into a Message object.
         *
         * @param msg the string representation of the message
         * @return the Message object
         */
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

        /**
         * Checks if the given header is valid.
         *
         * @param header the header to check
         * @return true if the header is valid, false otherwise
         */
        @Override
        public boolean isValidHeader(String header) {
            return true;
        }

        /**
         * Extracts the header from the given object.
         *
         * @param header the object containing the header
         * @return the extracted header as a string
         */
        @Override
        public String extractHeader(Object header) {
            return header.toString();
        }

        /**
         * Checks if the given content is valid.
         *
         * @param content the content to check
         * @return true if the content is valid, false otherwise
         */
        @Override
        public boolean isValidContent(String content) {
            return true;
        }

        /**
         * Extracts the content from the given object.
         *
         * @param content the object containing the content
         * @return the extracted content as a string
         */
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

    /**
     * Default constructor that initializes a Message with a null formatter and a timestamp of 0.
     */
    public Message() { this(null, 0); }

    /**
     * Constructor that initializes a Message with a null formatter and the given timestamp.
     *
     * @param timestamp the logical timestamp of the message
     */
    public Message(long timestamp) { this(null, timestamp); }

    /**
     * Constructor that initializes a Message with the given formatter and timestamp.
     *
     * @param format the formatter to use for the message
     * @param timestamp the logical timestamp of the message
     */
    public Message(MessageFormatter format, long timestamp) {
        formatter = format;
        logicalTimestamp = timestamp;
    }

    /**
     * Constructor that initializes a Message from a raw string representation.
     *
     * @param raw the raw string representation of the message
     */
    public Message(String raw) { this(BasicFormatter.fromString(raw));}

    /**
     * Copy constructor that initializes a Message with the values from another Message.
     *
     * @param other the Message to copy values from
     */
    public Message(Message other) {
        from = other.from;
        to = other.to;
        logicalTimestamp = other.logicalTimestamp;
        subject = other.subject;
        header = other.header;
        content = other.content;
        formatter = other.formatter;
    }

    /**
     * Compares this message to another message based on the logical timestamp.
     *
     * @param o the other message to compare to
     * @return a negative integer, zero, or a positive integer as this message's timestamp
     *         is less than, equal to, or greater than the other message's timestamp
     */
    @Override
    public int compareTo(Message o) {
        return (int) (logicalTimestamp - o.logicalTimestamp);
    }

    /**
     * Converts this message to its string representation using the formatter.
     *
     * @return the string representation of this message
     */
    public String toString() { 
        MessageFormatter format = formatter != null ? formatter : BasicFormatter;
        return format.toObject(this).toString();
    }

    /**
     * Converts a raw string representation of a message back into a Message object using the formatter.
     *
     * @param raw the raw string representation of the message
     * @return the Message object
     */
    public Message fromString(String raw) {
        MessageFormatter format = formatter != null ? formatter : BasicFormatter;
        return format.fromString(raw);
    }

    /**
     * Checks if the message is empty (both header and content are empty).
     *
     * @return true if the message is empty, false otherwise
     */
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
