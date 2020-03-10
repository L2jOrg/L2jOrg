package org.l2j.gameserver.network.clientpackets.pvpbook;

import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.CANNOT_LOCATE_THE_SELECTED_FOE_THE_FOE_IS_NOT_ONLINE;

/**
 * @author JoeAlisson
 */
public class ExTeleportToKiller extends ClientPacket {

    private String killerName;

    @Override
    protected void readImpl() throws Exception {
        killerName = readSizedString();
    }

    @Override
    protected void runImpl() {
        var killer = World.getInstance().findPlayer(killerName);

        if(isNull(killer)){
            client.sendPacket(CANNOT_LOCATE_THE_SELECTED_FOE_THE_FOE_IS_NOT_ONLINE);
            return;
        }

        if(killer.isInsideZone(ZoneType.PEACE) || killer.isInsideZone(ZoneType.SIEGE) || killer.isInOlympiadMode()) {
            return;
        }

        var player = client.getPlayer();
        if(player.getRevengeUsableTeleport() > 0 && player.reduceAdena("Teleport To Killer", 140000, player, true)) {
            player.useRevengeTeleport();
            CommonSkill.HIDE.getSkill().applyEffects(player, player);
            player.teleToLocation(killer.getLocation());
        }
    }
}
