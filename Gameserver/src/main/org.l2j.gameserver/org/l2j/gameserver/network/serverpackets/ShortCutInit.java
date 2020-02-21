package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public final class ShortCutInit extends ServerPacket {

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHORT_CUT_INIT);

        var player = client.getPlayer();

        writeInt(player.getShortcutAmount());

        player.forEachShortcut(s -> {
            writeInt(s.getType().ordinal());
            writeInt(s.getClientId());
            writeByte(0x00); // 228

            switch (s.getType()) {
                case ITEM -> writeShortcutItem(s);
                case SKILL -> writeShortcutSkill(s);
                case ACTION, MACRO, RECIPE, BOOKMARK -> {
                    writeInt(s.getShortcutId());
                    writeInt(s.getCharacterType());
                }
            }
        });
    }

    private void writeShortcutSkill(Shortcut sc) {
        writeInt(sc.getShortcutId());
        writeShort(sc.getLevel());
        writeShort(sc.getSubLevel());
        writeInt(sc.getSharedReuseGroup());
        writeByte(0x00);
        writeInt(0x01);
    }

    private void writeShortcutItem(Shortcut sc) {
        writeInt(sc.getShortcutId());
        writeInt(0x01); // Enabled or not
        writeInt(sc.getSharedReuseGroup());
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00); // Augment effect 1
        writeInt(0x00); // Augment effect 2
        writeInt(0x00); // Visual id
    }

}
