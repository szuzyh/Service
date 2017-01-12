import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import http.CommonOperate;
import model.UserMsg;
import model.recordModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Spark;
import spark.utils.IOUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Leo on 2016/12/28.
 */
public class Main {
    static String api_key="pPcCQavGfltqRq6m8vIFgALMpmaS3BhI";
    static String api_secret="dbwoZSx8BUYRIwQudeSxZiab7WnQEhST";
    static CommonOperate commonOperate=new CommonOperate(api_key,api_secret);


    public static void main(String[] args) throws Exception {

        // saveMinPhoto(file.getAbsolutePath(),"//home//img//min.jpg",139,0.9d);
        register();
        getUMsg();
        //uploadRecord();
        getRecord();
        updateMsg();
        updateName();
        updatePassword();
        updateRealname();
        updateTel();
        updateEmail();
        updateHotelname();
        updateLocation();
        updateUserlevel();
        updateRemain();

       getAvatar();

        compare();
        updateAvatar();

      //  getUAvatar();

        getRecord_average();
        
        //登录
        login();

        /**
         *Author:ace
         *@Date: 2017/1/12 9:06
         *Decpration:上传头像，更新头像，接收数据并保存为图片。
         */
        upgrade();
        /**
         *Author:ace
         *@Date: 2017/1/12 15:42
         *Decpration:充值
         */
        recharge();
    }

    private static void recharge() {
        Spark.post("/api/recharge",(request, response) -> {
            response.header("Content-Type", "application/json");
            responseJson rj=new responseJson();
            String account=request.queryParams("account");
            String sum=request.queryParams("sum");
            DBAccess dbAccess=new DBAccess();

            dbAccess.init();
            String recharge=dbAccess.queryRecharge(account);
            int i= Integer.parseInt(recharge);
            i+=Integer.parseInt(sum);
            System.out.println("0");
            dbAccess.updateRecharge(account, String.valueOf(i));
            System.out.println("11");
            dbAccess.addRechargeRecord(account, String.valueOf(i),sum);
            rj.setStatus("success");
            rj.setDetail("recharge");
            return rj.toString();
        });
    }

    private static void upgrade() {
        Spark.post("/api/upgrade",(request, response) -> {
          //  String data=request.queryParams("imageName");

            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            File file;
            String name=request.raw().getPart("imageName").getSubmittedFileName();
            System.out.println(request.raw().getPart("imageName").getSubmittedFileName());
            try (InputStream is = request.raw().getPart("imageName").getInputStream()) {

                if (name.contains("head")){
                    file=new File("//home//zonion//upgrade//avatar//"+name);
                }else if (name.contains("person")){
                    file=new File("//home//zonion//upgrade//compare//"+name);
                }else {
                    file=new File("//home//zonion//upgrade//compare//"+name);
                }

                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                OutputStream outputStream=new FileOutputStream(file);
                outputStream.write(b);
                outputStream.close();
                // Use the input stream to create a file
            }
            if (name.contains("head")){
                System.out.println("压缩了");
                Map<String,Long> map=getImgInfo(file.getAbsolutePath());
                int width= Math.toIntExact(map.get("width"));
                int height= Math.toIntExact(map.get("height"));
                zipImageFile(file.getAbsolutePath(),width/2, height/2, 1f, "");
            }
            response.header("Content-Type", "application/json");
            responseJson rj=new responseJson();
            rj.setDetail("success");
            rj.setStatus("success");
            return rj.toString();
        });
    }

