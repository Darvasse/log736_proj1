package DistributedSystems;

import java.util.UUID;

public class Message {
    
    private static final String SubjectTag = "subject";
    private static final String HeaderTag = "header";
    private static final String ContentTag = "content";
    private static final String SourceTag = "from";
    private static final String DestinationTag = "to";

    private UUID from;
    private UUID to;
    private long logicalTimestamp;

    private String subject;
    private String header;
    private String content;
    private Formatter converter;


    public static abstract class Formatter {
        public abstract String toString(Message msg);
        public abstract Message fromString(String msg);
    }

    public Message() { this(null, 0); }
    public Message(long timestamp) { this(null, timestamp); }
    public Message(Formatter format, long timestamp) {
        converter = format != null ? format : new MessageJSONFormatter();
        logicalTimestamp = timestamp;
    }

    public String toString() { return Formatter.toString(this); }
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
}
