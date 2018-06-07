package tech.peller.mrblackandroidwatch.models.log;

/**
 * Created by Sam (salyasov@gmail.com) on 31.05.2018
 */
public class LogTO {
    private String url;
    private String message;

    public LogTO() {}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
