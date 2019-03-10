package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author -Wooden-
 */
public class ExCursedWeaponList extends IClientOutgoingPacket {
    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CURSED_WEAPON_LIST.writeId(packet);

        final Set<Integer> ids = CursedWeaponsManager.getInstance().getCursedWeaponsIds();
        packet.putInt(ids.size());
        ids.forEach(packet::putInt);
    }
}
