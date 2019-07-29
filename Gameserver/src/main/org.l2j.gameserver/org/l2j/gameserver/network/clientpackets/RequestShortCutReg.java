package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;

public final class RequestShortCutReg extends ClientPacket {
    private ShortcutType _type;
    private int _id;
    private int _slot;
    private int _page;
    private int _lvl;
    private int _subLvl;
    private int _characterType; // 1 - player, 2 - pet

    @Override
    public void readImpl() {
        final int typeId = readInt();
        _type = ShortcutType.values()[(typeId < 1) || (typeId > 6) ? 0 : typeId];
        final int slot = readInt();
        _slot = slot % 12;
        _page = slot / 12;
        _id = readInt();
        _lvl = readShort();
        _subLvl = readShort(); // Sublevel
        _characterType = readInt();
    }

    @Override
    public void runImpl() {
        if ((client.getPlayer() == null) || (_page > 19) || (_page < 0)) {
            return;
        }

        final Shortcut sc = new Shortcut(_slot, _page, _type, _id, _lvl, _subLvl, _characterType);
        client.getPlayer().registerShortCut(sc);
        client.sendPacket(new ShortCutRegister(sc));
    }
}
