import org.json.JSONObject;

/**
 * Created by Leo on 2016/12/21.
 */
public class responseJson extends JSONObject {
    private String detail;
    private String status;
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{\"detail\":\""+detail+"\""+ ",\"status\":\"" +status+"\"}";
    }
    public String toJsonString() {
        return "{\"detail\":"+detail+ ",\"status\":\"" +status+"\"}";
    }
}
