package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Set;

/**
 * @author -Wooden-
 */
public class ExCursedWeaponList extends ServerPacket {
    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_CURSED_WEAPON_LIST);

        final Set<Integer> ids = CursedWeaponsManager.getInstance().getCursedWeaponsIds();
        writeInt(ids.size());
        ids.forEach(this::writeInt);
    }

}
