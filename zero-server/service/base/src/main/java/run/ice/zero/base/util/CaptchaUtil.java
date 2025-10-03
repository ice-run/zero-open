package run.ice.zero.base.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author DaoDao
 */
@Slf4j
public class CaptchaUtil {

    private CaptchaUtil() {
    }

    private static final int WIDTH = 120;
    private static final int HEIGHT = 45;
    private static final int LENGTH = 4;
    private static final String EX_CHARS = "10ioIOLl";

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成随机验证码
     */
    public static String getRandomCode() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < LENGTH) {
            int t = RANDOM.nextInt(123);
            if ((t >= 97 || t >= 65 && t <= 90 || t >= 48 && t <= 57) && EX_CHARS.indexOf((char) t) < 0) {
                sb.append((char) t);
                i++;
            }
        }
        return sb.toString();
    }

    /**
     * 生成验证码图片
     *
     * @param randomCode 验证码
     */
    public static BufferedImage genCaptcha(String randomCode) {
        // System.setProperty("java.awt.headless","true");
        // 创建画布
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        g.setColor(getRandColor(100, 180));
        for (int i = 0; i < 30; i++) {
            int x = RANDOM.nextInt(WIDTH - 1);
            int y = RANDOM.nextInt(HEIGHT - 1);
            int xl = RANDOM.nextInt(WIDTH / 2);
            int yl = RANDOM.nextInt(WIDTH / 2);
            g.drawLine(x, y, x + xl, y + yl + 20);
        }

        // 添加噪点
        float rate = 0.1f;
        int area = (int) (rate * WIDTH * HEIGHT);
        for (int i = 0; i < area; i++) {
            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);
            image.setRGB(x, y, getRandColor(100, 200).getRGB());
        }

        // 绘制验证码
        int size = HEIGHT - 4;
        // Font font = new Font(Font.SANS_SERIF, Font.PLAIN, size);
        // Font font = new Font(null, Font.PLAIN, size);
        Font font;
        try {
            font = loadFont(size);
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
        g.setFont(font);
        char[] chars = randomCode.toCharArray();
        for (int i = 0; i < randomCode.length(); i++) {
            g.drawChars(chars, i, 1, ((WIDTH - 10) / randomCode.length()) * i + 5, HEIGHT / 2 + size / 2 - 6);
        }

        g.dispose();
        return image;
    }

    /**
     * 获取相应范围的随机颜色
     *
     * @param min 最小值
     * @param max 最大值
     */
    private static Color getRandColor(int min, int max) {
        min = Math.min(min, 255);
        max = Math.min(max, 255);
        int r = min + RANDOM.nextInt(max - min);
        int g = min + RANDOM.nextInt(max - min);
        int b = min + RANDOM.nextInt(max - min);
        return new Color(r, g, b);
    }

    public static Font loadFont(int size) throws IOException, FontFormatException {
        try (InputStream fontStream = CaptchaUtil.class.getResourceAsStream("/fonts/DejaVuSansMono.ttf")) {
            if (fontStream != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                return font.deriveFont((float) size);
            } else {
                log.warn("自定义字体文件不存在，使用系统默认字体");
                return new Font(Font.SANS_SERIF, Font.PLAIN, size);
            }
        } catch (IOException | FontFormatException e) {
            log.error("加载字体出错: {}", e.getMessage(), e);
            return new Font(Font.SANS_SERIF, Font.PLAIN, size);
        }
    }

    void test() {
        String code = getRandomCode();
        String img;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(CaptchaUtil.genCaptcha(code), "jpg", outputStream);
            img = "data:image/jpeg;base64," + new String(Base64.getEncoder().encode(outputStream.toByteArray())).replaceAll("[\\s*\t\n\r]", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
