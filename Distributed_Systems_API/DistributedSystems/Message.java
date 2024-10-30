package DistributedSystems;

import java.util.UUID;

public class Message {
    
    private static final String SubjectTitle = "subject";
    private static final String HeaderTitle = "header";
    private static final String ContentTitle = "content";

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

    public String toString() {
        return Formatter.toString(this);
    }

    public boolean isEmpty() {
        return header.isEmpty() && content.isEmpty();
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public long getTimestamp() {
        return logicalTimestamp;
    }

    public void setTimestamp(long timestamp) {
        logicalTimestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
