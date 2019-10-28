package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;

import static org.l2j.gameserver.network.SystemMessageId.ONLY_MACROS_CAN_BE_REGISTERED;

public final class RequestShortCutReg extends ClientPacket {
    private ShortcutType type;
    private int id;
    private int slot;
    private int page;
    private int lvl;
    private int subLvl;
    private int characterType; // 1 - player, 2 - pet

    @Override
    public void readImpl() {
        final int typeId = readInt();
        type = ShortcutType.values()[(typeId < 1) || (typeId > 6) ? 0 : typeId];
        final int slot = readInt();
        this.slot = slot % 12;
        page = slot / 12;
        readByte(); // unk 0
        id = readInt();
        lvl = readShort();
        subLvl = readShort(); // Sublevel
        characterType = readInt();
    }

    @Override
    public void runImpl() {
        if ((client.getPlayer() == null) || (page > 23) || (page < 0)) {
            return;
        }

        if(page == 23 && type != ShortcutType.MACRO) {
            client.sendPacket(ONLY_MACROS_CAN_BE_REGISTERED);
            return;
        }

        final Shortcut sc = new Shortcut(slot, page, type, id, lvl, subLvl, characterType);
        client.getPlayer().registerShortCut(sc);
        client.sendPacket(new ShortCutRegister(sc));
    }
}
