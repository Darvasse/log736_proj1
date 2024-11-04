/* NEED org.java to work 
import java.text.MessageFormat;
import java.util.UUID;
import org.json.JSONObject;

public class JSONMessage extends Message {
    public static final String SubjectTag = "subject";
    public static final String HeaderTag = "header";
    public static final String ContentTag = "content";
    public static final String SourceTag = "from";
    public static final String DestinationTag = "to";

    protected static final MessageFormatter Formatter = new MessageFormatter() {
        @Override
        public Object toObject(Message msg) {
            JSONObject json = new JSONObject();
            json.put(Message.SubjectTag, msg.getSubject());
            json.put(Message.SourceTag, msg.getFrom());
            json.put(Message.DestinationTag, msg.getTo());
            json.put(Message.HeaderTag, interpretHeader(msg));
            json.put(Message.ContentTag, interpretContent(msg));
            return json;
        }

        @Override
        public Message fromString(String msg) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'fromString'");
        }

        @Override
        public Message fromObject(Object msg) {
            if(!(msg instanceof JSONObject)) {
                throw new IllegalArgumentException("Message must be a JSONObject");
            }

            JSONObject json = (JSONObject) msg;
            String subject = json.getString(Message.SubjectTag);
            UUID source = UUID.fromString(json.getString(Message.SourceTag));
            UUID destination = UUID.fromString(json.getString(Message.DestinationTag));
            JSONObject header = json.getJSONObject(Message.HeaderTag);
            JSONObject content = json.getJSONObject(Message.ContentTag);

            Message message = new Message();
            message.setSubject(subject);
            message.setFrom(source);
            message.setTo(destination);
            message.setHeader(header.toString());
            message.setContent(content.toString());

            return message;
        }

        @Override
        public boolean isValidHeader(String header) {
            try {
                new JSONObject(header);
                return true;
            } catch (JsonException e) {
                return false;
            }
        }

        @Override
        public String extractHeader(Object header) {
            return ((JSONObject) header).toString();
        }

        @Override
        public boolean isValidContent(String content) {
            try {
                new JSONObject(content);
                return true;
            } catch (JsonException e) {
                return false;
            }
        }

        @Override
        public String extractContent(Object content) {
            return ((JSONObject) content).toString();
        }

    };

    public MessageJSONFormatter() {
        super(Formatter, -1);
    }
    public MessageJSONFormatter(long timestamp) {
        super(Formatter, timestamp);
    }

    @Override
    public void setHeader(String header) {
        if(Formatter.isValidHeader(header)) {
            setHeader(new JSONObject(header));
        }
    }
    public void setContent(JSONObject content) {
       content = Formatter.extractContent(content);
    }

    @Override
    public void setContent(String content) {
        if(Formatter.isValidContent(content)) {
            setContent(new JSONObject(content));
        }
    }

    public void setContent(JSONObject content) {
       content = Formatter.extractContent(content);
    }

    @Override
    public void setFormatter(MessageFormatter format) {
        throw new UnsupportedOperationException("MessageJSONFormatter cannot be changed.");
    }
    
    @Override
    public MessageFormatter getFormatter() {
        return Formatter;
    }
}
*/