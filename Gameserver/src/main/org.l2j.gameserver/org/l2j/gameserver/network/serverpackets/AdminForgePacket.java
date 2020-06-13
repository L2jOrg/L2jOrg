/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;

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
    public void writeImpl(GameClient client) {
        for (Part p : _parts) {
            generate(p.b, p.str);
        }
    }

    public boolean generate(byte type, String value) {
        return switch (type | 32) {
            case 'c' -> {
                writeByte(Integer.decode(value).byteValue());
                yield true;
            }
            case 'd' -> {
                writeInt(Integer.decode(value));
                yield true;
            }
            case 'h' -> {
                writeShort(Integer.decode(value).shortValue());
                yield true;
            }
            case 'f' -> {
                writeDouble(Double.parseDouble(value));
                yield true;
            }
            case 's' -> {
                writeString(value);
                yield true;
            }
            case 'b', 'x' -> {
                writeBytes(new BigInteger(value).toByteArray());
                yield true;
            }
            case 'q' -> {
                writeLong(Long.decode(value));
                yield true;
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