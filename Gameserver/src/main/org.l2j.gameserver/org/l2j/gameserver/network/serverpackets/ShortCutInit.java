package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class ShortCutInit extends ServerPacket {
    private Shortcut[] shortCuts;

    public ShortCutInit(Player player) {
        if (player == null) {
            return;
        }

        shortCuts = player.getAllShortCuts();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHORT_CUT_INIT);

        writeInt(shortCuts.length);
        for (Shortcut sc : shortCuts) {

            writeInt(sc.getType().ordinal());
            writeInt(sc.getSlot() + (sc.getPage() * 12));
            writeByte(0x00); // 228

            switch (sc.getType()) {
                case ITEM -> writeShortcutItem(sc);
                case SKILL -> writeShortcutSkill(sc);
                case ACTION, MACRO, RECIPE, BOOKMARK -> {
                    writeInt(sc.getId());
                    writeInt(sc.getCharacterType());
                }
            }
        }
    }

    private void writeShortcutSkill(Shortcut sc) {
        writeInt(sc.getId());
        writeShort(sc.getLevel());
        writeShort(sc.getSubLevel());
        writeInt(sc.getSharedReuseGroup());
        writeByte(0x00);
        writeInt(0x01);
    }

    private void writeShortcutItem(Shortcut sc) {
        writeInt(sc.getId());
        writeInt(0x01); // Enabled or not
        writeInt(sc.getSharedReuseGroup());
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00); // Augment effect 1
        writeInt(0x00); // Augment effect 2
        writeInt(0x00); // Visual id
    }

}
