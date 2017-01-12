package model;

/**
 * Created by Leo on 2017/1/4.
 */
public class UserMsg {

    private String user;
    private String realname;
    private String tel;
    private String email;
    private String hotelname;
    private String location;
    private String userlevel;
    private String remain;
    private String created;
    private String total;
    private String account;
    private String sex;
    private String average;
    private String recharge;
    @Override
    public String toString() {
        return "{\"user\":\""+user+"\""+ ",\"realname\":\"" +realname+"\"" +
                ",\"tel\":\"" +tel+"\""+",\"email\":\"" +email+"\""+",\"hotelname\":\"" +hotelname+"\""+
                ",\"location\":\"" +location+"\""+",\"remain\":\"" +remain+"\""+",\"created\":\"" +created+"\""+
                ",\"userlevel\":\"" +userlevel+"\""+",\"recharge\":\"" +recharge+"\""+",\"sex\":\"" +sex+"\""+",\"average\":\"" +average+"\""+",\"account\":\"" +account+"\""+",\"total\":\"" +total+"\"}";
    }


    public String getRecharge() {
        return recharge;
    }

    public void setRecharge(String recharge) {
        this.recharge = recharge;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }



    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHotelname() {
        return hotelname;
    }

    public void setHotelname(String hotelname) {
        this.hotelname = hotelname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserlevel() {
        return userlevel;
    }

    public void setUserlevel(String userlevel) {
        this.userlevel = userlevel;
    }

    public String getRemain() {
        return remain;
    }

    public void setRemain(String remain) {
        this.remain = remain;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }


}
