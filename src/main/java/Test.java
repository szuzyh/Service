import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leo on 2017/1/6.
 */
public class Test {
    public static void main(String[] args) {
        File file=new File("12.jpg");
        Map<String,Long> map=getImgInfo(file.getAbsolutePath());
        int width= Math.toIntExact(map.get("w"));
        int height= Math.toIntExact(map.get("h"));
        zipImageFile(file.getAbsolutePath(),width/2, height/2, 1f, "x2");

    }

    public static Map<String, Long> getImgInfo(String imgpath) {
        Map<String, Long> map = new HashMap<String, Long>(3);
        File imgfile = new File(imgpath);
        try {
            FileInputStream fis = new FileInputStream(imgfile);
            BufferedImage buff = ImageIO.read(imgfile);
            map.put("w", buff.getWidth() * 1L);
            map.put("h", buff.getHeight() * 1L);
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
    public static String zipImageFile(String oldFile, int width, int height, float quality, String smallIcon)
    {
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
}
