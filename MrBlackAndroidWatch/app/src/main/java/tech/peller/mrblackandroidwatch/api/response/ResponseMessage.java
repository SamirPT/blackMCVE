package tech.peller.mrblackandroidwatch.api.response;

import com.google.gson.annotations.SerializedName;

public class ResponseMessage {

    @SerializedName("response")
    private String response;

    public ResponseMessage(String response) {
        this.response = response;
    }

    public ResponseMessage() {
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResponseMessage {\n");

        sb.append("    response: ").append(toIndentedString(response)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
