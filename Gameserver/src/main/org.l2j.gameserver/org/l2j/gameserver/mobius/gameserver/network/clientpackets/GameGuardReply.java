package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Format: c dddd
 *
 * @author KenM
 */
public class GameGuardReply extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameGuardReply.class);
    private static final byte[] VALID =
            {
                    (byte) 0x88,
                    0x40,
                    0x1c,
                    (byte) 0xa7,
                    (byte) 0x83,
                    0x42,
                    (byte) 0xe9,
                    0x15,
                    (byte) 0xde,
                    (byte) 0xc3,
                    0x68,
                    (byte) 0xf6,
                    0x2d,
                    0x23,
                    (byte) 0xf1,
                    0x3f,
                    (byte) 0xee,
                    0x68,
                    0x5b,
                    (byte) 0xc5,
            };

    private final byte[] _reply = new byte[8];

    @Override
    public void readImpl(ByteBuffer packet) {
        packet.get(_reply, 0, 4);
        packet.getInt();
        packet.get(_reply, 4, 4);
    }

    @Override
    public void runImpl() {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA");
            final byte[] result = md.digest(_reply);
            if (Arrays.equals(result, VALID)) {
                client.setGameGuardOk(true);
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e.getLocalizedMessage(), e);
        }
    }
}
