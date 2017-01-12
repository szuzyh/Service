package model;

/**
 * Created by Leo on 2016/12/21.
 */
public class recordModel {
    private String account;
    private String created;
    private String message;
    private String level;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{\"account\":\""+account+"\""+ ",\"created\":\"" +created+"\"" +
                ",\"message\":\"" +message+"\""+",\"level\":\"" +level+"\"}";
    }


}
