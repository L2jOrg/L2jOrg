package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class ShortCutRegister extends ServerPacket {
    private final Shortcut shortcut;

    /**
     * Register new skill shortcut
     *
     * @param shortcut
     */
    public ShortCutRegister(Shortcut shortcut) {
        this.shortcut = shortcut;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHORT_CUT_REGISTER);

        writeInt(shortcut.getType().ordinal());
        writeInt(shortcut.getSlot() + (shortcut.getPage() * 12)); // C4 Client
        writeByte(0);
        switch (shortcut.getType()) {
            case ITEM -> writeShortcutItem();
            case SKILL -> writeShortcutSkill();
            case ACTION, MACRO, RECIPE, BOOKMARK -> {
                writeInt(shortcut.getId());
                writeInt(shortcut.getCharacterType());
            }
        }
    }

    private void writeShortcutSkill() {
        writeInt(shortcut.getId());
        writeShort(shortcut.getLevel());
        writeShort(shortcut.getSubLevel());
        writeInt(shortcut.getSharedReuseGroup());
        writeByte(0x00); // C5
        writeInt(shortcut.getCharacterType());
        writeInt(0x00); // TODO: Find me
        writeInt(0x00); // TODO: Find me
    }

    private void writeShortcutItem() {
        writeInt(shortcut.getId());
        writeInt(shortcut.getCharacterType());
        writeInt(shortcut.getSharedReuseGroup());
        writeInt(0x00); // Remaining time
        writeInt(0x00); // Cool down time
        writeInt(0x00); // item augment effect 1
        writeInt(0x00); // item augment effect 2
        writeInt(0x00); // unk
    }

}
