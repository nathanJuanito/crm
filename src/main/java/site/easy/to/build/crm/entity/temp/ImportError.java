package site.easy.to.build.crm.entity.temp;

public class ImportError {
    private String fileName;
    private int lineNumber;
    private String errorMessage;
    private String rawData;
    
    // Constructeurs, getters et setters
    public ImportError() {}
    
    public ImportError(String fileName, int lineNumber, String errorMessage, String rawData) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.errorMessage = errorMessage;
        this.rawData = rawData;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getRawData() {
        return rawData;
    }
    
    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
    
    @Override
    public String toString() {
        return "ImportError{" +
                "fileName='" + fileName + '\'' +
                ", lineNumber=" + lineNumber +
                ", errorMessage='" + errorMessage + '\'' +
                ", rawData='" + rawData + '\'' +
                '}';
    }
}