    public static Map<String, Long> getImgInfo(String imgpath) {
        Map<String, Long> map = new HashMap<String, Long>(3);
        File imgfile = new File(imgpath);
        try {
            FileInputStream fis = new FileInputStream(imgfile);
            BufferedImage buff = ImageIO.read(imgfile);
            map.put("width", buff.getWidth() * 1L);
            map.put("height", buff.getHeight() * 1L);
            map.put("s", imgfile.length());
            fis.close();
        } catch (FileNotFoundException e) {
            System.err.println("所给的图片文件" + imgfile.getPath() + "不存在！计算图片尺寸大小信息失败！");
            map = null;
        } catch (IOException e) {
            System.err.println("计算图片" + imgfile.getPath() + "尺寸大小信息失败！");
            map = null;
        }
        return map;
    }
    private static void login() {
        Spark.post("/api/login",(request, response) -> {
            response.header("Content-Type", "application/json");
            responseJson rj=new responseJson();
            String account=request.queryParams("account");
            String password=request.queryParams("password");
            DBAccess dbAccess=new DBAccess();
            dbAccess.init();
            if (!dbAccess.queryIsExist(account)){
                rj.setDetail("account not found");
                rj.setStatus("fail");
            }else {
                if (!dbAccess.queryPasswd(account).equals(password)){
                    rj.setDetail("password error");
                    rj.setStatus("fail");
                }else {
                    rj.setDetail("login!");
                    rj.setStatus("success");
                }
            }
            return rj.toString();
        });
    }

    private static void getRecord_average() {
        Spark.get("/api/getAverage/:account",(request, response) -> {
            response.header("Content-Type", "application/json");
            responseJson rj=new responseJson();
            String account=request.params("account");
            DBAccess dbAccess=new DBAccess();
            dbAccess.init();
           int average= dbAccess.queryMessage(account);
            rj.setStatus("success");
            rj.setDetail(String.valueOf(average));
           return rj.toString();
        });
    }

//    private static void getUAvatar() {
//        Spark.post("/api/avatar",(request, response) -> {
//            String base=request.queryParams("base");
//            File file=new File("//home//img//get.jpg");
//            if (!file.exists()){
//                file.createNewFile();
//            }
//            responseJson rj=new responseJson();
//            if (base64ToImage(base,file.getPath())){
//                rj.setDetail("true");
//                rj.setStatus("success");
//            }else {
//                rj.setDetail("false");
//                rj.setStatus("fail");
//            }
//            response.header("Content-Type", "application/json");
//            return rj.toString();
//        });
//    }


    private static void updateUserlevel() {
        Spark.post("/api/updateUerlevel",(request, response) -> {
            String userlevel=request.queryParams("userlevel");
            String account=request.queryParams("account");
            DBAccess db=new DBAccess();
            db.init();
            responseJson rj=new responseJson();
            if (userlevel==null||account==null){
                rj.setDetail("msg can not be null");
                rj.setStatus("fail");
            }else {
                if (db.queryIsExist(account)) {
                    db.updateUserlevel(userlevel, account);
                    rj.setStatus("success");
                    rj.setDetail("update!");
                } else {
                    rj.setDetail("not found account");
                    rj.setStatus("fail");
                }
            }
            response.header("Content-Type", "application/json");
            return rj.toString();
        });

    }

    private static void updateHotelname() {
        Spark.post("/api/updateHotelname",(request, response) -> {
            String hotelname=request.queryParams("hotelname");
            String account=request.queryParams("account");
            DBAccess db=new DBAccess();
            db.init();
            responseJson rj=new responseJson();
            if (hotelname==null||account==null){
                rj.setDetail("msg can not be null");
                rj.setStatus("fail");
            }else {
                if (db.queryIsExist(account)) {
                    db.updateHotelname(hotelname, account);
                    rj.setStatus("success");
                    rj.setDetail("update!");
                } else {
                    rj.setDetail("not found account");
                    rj.setStatus("fail");
                }
            }
            response.header("Content-Type", "application/json");
            return rj.toString();
        });

    }

    private static void updateEmail() {
        Spark.post("/api/updateEmail",(request, response) -> {
            String email=request.queryParams("email");
            String account=request.queryParams("account");
            DBAccess db=new DBAccess();
            db.init();
            responseJson rj=new responseJson();
            if (email==null||account==null){
                rj.setDetail("msg can not be null");
                rj.setStatus("fail");
            }else {
                if (db.queryIsExist(account)) {
                    db.updateEmail(email, account);
                    rj.setStatus("success");
                    rj.setDetail("update!");
                } else {
                    rj.setDetail("not found account");
                    rj.setStatus("fail");
                }
            }
            response.header("Content-Type", "application/json");
            return rj.toString();
        });
    }

