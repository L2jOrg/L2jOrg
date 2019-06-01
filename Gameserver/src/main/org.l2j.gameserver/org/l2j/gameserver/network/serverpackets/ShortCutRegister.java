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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SHORT_CUT_REGISTER.writeId(packet);

        packet.putInt(_shortcut.getType().ordinal());
        packet.putInt(_shortcut.getSlot() + (_shortcut.getPage() * 12)); // C4 Client
        packet.put((byte) 0x00); // 196
        switch (_shortcut.getType()) {
            case ITEM: {
                packet.putInt(_shortcut.getId());
                packet.putInt(_shortcut.getCharacterType());
                packet.putInt(_shortcut.getSharedReuseGroup());
                packet.putInt(0x00); // Remaining time
                packet.putInt(0x00); // Cool down time
                packet.putInt(0x00); // item augment effect 1
                packet.putInt(0x00); // item augment effect 2
                packet.putInt(0x00); // unk
                break;
            }
            case SKILL: {
                packet.putInt(_shortcut.getId());
                packet.putShort((short) _shortcut.getLevel());
                packet.putShort((short) _shortcut.getSubLevel());
                packet.putInt(_shortcut.getSharedReuseGroup());
                packet.put((byte) 0x00); // C5
                packet.putInt(_shortcut.getCharacterType());
                packet.putInt(0x00); // TODO: Find me
                packet.putInt(0x00); // TODO: Find me
                break;
            }
            case ACTION:
            case MACRO:
            case RECIPE:
            case BOOKMARK: {
                packet.putInt(_shortcut.getId());
                packet.putInt(_shortcut.getCharacterType());
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 41 ;
    }
}
