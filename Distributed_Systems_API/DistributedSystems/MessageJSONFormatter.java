

public class MessageJSONFormatter extends Message.Formatter {

    @Override
    public String toString(Message msg) {
        JSONObject json = new JSONObject();
        json.put(Message.SubjectTag, msg.getSubject());
        json.put(Message.SourceTag, msg.getFrom());
        json.put(Message.DestinationTag, msg.getTo());
        json.put(Message.HeaderTag, interpretHeader(msg));
        json.put(Message.ContentTag, interpretContent(msg));
        return json.toString().replace("\n", " ");
    }

    @Override
    public Message fromString(String msg) {
        JSONObject json = new JSONObject(msg);
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

    public JSONObject interpretHeader(Message msg) {
        return new JSONObject(msg.getHeader());
    }

    public JSONObject interpretContent(Message msg) {
        return new JSONObject(msg.getContent());
    }
    
}