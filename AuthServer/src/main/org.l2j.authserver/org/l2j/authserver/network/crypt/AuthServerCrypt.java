package org.l2j.authserver.network.crypt;

import org.l2j.commons.crypt.NewCrypt;

import java.io.IOException;

import static java.util.Objects.nonNull;

public class AuthServerCrypt {

    private static final String STATIC_BLOWFISH_KEY = "_;v.]05-31!|+-%xT!^[$\00";
    private static final int CHECKSUM_SIZE = 4;
    private static final int PADDING_SIZE = 8;

    private final NewCrypt staticCrypt = new NewCrypt(STATIC_BLOWFISH_KEY);
    private NewCrypt crypt;

    public void setKey(byte[] key) {
        crypt = new NewCrypt(key);
    }

    public boolean decrypt(byte[] raw, final int offset, final int size) throws IOException {
        if(nonNull(crypt)) {
            crypt.decrypt(raw, offset, size);
        } else {
            staticCrypt.decrypt(raw, offset, size);
        }
        return NewCrypt.verifyChecksum(raw, offset, size);
    }

    public int encrypt(byte[] raw, final int offset, int size) throws IOException {
        size += CHECKSUM_SIZE;
        size += PADDING_SIZE - (size % PADDING_SIZE);
        size += PADDING_SIZE;
        NewCrypt.appendChecksum(raw, offset, size);
        if(nonNull(crypt)) {
            crypt.crypt(raw, offset, size);
        } else {
            staticCrypt.crypt(raw, offset, size);
        }
        return size;
    }
}
