package tech.peller.mrblackandroidwatch.api.response;

public class ErrorMessage {
    private String error;
    private String fields;

    public ErrorMessage(String error, String fields) {
        this.error = error;
        this.fields = fields;
    }

    public ErrorMessage() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}
