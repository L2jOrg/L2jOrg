package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.ONLY_MACROS_CAN_BE_REGISTERED;

/**
 * @author JoeAlisson
 */
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
        final int room = readInt();
        this.slot = room % 12;
        page = room / 12;
        readByte(); // unk 0
        id = readInt();
        lvl = readShort();
        subLvl = readShort(); // Sublevel
        characterType = readInt();
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();
        if (isNull(player) || (page > 23) || (page < 0)) {
            return;
        }

        if(page == Shortcut.AUTO_PLAY_PAGE) {
            if(slot == Shortcut.AUTO_MACRO_SLOT && type != ShortcutType.MACRO) {
                client.sendPacket(ONLY_MACROS_CAN_BE_REGISTERED);
                return;
            }

            Item item;
            if(slot == Shortcut.AUTO_POTION_SLOT && ( type != ShortcutType.ITEM || isNull(item = player.getInventory().getItemByObjectId(id)) || !item.isAutoPotion())) {
                return;
            }
        }

        final Shortcut sc = new Shortcut(slot, page, type, id, lvl, subLvl, characterType);
        client.getPlayer().registerShortCut(sc);
        client.sendPacket(new ShortCutRegister(sc));
    }
}
