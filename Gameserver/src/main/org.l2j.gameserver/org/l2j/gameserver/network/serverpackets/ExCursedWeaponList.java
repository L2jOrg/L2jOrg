package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.util.Set;

/**
 * @author -Wooden-
 */
public class ExCursedWeaponList extends IClientOutgoingPacket {
    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_CURSED_WEAPON_LIST);

        final Set<Integer> ids = CursedWeaponsManager.getInstance().getCursedWeaponsIds();
        writeInt(ids.size());
        ids.forEach(this::writeInt);
    }

}
