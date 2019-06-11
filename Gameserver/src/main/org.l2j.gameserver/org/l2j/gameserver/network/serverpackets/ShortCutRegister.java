package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class ShortCutRegister extends IClientOutgoingPacket {
    private final Shortcut _shortcut;

    /**
     * Register new skill shortcut
     *
     * @param shortcut
     */
    public ShortCutRegister(Shortcut shortcut) {
        _shortcut = shortcut;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.SHORT_CUT_REGISTER);

        writeInt(_shortcut.getType().ordinal());
        writeInt(_shortcut.getSlot() + (_shortcut.getPage() * 12)); // C4 Client
        writeByte((byte) 0x00); // 196
        switch (_shortcut.getType()) {
            case ITEM: {
                writeInt(_shortcut.getId());
                writeInt(_shortcut.getCharacterType());
                writeInt(_shortcut.getSharedReuseGroup());
                writeInt(0x00); // Remaining time
                writeInt(0x00); // Cool down time
                writeInt(0x00); // item augment effect 1
                writeInt(0x00); // item augment effect 2
                writeInt(0x00); // unk
                break;
            }
            case SKILL: {
                writeInt(_shortcut.getId());
                writeShort((short) _shortcut.getLevel());
                writeShort((short) _shortcut.getSubLevel());
                writeInt(_shortcut.getSharedReuseGroup());
                writeByte((byte) 0x00); // C5
                writeInt(_shortcut.getCharacterType());
                writeInt(0x00); // TODO: Find me
                writeInt(0x00); // TODO: Find me
                break;
            }
            case ACTION:
            case MACRO:
            case RECIPE:
            case BOOKMARK: {
                writeInt(_shortcut.getId());
                writeInt(_shortcut.getCharacterType());
            }
        }
    }

}
