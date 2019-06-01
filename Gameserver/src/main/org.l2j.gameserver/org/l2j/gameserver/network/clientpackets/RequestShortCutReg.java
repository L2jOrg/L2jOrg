package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;

import java.nio.ByteBuffer;

public final class RequestShortCutReg extends IClientIncomingPacket {
    private ShortcutType _type;
    private int _id;
    private int _slot;
    private int _page;
    private int _lvl;
    private int _subLvl;
    private int _characterType; // 1 - player, 2 - pet

    @Override
    public void readImpl(ByteBuffer packet) {
        final int typeId = packet.getInt();
        _type = ShortcutType.values()[(typeId < 1) || (typeId > 6) ? 0 : typeId];
        final int slot = packet.getInt();
        _slot = slot % 12;
        _page = slot / 12;
        packet.get(); // unk 196
        _id = packet.getInt();
        _lvl = packet.getShort();
        _subLvl = packet.getShort(); // Sublevel
        _characterType = packet.getInt();
    }

    @Override
    public void runImpl() {
        if ((client.getActiveChar() == null) || (_page > 19) || (_page < 0)) {
            return;
        }

        final Shortcut sc = new Shortcut(_slot, _page, _type, _id, _lvl, _subLvl, _characterType);
        client.getActiveChar().registerShortCut(sc);
        client.sendPacket(new ShortCutRegister(sc));
    }
}