    private static void updateTel()
    {

        Spark.post("/api/updateTel",(request, response) -> {
            String tel=request.queryParams("tel");
            String account=request.queryParams("account");
            DBAccess db=new DBAccess();
            db.init();
            responseJson rj=new responseJson();
            if (tel==null||account==null){
                rj.setDetail("msg can not be null");
                rj.setStatus("fail");
            }else {
                if (db.queryIsExist(account)) {
                    db.updateTel(tel, account);
                    rj.setStatus("success");
                    rj.setDetail("update!");
                } else {
                    rj.setDetail("not found account");
                    rj.setStatus("fail");
                }
            }
            response.header("Content-Type", "application/json");
            return rj.toString();
        });
    }

    private static void updateRealname() {
        Spark.post("/api/updateRealname",(request, response) -> {
            String realname=request.queryParams("realname");
            String account=request.queryParams("account");
            DBAccess db=new DBAccess();
            db.init();
            responseJson rj=new responseJson();
            if (realname==null||account==null){
                rj.setDetail("msg can not be null");
                rj.setStatus("fail");
            }else {
                if (db.queryIsExist(account)) {
                    db.updateRealname(realname, account);
                    rj.setStatus("success");
                    rj.setDetail("update!");
                } else {
                    rj.setDetail("not found account");
                    rj.setStatus("fail");
                }
            }
            response.header("Content-Type", "application/json");
            return rj.toString();
        });
    }
static  String vip="123456789";
    private static void compare() {
        Spark.post("/api/compare",(request, response) -> {
            String account=request.queryParams("account");
            String record=request.queryParams("record");
            String t="true";
            responseJson rj=new responseJson();
            DBAccess dbAccess=new DBAccess();
            dbAccess.init();
//            File file1=new File("//home//zonion//upgrade//"+account+"_own.jpg");
//            File file2=new File("//home//zonion//upgrade//"+account+"_person.jpg");
            File file1=new File("//home//zonion//upgrade//compare//"+account+"_own.jpg");
            File file2=new File("//home//zonion//upgrade//compare//"+account+"_person.jpg");
            int confidence=compareFace(file1,file2);
            if (confidence==1){
                    rj.setDetail("token create fail");
                    rj.setStatus("fail");
                }else if (confidence==0){
                    rj.setDetail("confidence too low");
                    rj.setStatus("fail");
                }else if (confidence==500){
                    rj.setDetail("file not exist");
                    rj.setStatus("fail");
                }else {
                    if (record.equals(t)){
                        if (Integer.parseInt(dbAccess.queryRemain(account))==0){
                            rj.setStatus("fail");
                            rj.setDetail("remain is 0");
                            String level="warning";
                            dbAccess.record(account,level,"(未比较)");
                        }else {
                            String level="info";
                            String c=String.valueOf(confidence);
                            rj.setDetail(c);
                            rj.setStatus("success");
                            dbAccess.record(account,level,c);
                        }
                    }else {
                        rj.setDetail(String.valueOf(confidence));
                        rj.setStatus("success but not record");
                    }
                }

            response.header("Content-Type", "application/json");
            System.out.println("finish");
            return rj.toString();
        });
    }

    private static int compareFace(File file1, File file2) throws Exception {
        if (!isFileExist01(file1)|!isFileExist01(file2)){
            return 500;
        }
        byte[] result1= commonOperate.detectFile(file1);
        String token1= resultGetFaceToken(result1);
        if (token1==null){
            return 1;
        }
        byte[] result2=commonOperate.detectFile(file2);
        String token2= resultGetFaceToken(result2);
        if (token2==null){
            return 1;
        }
        byte[] compareResult=commonOperate.compare(token1,token2);
        String s=new String(compareResult);
        JSONObject jsonObject=new JSONObject(s);
        JSONObject levelObject=jsonObject.getJSONObject("thresholds");
        int le_3=levelObject.getInt("1e-3");
        int le_4=levelObject.getInt("1e-3");
        int le_5=levelObject.getInt("1e-3");
        int confidence= (int) jsonObject.getDouble("confidence");
        if (confidence<le_3){
            return 0;
        }else {
            return confidence;
        }
    }
    private static String resultGetFaceToken(byte[] result) throws JSONException {
        String s=new String(result);
        JSONObject jsonObject = new JSONObject(s);
        JSONArray jsonArray = (JSONArray) jsonObject.get("faces");
        JSONObject object = jsonArray.optJSONObject(0);
        if (object==null) {
            return null;
        }else {
            String str = object.getString("face_token");
            return str;
        }
    }
    private static boolean isFileExist01(File file) {
        if (file.exists()){
            return true;
        }else {
            return false;
        }
    }


//public byte[] loadImg(File file){
//    byte[] data=null;
//    FileInputStream fin=null;
//    ByteArrayOutputStream bout=null;
//    try{
//             fin=new FileInputStream(file);
//             bout=new ByteArrayOutputStream((int)file.length);
//             byte[] buffer=new byte[1024];
//             int len=-1;
//             while((len=fin.read(buffer))!=-1){
//                 bout.write(buffer,0,len);
//             }
//        data=bout.toByteArray();
//                //关闭输入输出流
//             fin.close();
//                 bout.close();
//         }catch(Exception e){
//                 e.printStackTrace();
//             }
//         return data;
//}


