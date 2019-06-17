package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is made to create packets with any format
 *
 * @author Maktakien
 */
public class AdminForgePacket extends ServerPacket {
    private final List<Part> _parts = new ArrayList<>();

    public AdminForgePacket() {

    }

    @Override
    public void writeImpl(L2GameClient client) {
        for (Part p : _parts) {
            generate(p.b, p.str);
        }
    }

    public boolean generate(byte type, String value) {
        return switch (type | 32) {
            case 'c' -> {
                writeByte(Integer.decode(value).byteValue());
                break true;
            }
            case 'd' -> {
                writeInt(Integer.decode(value));
                break true;
            }
            case 'h' -> {
                writeShort(Integer.decode(value).shortValue());
                break true;
            }
            case 'f' -> {
                writeDouble(Double.parseDouble(value));
                break true;
            }
            case 's' -> {
                writeString(value);
                break true;
            }
            case 'b', 'x' -> {
                writeBytes(new BigInteger(value).toByteArray());
                break  true;
            }
            case 'q' -> {
                writeLong(Long.decode(value));
                break true;
            }
            default -> false;
        };
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