package site.easy.to.build.crm.exception;

public class ImportException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public ImportException(String message) {
        super(message);
    }
    
    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
