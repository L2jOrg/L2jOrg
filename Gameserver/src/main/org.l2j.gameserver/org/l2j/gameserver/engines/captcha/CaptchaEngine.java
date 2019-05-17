package org.l2j.gameserver.engines.captcha;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.commons.util.Rnd;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CaptchaEngine {

    private static final IntObjectMap<Captcha> captchas = new HashIntObjectMap<>();
    private static final DXT1ImageCompressor compressor = new DXT1ImageCompressor();

    private CaptchaEngine() {
        //
    }

    public Captcha next() {
        var code = generateCaptchaCode();
        return captchas.computeIfAbsent(code, this::generateCaptcha);
    }

    private int generateCaptchaCode() {
        return Rnd.get(111111, 999999);
    }

    private Captcha generateCaptcha(int code) {
        var height = 32;
        var width  = 128;

        // http://msdn.microsoft.com/en-us/library/bb694531(VS.85).aspx
        var image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D graphics = createGraphics(height, width, image);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        writeCode(code, graphics);
        addNoise(graphics);
        graphics.dispose();
        return new Captcha(code, compressor.compress(image));
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
            var charWidth = metrics.charWidth(character) + 5;
            graphics.setColor(getColor());
            graphics.drawString(""+character, textStart + (i * charWidth),  Rnd.get(22, 32));
        }
    }

    private void addNoise(Graphics2D graphics) {
        for (int i = 0; i < 12; i++) {
            graphics.setColor(getColor());
            graphics.fillOval(Rnd.get(10, 122), Rnd.get(6, 20), 5, 5);
        }

        for (int i = 0; i < 5; i++) {
            graphics.setColor(getColor());
            graphics.drawLine(Rnd.get(30, 90), Rnd.get(6, 28),  Rnd.get(80, 120), Rnd.get(10, 26));
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
