package org.l2j.gameserver.engine.captcha;

import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.commons.util.Rnd;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author JoeAlisson
 */
public class CaptchaEngine {

    private static final IntMap<Captcha> captchas = new CHashIntMap<>();
    private static final DXT1ImageCompressor compressor = new DXT1ImageCompressor();

    private CaptchaEngine() {
    }

    public Captcha next() {
        var id = Rnd.get(captchas.size() + 5);
        return captchas.computeIfAbsent(id, this::generateCaptcha);
    }

    public Captcha next(int previousId) {
        var id = Rnd.get(captchas.size() + 5);
        if(id == previousId) {
            id++;
        }
        return captchas.computeIfAbsent(id, this::generateCaptcha);
    }

    private int generateCaptchaCode() {
        return Rnd.get(111111, 999999);
    }

    private Captcha generateCaptcha(int id) {
        var height = 32;
        var width  = 128;

        var image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D graphics = createGraphics(height, width, image);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        var code = generateCaptchaCode();

        writeCode(code, graphics);
        addNoise(graphics);
        graphics.dispose();
        return new Captcha(id, code, compressor.compress(image));
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
            graphics.drawString(""+character, textStart + (i * charWidth),  Rnd.get(24, 32));
        }
    }

    private void addNoise(Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        for (int i = 0; i < 20; i++) {
            graphics.fillOval(Rnd.get(10, 122), Rnd.get(6, 20), 4, 4);
        }

        for (int i = 0; i < 6; i++) {
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

}
