package org.l2j.gameserver.engine.captcha;

/**
 * @author JoeAlisson
 */
public class Captcha {
    private final int code;
    private final byte[] data;
    private final int id;

    Captcha(int id, int code, byte[] data) {
        this.id = id;
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public byte[] getData() {
        return data;
    }

    public int getId() {
        return id;
    }
}
