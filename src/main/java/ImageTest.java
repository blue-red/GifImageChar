import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author jack.wang 2018-09-19
 **/
public class ImageTest {

    //static String ascii = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\\\"^`'.";
    static String ascii = ";jkBJL; BbJjg ";

    public static char toChar(int g) {
        if (g <= 30) {
            return '#';
        } else if (g > 30 && g <= 60) {
            return '&';
        } else if (g > 60 && g <= 120) {
            return '$';
        } else if (g > 120 && g <= 150) {
            return '*';
        } else if (g > 150 && g <= 180) {
            return 'o';
        } else if (g > 180 && g <= 210) {
            return '!';
        } else if (g > 210 && g <= 240) {
            return ';';
        } else {
            return ' ';
        }
    }

    public static void load(String imagePath, String txtPath) throws IOException {
        BufferedImage bi = null;
        File imageFile = new File(imagePath);
        bi = ImageIO.read(imageFile);
        load(bi, txtPath);
    }

    public static void load(BufferedImage bi, String txtPath) throws IOException {
        File txtFile = new File(txtPath);
        if (!txtFile.exists()) {
            txtFile.getParentFile().mkdirs();
            txtFile.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtFile)));
        int width = bi.getWidth();
        int height = bi.getHeight();
        int minx = bi.getMinX();
        int miny = bi.getMinY();
        System.out.println(width + " " + height);
        for (int i = miny; i < height; i += 8) {
            for (int j = minx; j < width; j += 8) {
                int pixel = bi.getRGB(j, i); // 下面三行代码将一个数字转换为RGB数字
                int red = (pixel & 0xff0000) >> 16;
                int green = (pixel & 0xff00) >> 8;
                int blue = (pixel & 0xff);
                double gray = 0.299 * red + 0.578 * green + 0.114 * blue;
                char c = ascii.charAt((int) (gray / 255 * ascii.length()));
                //char c = toChar((int) gray);
                bufferedWriter.write(c);
            }
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static void loadGif(String imagePath, String outPath) throws IOException {
        File imageFile = new File(imagePath);
        //FileImageInputStream in = new FileImageInputStream(imageFile);
        FileInputStream in = new FileInputStream(imagePath);

        GifDecoder.GifImage gifImage = GifDecoder.read(in);
        int num = gifImage.getFrameCount();
        System.out.println(num);
        BufferedImage[] bufferedImages = new BufferedImage[num];
        for (int i = 0; i < num; i++) {
            BufferedImage bi = gifImage.getFrame(i);
            bufferedImages[i] = txtToImage(bi, outPath + "out" + i + ".jpeg");
        }
        jpgToGif(bufferedImages, outPath + imagePath.substring(imagePath.length() - 6) + "outGif.gif", 100);
    }
    public static BufferedImage txtToImage(BufferedImage bi, String outPutPath) {
        File imageFile = new File(outPutPath);
        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int width = bi.getWidth();
        int height = bi.getHeight();
        int minx = bi.getMinX();
        int miny = bi.getMinY();
        System.out.println(width + " " + height);
        int speed = 7;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取图像上下文
        Graphics g = createGraphics(bufferedImage, width, height, speed);
        // 图片中文本行高
        final int Y_LINEHEIGHT = speed;
        int lineNum = 1;
        for (int i = miny; i < height; i += speed) {
            // StringBuilder stringBuilder = new StringBuilder();
            for (int j = minx; j < width; j += speed) {
                int pixel = bi.getRGB(j, i); // 下面三行代码将一个数字转换为RGB数字
                int red = (pixel & 0xff0000) >> 16;
                int green = (pixel & 0xff00) >> 8;
                int blue = (pixel & 0xff);
                double gray = 0.299 * red + 0.578 * green + 0.114 * blue;
                char c = ascii.charAt((int) (gray / 255 * ascii.length()));
                //char c = toChar((int) gray);
                // stringBuilder.append(c);
                g.drawString(String.valueOf(c), j, i);
            }
            // g.drawString(stringBuilder.toString(), 0, lineNum * Y_LINEHEIGHT);
            lineNum++;
        }
        g.dispose();
        // 保存为jpg图片
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imageFile);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
            encoder.encode(bufferedImage);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ImageFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;

    }

    private static void jpgToGif(BufferedImage[] bufferedImages, String newPic, int playTime) {
        try {
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.setRepeat(0);
            e.start(newPic);
            for (int i = 0; i < bufferedImages.length; i++) {
                e.setDelay(playTime); // 设置播放的延迟时间
                e.addFrame(bufferedImages[i]); // 添加到帧中
            }
            e.finish();
        } catch (Exception e) {
            System.out.println("jpgToGif Failed:");
            e.printStackTrace();
        }
    }

    private static Graphics createGraphics(BufferedImage image, int width, int height, int size) {
        Graphics g = image.createGraphics();
        g.setColor(null); // 设置背景色
        g.fillRect(0, 0, width, height);// 绘制背景
        g.setColor(Color.BLACK); // 设置前景色
        g.setFont(new Font("微软雅黑", Font.PLAIN, size)); // 设置字体
        return g;
    }


    public static void main(String[] args) throws IOException {
        loadGif("E:\\Demo\\test1.gif", "E:/Demo/");
    }
}
