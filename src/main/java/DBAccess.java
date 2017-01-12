import model.UserMsg;
import model.recordModel;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Leo on 2016/12/20.
 */
public class DBAccess {
    String driver="com.mysql.jdbc.Driver";
    //这里以后需要修改！！

    String connectionStr="jdbc:mysql://localhost:3306/facial?user=root&password=passwd&useUnicode=true&characterEncoding=utf-8";
    String url="jdbc:mysql://localhost:3306/facial";
    String user="root";
    String password="passwd";
    Connection connection=null;
    Statement statement=null;
    public void init(){
        try{
            Class.forName(driver);
            System.out.println("找到驱动");
           connection= DriverManager.getConnection(url,user,password);
           // connection=DriverManager.getConnection(connectionStr);
            System.out.println("开始连接");
            statement=connection.createStatement();
            System.out.println("连接成功");
        } catch (ClassNotFoundException e) {
            System.out.println("找不到驱动程序");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 /**
  *Author:ace
  *@Date: 2016/12/20 9:41
  *Decpration:注册：  用户名、密码、真实姓名、电话、邮箱、宾馆名、位置（用户输入）、等级（初步都为0）
  */



/**
 *Author:ace
 *@Date: 2016/12/20 10:48
 *Decpration:查询 防止重复注册  通过用户名、电话、邮箱来
 */
    public boolean queryIsExist(String account) throws SQLException {
        String strName;
        if (account.endsWith(".com")){
            strName="select password from register where email='"+account+"'";
        }else {
             strName="select password from register where account='"+account+"'";
        }
            ResultSet resultSet=statement.executeQuery(strName);
            if (resultSet.next()){
                return true;
            }else {
                return false;
            }
    }

    /**
     *Author:ace
     *@Date: 2016/12/20 11:48
     *Decpration:查询密码 用于验证登录
     */
    public String queryPasswd(String account) throws SQLException {
        String strName;
        String passwd = null;
        if (account.endsWith(".com")){
            strName="select password from register where email='"+account+"'";
        }else {
            strName="select password from register where account='"+account+"'";
        }
        ResultSet resultSet=statement.executeQuery(strName);
        resultSet.next();
        passwd=resultSet.getString("password");
        return passwd;
    }

    public void record(String account,String level,String c) throws SQLException {
        Date date=new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(date);
        Timestamp timestamp= Timestamp.valueOf(nowTime);
        String remain;
        String s=queryRemain(account);
        int b= Integer.parseInt(s);

        if (b==0){
                String msg="用户:"+account+" 剩余次数为:0";
                System.out.println(timestamp);
                String string = "insert into pay(account,created,message,level) values('" + account + "','" + timestamp + "','" + msg + "','"+level+"')";
                statement.execute(string);
            }else {
                b=b-1;
                remain= String.valueOf(b);
                updateRemain(remain,account);
                String recordTime=queryRecordTimes(account);
                int Time= Integer.parseInt(recordTime);
                Time+=1;
                String t= String.valueOf(Time);
                updateRecordTime(t,account);
                String msg="用户:"+account+" 进行了一次对比,相似度为:"+c+",剩余次数为:"+remain;
                String string = "insert into pay(account,created,message,level) values('" + account + "','" + timestamp + "','" + msg + "','"+level+"')";
                statement.execute(string);
                int average=queryMessage(account);
                updateAverage(account,average);
        }

    }

    private void updateAverage(String account, int average) throws SQLException {
        String sql="update register set average='"+average+"' where account='"+account+"'";
        statement.execute(sql);
    }

    private void updateRecordTime(String time,String account) throws SQLException {
        String sql="update register set total='"+time+"' where account='"+account+"'";
        statement.execute(sql);
    }

    public UserMsg queryUMsg(String token) throws SQLException {
        String SQL;
        if (token.endsWith(".com")){
            SQL="select * from register where email='"+token+"'";
        }else {
            SQL="select * from register where account='"+token+"'";
        }
        ResultSet set=statement.executeQuery(SQL);
        UserMsg userMsg=new UserMsg();
        while (set.next()){
            userMsg.setAverage(set.getString("average"));
            userMsg.setUser(set.getString("user"));
            userMsg.setSex(set.getString("sex"));
            userMsg.setRealname(set.getString("realname"));
            userMsg.setTel(set.getString("tel"));
            userMsg.setEmail(set.getString("email"));
            userMsg.setLocation(set.getString("location"));
            userMsg.setHotelname(set.getString("hotelname"));
            userMsg.setRemain(set.getString("remain"));
            userMsg.setUserlevel(set.getString("userlevel"));
            userMsg.setTotal(set.getString("total"));
            userMsg.setCreated(set.getString("created"));
            userMsg.setAccount(set.getString("account"));
            userMsg.setRecharge(set.getString("recharge"));
        }
        return userMsg;
    }
    public List<recordModel> qureyRecord(String account) throws SQLException {
        String sql="SELECT * FROM pay where account='"+account+"'";
        ResultSet resultSet=statement.executeQuery(sql);
        List<recordModel> recordList=new ArrayList<>();
        while (resultSet.next()){
            recordModel recordModel=new recordModel();
            recordModel.setAccount(resultSet.getString("account"));
            recordModel.setMessage(resultSet.getString("message"));
            recordModel.setCreated(resultSet.getString("created"));
            recordModel.setLevel(resultSet.getString("level"));
            recordList.add(recordModel);
        }
        return recordList;
    }
    /**
     *Author:ace
     *@Date: 2016/12/22 16:07
     *Decpration:通过用户名查到余额
     */
    public String queryRemain(String account) throws SQLException {
        String sql="select remain from register where account='"+account+"'";
        ResultSet set=statement.executeQuery(sql);
        String s=null;
        if (set.next()){
            s=set.getString("remain");
        }
        return s;
    }
    public String queryRecordTimes(String account) throws SQLException {
        String sql="select total from register where account='"+account+"'";
        ResultSet set=statement.executeQuery(sql);
        String s=null;
        if (set.next()){
            s=set.getString("total");
        }
        return s;
    }

    public void updateUMsg(String account, String user_name,  String realname, String tel, String email, String hotelname, String location,String sex) throws SQLException {
        String sql;
        if (account.endsWith(".com")){
            sql="update register SET user="+"'"+user_name+"', realname="+"'"+realname+"', tel="+"'"+tel+"', email="+"'"+email+"', hotelname="+"'"+hotelname+"', sex="+"'"+sex+"', location="+"'"+location+"'"+" where email='"+account+"'";
        }else {
            sql="update register SET user="+"'"+user_name+"', realname="+"'"+realname+"', tel="+"'"+tel+"', email="+"'"+email+"', hotelname="+"'"+hotelname+"', sex="+"'"+sex+"', location="+"'"+location+"'"+" where account='"+account+"'";
        }
        statement.execute(sql);
    }

    public void updateName(String account, String user_name) throws SQLException {
        String sql="update register set user='"+user_name+"' where account='"+account+"'";
        statement.execute(sql);
    }
    /**
     *Author:ace
     *@Date: 2016/12/30 15:16
     *Decpration:计费同时更新名字
     */

    public void updatePassord(String password, String account) throws SQLException {
        String sql="update register set password='"+password+"' where account='"+account+"'";
        statement.execute(sql);
    }

    public void updateRealname(String realname, String account) throws SQLException {
        String sql="update register set realname='"+realname+"' where account='"+account+"'";
        statement.execute(sql);
    }
    public void updateTel(String tel, String account) throws SQLException {
        String sql="update register set tel='"+tel+"' where account='"+account+"'";
        statement.execute(sql);
    }
    public void updateUserlevel(String userlevel, String account) throws SQLException {
        String sql="update register set userlevel='"+userlevel+"' where account='"+account+"'";
        statement.execute(sql);
    }
    public void updateEmail(String email, String account) throws SQLException {
        String sql="update register set email='"+email+"' where account='"+account+"'";
        statement.execute(sql);
    }
    public void updateHotelname(String hotelname, String account) throws SQLException {
        String sql="update register set hotelname='"+hotelname+"' where account='"+account+"'";
        statement.execute(sql);
    }
    public void updateLocation(String location, String account) throws SQLException {
        String sql="update register set location='"+location+"' where account='"+account+"'";
        statement.execute(sql);
    }
    public void updateAvatar(Blob avatar, String account) throws SQLException {
        String sql="update register set avatar='"+avatar+"' where account='"+account+"'";
        statement.execute(sql);
    }
    public void updateRemain(String remain, String account) throws SQLException {
        String sql="update register set remain='"+remain+"' where account='"+account+"'";
        statement.execute(sql);
    }

    public void registerUser_BasicMsg(String user, String password, String email, String account) throws SQLException {
        Date date=new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(date);
        Timestamp timestamp= Timestamp.valueOf(nowTime);
        String str="insert into register(user,password,realname,tel,email,hotelname,location,userlevel,remain,created,total,account,sex,average,recharge) values('"+user+"','"+password+"','','','"+email+"','','','1','100','"+timestamp+"','0','"+account+"','','0','0')";
        statement.execute(str);
    }


    public Blob queryAvatar(String user) throws SQLException {
        String sql="select avatar from register where user='"+user+"'";
        ResultSet resultSet=statement.executeQuery(sql);
        Blob blob=null;
        if (resultSet.next()) {
            blob = resultSet.getBlob("avatar");
        }
        return blob;
    }

    public boolean queryEmail(String email) throws SQLException {
        String sql="select * from register where email='"+email+"'";
        ResultSet resultSet=statement.executeQuery(sql);
        if (resultSet.next()){
            return true;
        }else {
            return false;
        }
    }

    public int queryMessage(String account) throws SQLException {
        String sql="select * from pay where account='"+account+"'and level='info'";
        ResultSet resultSet=statement.executeQuery(sql);
        List<String> msgs =new ArrayList<>();
        while (resultSet.next()){
            String msg=resultSet.getString("message");
            msgs.add(msg);
        }
        if (msgs.size()>0){
            int[] confindence=new int[msgs.size()];
            for (int i=0;i<msgs.size();i++){
                String s=msgs.get(i);
                String[] sArray=s.split("为:");
                String s1=sArray[1];
                String[] array=s1.split(",");
                String c=array[0];
                confindence[i]= Integer.parseInt(c);
            }
            int average=0;
            for (int i=0;i<confindence.length;i++){
                average+=confindence[i];
            }
            average=average/confindence.length;
            return average;
        }else {
            return -1;
        }
    }


    public String queryAccount(String email) throws SQLException {
        String sql="select account from register where email='"+email+"'";
        ResultSet resultSet=statement.executeQuery(sql);
        resultSet.next();
        return resultSet.getString("account");
    }

    public String queryRecharge(String account) throws SQLException {
        String sql;
        if (account.endsWith(".com")){
            sql="select recharge from register where email='"+account+"'";
        }else {
            sql="select recharge from register where account='"+account+"'";
        }
        ResultSet set=statement.executeQuery(sql);
        set.next();
        return set.getString("recharge");
    }

    public void updateRecharge(String account, String i) throws SQLException {
        String sql;
        if (account.endsWith(".com")){
            sql="update  register set recharge='"+i+"' where email='"+account+"'";
        }else {
            sql="update  register set recharge='"+i+"' where account='"+account+"'";
        }
        statement.execute(sql);
    }

    public void addRechargeRecord(String account, String i,String sum) throws SQLException {
        Date date=new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(date);
        Timestamp timestamp= Timestamp.valueOf(nowTime);
        if (account.endsWith(".com")){
            account=queryAccount(account);
        }
        System.out.println("22");
        String msg="用户:"+account+" 进行了一次充值,充值金额为:"+sum+",账户金额为:"+i;
        String string = "insert into pay(account,created,message,level) values('" + account + "','" + timestamp + "','" + msg + "','info')";
        statement.execute(string);
    }
}
