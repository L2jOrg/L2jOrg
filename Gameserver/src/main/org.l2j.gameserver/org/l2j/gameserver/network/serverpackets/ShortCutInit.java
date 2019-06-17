package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class ShortCutInit extends ServerPacket {
    private Shortcut[] _shortCuts;

    public ShortCutInit(L2PcInstance activeChar) {
        if (activeChar == null) {
            return;
        }

        _shortCuts = activeChar.getAllShortCuts();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.SHORT_CUT_INIT);

        writeInt(_shortCuts.length);
        for (Shortcut sc : _shortCuts) {
            writeInt(sc.getType().ordinal());
            writeInt(sc.getSlot() + (sc.getPage() * 12));
            switch (sc.getType()) {
                case ITEM: {
                    writeInt(sc.getId());
                    writeInt(0x01); // Enabled or not
                    writeInt(sc.getSharedReuseGroup());
                    writeInt(0x00);
                    writeInt(0x00);
                    writeInt(0x00); // Augment effect 1
                    writeInt(0x00); // Augment effect 2
                    writeInt(0x00); // Visual id
                    break;
                }
                case SKILL: {
                    writeInt(sc.getId());
                    writeShort((short) sc.getLevel());
                    writeShort((short) sc.getSubLevel());
                    writeInt(sc.getSharedReuseGroup());
                    writeByte((byte) 0x00); // C5
                    writeInt(0x01); // C6
                    break;
                }
                case ACTION:
                case MACRO:
                case RECIPE:
                case BOOKMARK: {
                    writeInt(sc.getId());
                    writeInt(0x01); // C6
                }
            }
        }
    }

}