    //压缩图片
public static String zipImageFile(String oldFile, int width, int height, float quality, String smallIcon) {
    if (oldFile == null) {
        return null;
    }
    String newImage = null;
    try {
        Image srcFile = ImageIO.read(new File(oldFile));
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
        String filePrex = oldFile.substring(0, oldFile.indexOf('.'));
        newImage = filePrex + smallIcon + oldFile.substring(filePrex.length());
        FileOutputStream out = new FileOutputStream(newImage);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
        jep.setQuality(quality, true);
        encoder.encode(tag, jep);
        out.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return newImage;
}

    public static String writeFile(String fileName, InputStream is) {
        if (fileName == null || fileName.trim().length() == 0) {
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            byte[] readBytes = new byte[512];// 缓冲大小
            int readed = 0;
            while ((readed = is.read(readBytes)) > 0) {
                fos.write(readBytes, 0, readed);
            }
            fos.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }
    public static void saveMinPhoto(String srcURL, String deskURL, double comBase,
                                    double scale) throws Exception {
        File srcFile = new java.io.File(srcURL);
        Image src = ImageIO.read(srcFile);
        int srcHeight = src.getHeight(null);
        int srcWidth = src.getWidth(null);
        int deskHeight = 0;// 缩略图高
        int deskWidth = 0;// 缩略图宽
        double srcScale = (double) srcHeight / srcWidth;
        /**缩略图宽高算法*/
        if ((double) srcHeight > comBase || (double) srcWidth > comBase) {
            if (srcScale >= scale || 1 / srcScale > scale) {
                if (srcScale >= scale) {
                    deskHeight = (int) comBase;
                    deskWidth = srcWidth * deskHeight / srcHeight;
                } else {
                    deskWidth = (int) comBase;
                    deskHeight = srcHeight * deskWidth / srcWidth;
                }
            } else {
                if ((double) srcHeight > comBase) {
                    deskHeight = (int) comBase;
                    deskWidth = srcWidth * deskHeight / srcHeight;
                } else {
                    deskWidth = (int) comBase;
                    deskHeight = srcHeight * deskWidth / srcWidth;
                }
            }
        } else {
            deskHeight = srcHeight;
            deskWidth = srcWidth;
        }
        BufferedImage tag = new BufferedImage(deskWidth, deskHeight, BufferedImage.TYPE_3BYTE_BGR);
        tag.getGraphics().drawImage(src, 0, 0, deskWidth, deskHeight, null); //绘制缩小后的图
        FileOutputStream deskImage = new FileOutputStream(deskURL); //输出到文件流
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(deskImage);
        encoder.encode(tag); //近JPEG编码
        deskImage.close();
    }


//
    private static void getAvatar() throws IOException {
       Spark.get("/api/downloadAvatar/:account",(request, response) -> {
//           File file=new File("//home//zonion//avatar//"+account+".jpg");
//           zipImageFile(file.getAbsolutePath(),1280, 1280, 1f, "x2");

           responseJson responseJson=new responseJson();
           String account=request.params("account");
           File file=new File("//home//zonion//upgrade//avatar//"+account+"_head.jpg");
           if (!file.exists()){
               File nullFile=new File("//home//zonion//upgrade//avatar//null.jpg");
               String base=GetImageStr(nullFile.getPath());
               return "data:image/jpg;base64,"+base.replace("\n","");
           }else {
               String base=GetImageStr(file.getPath());
               return "data:image/jpg;base64,"+base.replace("\n","");
           }
       });
    }
    public static boolean base64ToImage(String base64, String path) {// 对字节数组字符串进行Base64解码并生成图片
        if (base64 == null){ // 图像数据为空
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(base64);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(path);
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    public static String GetImageStr(String imgFilePath) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data).trim();// 返回Base64编码过的字节数组字符串
    }
    private static void getUMsg() {
        Spark.get("/api/getUMsg/:account",(request, response) -> {
            response.header("Content-Type", "application/json");
            String token=request.params("account");
            responseJson rj=new responseJson();
            DBAccess db=new DBAccess();
            db.init();
           UserMsg userMsg=db.queryUMsg(token);
           rj.setDetail(userMsg.toString());
           rj.setStatus("success");
           return rj.toJsonString();
        });

    }
    private static void getRecord() {
        Spark.get("/api/getRecord/:account",(request, response) -> {
            response.header("Content-Type", "application/json");
            String account=request.params("account");
            responseJson rj=new responseJson();
            DBAccess db=new DBAccess();
            db.init();
            if (db.queryIsExist(account)){
                String Account;
                if (account.endsWith(".com")){
                     Account=db.queryAccount(account);
                }else {
                    Account=account;
                }
                List<recordModel> recordList=db.qureyRecord(Account);
                String jsonstr="[";
                String json = null;
                for(int i=0;i<recordList.size();i++){
                    json=recordList.get(i).toString();
                    if(i<recordList.size()-1){jsonstr=jsonstr+json+",";}
                    if(i==recordList.size()-1){jsonstr=jsonstr+json;}
                }
                jsonstr=jsonstr+"]";
                rj.setDetail(jsonstr);
                rj.setStatus("success");
                return  rj.toJsonString();
            }else {
                rj.setStatus("fail");
                rj.setDetail("user not found");
                return  rj.toString();
            }

        });
    }

    private static void updateRemain() {
        Spark.post("/api/updateRemain",(request, response) -> {
            String remain=request.queryParams("remain");
            String account=request.queryParams("account");
            DBAccess db=new DBAccess();
            db.init();
            responseJson rj=new responseJson();
            if (remain==null||account==null){
                rj.setDetail("msg can not be null");
                rj.setStatus("fail");
            }else {
                if (db.queryIsExist(account)) {
                    db.updateRemain(remain, account);
                    rj.setStatus("success");
                    rj.setDetail("update!");
                } else {
                    rj.setDetail("not found user");
                    rj.setStatus("fail");
                }
            }
            response.header("Content-Type", "application/json");
            return rj.toString();
        });
    }

    private static void updateAvatar() {
        Spark.post("/api/updateAvatar",(request, response) -> {
            String avatar=request.queryParams("avatar");
            String account=request.queryParams("account");
            Blob blob=new SerialBlob(avatar.getBytes());
//            ByteArrayOutputStream bt = new ByteArrayOutputStream();
//            ObjectOutputStream ob;
//            try {
//                ob= new ObjectOutputStream(bt );
//                ob.writeObject(avatar);
//            } catch (IOException e) {
//
//                e.printStackTrace();
//
//            }
//            Blob blob=Hibernate.createBlob(bt.toByteArray());
            DBAccess db=new DBAccess();
            db.init();
            responseJson rj=new responseJson();
            db.updateAvatar(blob, account);
//            File file1=new File("//home//zonion//upgrade//avatar.jpg");
//            if (!file1.exists()){
//                file1.createNewFile();
//            }
            rj.setStatus("success");
            rj.setDetail("update!");
            response.header("Content-Type", "application/json");
            return rj.toString();
        });

    }

    private static void updateLocation() {
        Spark.post("/api/updateLocation",(request, response) -> {
            String location=request.queryParams("location");
            String account=request.queryParams("account");
            DBAccess db=new DBAccess();
            db.init();
            responseJson rj=new responseJson();
            if (location==null||account==null){
                rj.setDetail("msg account not be null");
                rj.setStatus("fail");
            }else {
                if (db.queryIsExist(account)){
                    db.updateLocation(location,account);
                    rj.setStatus("success");
                    rj.setDetail("update!");
                }else {
                    rj.setDetail("not found user");
                    rj.setStatus("fail");
                }
            }
            response.header("Content-Type", "application/json");
            return rj.toString();
        });
    }

    private static void updatePassword() {
        Spark.post("/api/updatePassword",(request, response) -> {
            String password=request.queryParams("password");
            String account=request.queryParams("account");
            DBAccess db=new DBAccess();
            db.init();
            responseJson rj=new responseJson();
            if (password==null||account==null){
                rj.setDetail("msg can not be null");
                rj.setStatus("fail");
            }else {
                if (db.queryIsExist(account)) {
                    db.updatePassord(password, account);
                    rj.setStatus("success");
                    rj.setDetail("update!");
                } else {
                    rj.setDetail("not found user");
                    rj.setStatus("fail");
                }
            }
            response.header("Content-Type", "application/json");
            return rj.toString();
        });
    }

    private static void updateName() {
        Spark.post("/api/updateName",(request, response) -> {
            String account=request.queryParams("account");
            String user=request.queryParams("user");
            DBAccess db=new DBAccess();
            db.init();
            responseJson rj=new responseJson();
            if (account==null||user==null){
                rj.setDetail("msg can not be null");
                rj.setStatus("fail");
            }else {
                if (db.queryIsExist(account)) {
                    db.updateName(account, user);
                    rj.setStatus("success");
                    rj.setDetail("update!");
                } else {
                    rj.setDetail("not found user");
                    rj.setStatus("fail");
                }
            }
            response.header("Content-Type", "application/json");
            return rj.toString();
        });
    }

    private static void uploadRecord() {
        Spark.post("/api/uploadRecord",(request, response) -> {
            responseJson rj=new responseJson();
            String account=request.queryParams("account");
            DBAccess db=new DBAccess();
            db.init();
            if (db.queryIsExist(account)){
                String level="info";
                db.record(account,level,"0");
                  rj.setDetail("record success");
                  rj.setStatus("success");
            }else {
                rj.setDetail("user not found");
                rj.setStatus("fail");
            }
            return  rj.toString();
        });

    }

    private static void updateMsg() {
        Spark.post("/api/updateUMsg",(request, response) -> {
            String user=request.queryParams("user");
            String account=request.queryParams("account");
            String realname=request.queryParams("realname");
            String tel=request.queryParams("tel");
            String email=request.queryParams("email");
            String hotelname=request.queryParams("hotelname");
            String location=request.queryParams("location");
            String sex=request.queryParams("sex");
            DBAccess db=new DBAccess();
            db.init();
            boolean isExist=db.queryIsExist(account);
            responseJson rj=new responseJson();
            if (isExist){
                db.updateUMsg(account,user,realname,tel,email,hotelname,location,sex);
                rj.setStatus("success");
                rj.setDetail("update!");
            }else {
                rj.setStatus("fail");
                rj.setDetail("user not found");
            }
            response.header("Content-Type", "application/json");
            System.out.println("update success");
            return  rj.toString();
        });
    }

    private static void register() {
        Spark.post("/api/register",(request, response) -> {
            responseJson rj=new responseJson();
            response.header("Content-Type", "application/json");
            String user=request.queryParams("user");
            String password=request.queryParams("password");
            String email=request.queryParams("email");
            DBAccess db=new DBAccess();
            db.init();
            if (db.queryEmail(email)){
                rj.setStatus("fail");
                rj.setDetail("email is exist");
            }else {
                String account = createAccount();
                while (db.queryIsExist(account)) {
                    account = createAccount();
                }
                db.registerUser_BasicMsg(user, password, email, account);
                rj.setDetail(account);
                rj.setStatus("success");
            }
            return rj.toString();
        });
    }
    public static String createAccount(){
        int[] array = {0,1,2,3,4,5,6,7,8,9};
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for(int i = 0; i < 6; i++) {
            result = result * 10 + array[i];
        }
        if (String.valueOf(result).length()<6){
            int i=6-String.valueOf(result).length();
            for (int z=0;z<i;z++){
                result = result * 10 + array[10-i];
            }
        }
        return String.valueOf(result);
    }


}
