

public class MessageJSONFormatter extends Message.Formatter {

    @Override
    public String toString(Message msg) {
        JSONObject json = new JSONObject();
        json.put(Message.SubjectTitle, msg.getSubject());
        json.put(Message.HeaderTitle, interpretHeader(msg));
        json.put(Message.ContentTitle, interpretContent(msg));
        return json.toString().replace("\n", " ");
    }

    @Override
    public Message fromString(String msg) {
        JSONObject json = new JSONObject(msg);
        String subject = json.getString(Message.SubjectTitle);
        JSONObject header = json.getJSONObject(Message.HeaderTitle);
        JSONObject content = json.getJSONObject(Message.ContentTitle);

        Message message = new Message();
        message.setSubject(subject);
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