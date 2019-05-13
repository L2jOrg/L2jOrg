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

    @Override
    protected int size(L2GameClient client) {
        return _parts.stream().mapToInt(p ->
            switch (p.b | 32) {
                case 'c'-> 1;
                case 'h'-> 2;
                case 'd'-> 4;
                case 'f', 'q' -> 8;
                case 's' -> p.str.length();
                case 'b', 'x' -> new BigInteger(p.str).toByteArray().length;
                default -> 0;
            }).sum();
    }

    public boolean generate(ByteBuffer packet, byte type, String value) {
        return switch (type | 32) {
            case 'c' -> {
                packet.put(Integer.decode(value).byteValue());
                break true;
            }
            case 'd' -> {
                packet.putInt(Integer.decode(value));
                break true;
            }
            case 'h' -> {
                packet.putShort(Integer.decode(value).shortValue());
                break true;
            }
            case 'f' -> {
                packet.putDouble(Double.parseDouble(value));
                break true;
            }
            case 's' -> {
                writeString(value, packet);
                break true;
            }
            case 'b', 'x' -> {
                packet.put(new BigInteger(value).toByteArray());
                break  true;
            }
            case 'q' -> {
                packet.putLong(Long.decode(value));
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