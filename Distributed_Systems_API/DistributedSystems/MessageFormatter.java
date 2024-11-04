interface MessageFormatter {
    
    public Object toObject(Message msg);

    public Message fromObject(Object msg);
    public Message fromString(String msg);

    public boolean isValidHeader(String header);
    public String extractHeader(Object header);

    public boolean isValidContent(String content);
    public String extractContent(Object content);
}