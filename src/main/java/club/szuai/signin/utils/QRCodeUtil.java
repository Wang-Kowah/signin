package club.szuai.signin.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

//import com.google.zxing.client.j2se.MatrixToImageWriter;

public class QRCodeUtil {

    public static void main(String[] args) {
        generateToFile(800, 800, "https://szuai.club", "png", "C:\\Users\\Kowah\\Desktop\\QRCode.png", "C:\\Users\\Kowah\\Desktop\\PDF\\pic");
    }

    /**
     * 生成二维码并输出到流
     *
     * @param width   宽度
     * @param height  高度
     * @param content 内容
     * @param format  图片格式:jpg/png
     */
    public static void generateToStream(int width, int height, String content, String format, OutputStream stream, String logoDir) {
        HashMap<EncodeHintType, Object> hits = new HashMap<>();
        hits.put(EncodeHintType.CHARACTER_SET, "UTF-8");                    //编码
        hits.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);  //纠错等级，纠错等级越高存储信息越少
        hits.put(EncodeHintType.MARGIN, 2);                                 //边距

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hits);
            MatrixToImageWriter.writeToStream(bitMatrix, format, stream, logoDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成二维码并输出到文件
     *
     * @param width    宽度
     * @param height   高度
     * @param content  内容
     * @param format   图片格式:jpg/png
     * @param filePath 输出路径
     */
    public static void generateToFile(int width, int height, String content, String format, String filePath, String logoDir) {
        HashMap<EncodeHintType, Object> hits = new HashMap<>();
        hits.put(EncodeHintType.CHARACTER_SET, "UTF-8");                    //编码
        hits.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);  //纠错等级，纠错等级越高存储信息越少
        hits.put(EncodeHintType.MARGIN, 2);                                 //边距

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hits);
            File file = new File(filePath);
            MatrixToImageWriter.writeToFile(bitMatrix, format, file, logoDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

/**
 * 参照zxing自带的测试类来实现
 * https://github.com/zxing/zxing/blob/master/javase/src/main/java/com/google/zxing/client/j2se/MatrixToImageWriter.java
 */
class MatrixToImageWriter {

    //生成二维码
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int custom_color = 0xff37b19e;
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, (matrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB()));
//                image.setRGB(x, y, (matrix.get(x, y) ? custom_color : Color.WHITE.getRGB()));
            }
        }
        return image;
    }

    //输出到文件
    public static void writeToFile(BitMatrix matrix, String format, File file, String logoDir) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        //设置logo图标
        LogoConfig logoConfig = new LogoConfig();
        image = logoConfig.LogoMatrix(image, logoDir);

        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    //输出到流
    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream, String logoDir) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        //设置logo图标
        LogoConfig logoConfig = new LogoConfig();
        image = logoConfig.LogoMatrix(image, logoDir);

        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }
}


/**
 * 二维码添加logo,模仿微信自动生成二维码效果
 * https://blog.csdn.net/sanfye/article/details/45749139
 */
class LogoConfig {

    /**
     * 设置 logo
     *
     * @param matrixImage 源二维码图片
     * @return 返回带有logo的二维码图片
     */
    public BufferedImage LogoMatrix(BufferedImage matrixImage, String logoDir) throws IOException {
        //读取二维码图片，并构建绘图对象
        Graphics2D g2 = matrixImage.createGraphics();
        int matrixWidth = matrixImage.getWidth();
        int matrixHeigh = matrixImage.getHeight();

        //读取Logo图片
        BufferedImage logo = ImageIO.read(new File(logoDir + File.separator + "wx.jpg"));

        //开始绘制图片
        g2.drawImage(logo, matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, null);//绘制
        BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);// 设置笔画对象
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, 20, 20);
        g2.setColor(Color.white);
        g2.draw(round);// 绘制圆弧矩形

        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);// 设置笔画对象
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth / 5 * 2 + 2, matrixHeigh / 5 * 2 + 2, matrixWidth / 5 - 4, matrixHeigh / 5 - 4, 20, 20);
        g2.setColor(new Color(128, 128, 128));
        g2.draw(round2);// 绘制圆弧矩形

        g2.dispose();
        matrixImage.flush();
        return matrixImage;
    }

}
