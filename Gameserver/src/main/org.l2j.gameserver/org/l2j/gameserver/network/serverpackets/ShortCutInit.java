package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class ShortCutInit extends IClientOutgoingPacket {
    private Shortcut[] _shortCuts;

    public ShortCutInit(L2PcInstance activeChar) {
        if (activeChar == null) {
            return;
        }

        _shortCuts = activeChar.getAllShortCuts();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SHORT_CUT_INIT.writeId(packet);

        packet.putInt(_shortCuts.length);
        for (Shortcut sc : _shortCuts) {
            packet.putInt(sc.getType().ordinal());
            packet.putInt(sc.getSlot() + (sc.getPage() * 12));

            switch (sc.getType()) {
                case ITEM: {
                    packet.putInt(sc.getId());
                    packet.putInt(0x01); // Enabled or not
                    packet.putInt(sc.getSharedReuseGroup());
                    packet.putInt(0x00);
                    packet.putInt(0x00);
                    packet.putLong(0x00); // Augment id
                    packet.putInt(0x00); // Visual id
                    break;
                }
                case SKILL: {
                    packet.putInt(sc.getId());
                    packet.putShort((short) sc.getLevel());
                    packet.putShort((short) sc.getSubLevel());
                    packet.putInt(sc.getSharedReuseGroup());
                    packet.put((byte) 0x00); // C5
                    packet.putInt(0x01); // C6
                    break;
                }
                case ACTION:
                case MACRO:
                case RECIPE:
                case BOOKMARK: {
                    packet.putInt(sc.getId());
                    packet.putInt(0x01); // C6
                }
            }
        }
    }
}
