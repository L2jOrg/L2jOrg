package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is made to create packets with any format
 *
 * @author Maktakien
 */
public class AdminForgePacket extends IClientOutgoingPacket {
    private final List<Part> _parts = new ArrayList<>();

    public AdminForgePacket() {

    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        for (Part p : _parts) {
            generate(packet, p.b, p.str);
        }
    }

    /**
     * @param packet
     * @param type
     * @param value
     * @return
     */
    public boolean generate(ByteBuffer packet, byte type, String value) {
        if ((type == 'C') || (type == 'c')) {
            packet.put(Byte.decode(value));
            return true;
        } else if ((type == 'D') || (type == 'd')) {
            packet.putInt(Integer.decode(value));
            return true;
        } else if ((type == 'H') || (type == 'h')) {
            packet.putShort(Short.decode(value));
            return true;
        } else if ((type == 'F') || (type == 'f')) {
            packet.putDouble(Double.parseDouble(value));
            return true;
        } else if ((type == 'S') || (type == 's')) {
            writeString(value, packet);
            return true;
        } else if ((type == 'B') || (type == 'b') || (type == 'X') || (type == 'x')) {
            packet.put(new BigInteger(value).toByteArray());
            return true;
        } else if ((type == 'Q') || (type == 'q')) {
            packet.putLong(Long.decode(value));
            return true;
        }
        return false;
    }

    public void addPart(byte b, String string) {
        _parts.add(new Part(b, string));
    }

    private static class Part {
        public byte b;
        public String str;

        public Part(byte bb, String string) {
            b = bb;
            str = string;
        }
    }
}