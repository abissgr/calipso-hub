package gr.abiss.calipso.model.types;

public enum ResourceProtocol {
	
	HTTP("http://"), 
	HTTPS("https://");

    private final String urlPrefix;
    
	/**
     * @param text the URL prefix commonly used for the protocol
     */
    private ResourceProtocol(final String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    /**
     * Return the URL prefix commonly used for the protocol
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return urlPrefix;
    }
}
