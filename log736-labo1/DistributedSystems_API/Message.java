package DistributedSystems_API;

import java.util.ArrayList;
import java.util.UUID;

public class Message {
    
    private static final String SubjectTitle = "subject";
    private static final String HeaderTitle = "header";
    private static final String ContentTitle = "content";

    private String subject;
    private String header;
    private String content;

    private UUID from;
    private UUID to;

    public Message() {}

    public static Message fromString(String raw) {
        //final String MessageFormat = "{subject:[\\w ]*,header:[\\w ]*,content:[\\w ]*}";
        Message message = new Message();
        //if(raw.matches(MessageFormat)) {
            String[] parts = raw.substring(1, raw.length() - 1).split(",");

            for(String element : parts) {
                String[] separated = element.split(":");
                if(separated.length == 2) {
                    switch (separated[0]) {
                        case SubjectTitle:  message.subject = separated[1];     break;
                        case HeaderTitle:   message.header = separated[1];      break;
                        case ContentTitle:  message.content = separated[1];     break;
                        default: break;
                    }
                }
            }
        //}

        return message;
    }

    public String toString() {
        String message = "{";
        message += SubjectTitle + ":" + subject + ",";
        message += HeaderTitle + ":" + header + ",";
        message += ContentTitle + ":" + content;
        message += "}";
        return message;
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


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
