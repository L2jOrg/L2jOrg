package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.CursedWeapon;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExCursedWeaponLocation;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Format: (ch)
 *
 * @author -Wooden-
 */
public final class RequestCursedWeaponLocation extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final List<ExCursedWeaponLocation.CursedWeaponInfo> list = new LinkedList<>();
        for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons()) {
            if (!cw.isActive()) {
                continue;
            }

            final Location pos = cw.getWorldPosition();
            if (pos != null) {
                list.add(new ExCursedWeaponLocation.CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
            }
        }

        // send the ExCursedWeaponLocation
        if (!list.isEmpty()) {
            client.sendPacket(new ExCursedWeaponLocation(list));
        }
    }
}
