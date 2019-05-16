package org.l2j.gameserver.engines.captcha;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.commons.util.Rnd;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CaptchaEngine {

    private static final IntObjectMap<Captcha> captchas = new HashIntObjectMap<>();

    private CaptchaEngine() {
        //
    }

    public Captcha next() {
        var code = generateCaptchaCode();
        return captchas.computeIfAbsent(code, this::generateCaptcha);
    }

    private int generateCaptchaCode() {
        return Rnd.get(121213, 987979);
    }

    private Captcha generateCaptcha(int code) {
        var height = 32;
        var width  = 128;

        // http://msdn.microsoft.com/en-us/library/bb694531(VS.85).aspx
        var image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
        Graphics2D graphics = createGraphics(height, width, image);

        writeCode(code, graphics);

        addNoise(graphics);

        graphics.dispose();
        return new Captcha(0, DXT1ImageCompressor.compress(image));
    }

    private Graphics2D createGraphics(int height, int width, BufferedImage image) {
        var graphics = image.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);
        return graphics;
    }

    private void writeCode(int code, Graphics2D graphics) {
        var text = String.valueOf(code);

        var metrics = graphics.getFontMetrics();

        var textStart = 10;
        for(var i = 0; i < text.length(); i++) {
            var character = text.charAt(i);
            var charWidth = metrics.charWidth(character);
            graphics.setColor(getColor());
            graphics.drawString(""+character, textStart + (i * charWidth),  Rnd.get(10) + 10);
        }
    }

    private void addNoise(Graphics2D graphics) {
        for (int i = 0; i < 30; i++) {
            graphics.setColor(getColor());
            graphics.drawOval(Rnd.get(128), Rnd.get(32), 6, 6);
        }

        for (int i = 0; i < 5; i++) {
            graphics.setColor(getColor());
            graphics.drawLine(20 + Rnd.get(75), 100 + Rnd.get(75) * -1, 2 + Rnd.get(15), 30 + Rnd.get(15) * -1);
        }
    }

    private Color getColor() {
        return switch (Rnd.get(5)) {
            case 1 -> Color.WHITE;
            case 2 -> Color.RED;
            case 3 -> Color.YELLOW;
            case 4 -> Color.CYAN;
            default -> Color.GREEN;
        };
    }


    public static CaptchaEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CaptchaEngine INSTANCE = new CaptchaEngine();
    }

    public static class Captcha {
        private final int code;
        private final byte[] data;

        private Captcha(int code, byte[] data) {
            this.code = code;
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public byte[] getData() {
            return data;
        }
    }

}
