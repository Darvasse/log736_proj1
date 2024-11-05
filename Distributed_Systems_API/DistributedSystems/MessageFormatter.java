
/**
 * Interface for formatting messages.
 */
interface MessageFormatter {

    /**
     * Converts a Message object to a generic Object.
     *
     * @param msg the Message object to be converted
     * @return the converted Object
     */
    public Object toObject(Message msg);

    /**
     * Converts a generic Object to a Message object.
     *
     * @param msg the Object to be converted
     * @return the converted Message object
     */
    public Message fromObject(Object msg);

    /**
     * Converts a String to a Message object.
     *
     * @param msg the String to be converted
     * @return the converted Message object
     */
    public Message fromString(String msg);

    /**
     * Validates the header of a message.
     *
     * @param header the header to be validated
     * @return true if the header is valid, false otherwise
     */
    public boolean isValidHeader(String header);

    /**
     * Extracts the header from a given Object.
     *
     * @param header the Object containing the header
     * @return the extracted header as a String
     */
    public String extractHeader(Object header);

    /**
     * Validates the content of a message.
     *
     * @param content the content to be validated
     * @return true if the content is valid, false otherwise
     */
    public boolean isValidContent(String content);

    /**
     * Extracts the content from a given Object.
     *
     * @param content the Object containing the content
     * @return the extracted content as a String
     */
    public String extractContent(Object content);
}