package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A PVP Zone
 *
 * @author durgus
 */
public class ArenaZone extends Zone {
    public ArenaZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayer(creature) && !creature.isInsideZone(ZoneType.PVP)) {
            creature.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
        }
        creature.setInsideZone(ZoneType.PVP, true);
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature) && creature.isInsideZone(ZoneType.PVP)) {
            creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
        }
        creature.setInsideZone(ZoneType.PVP, false);
    }
}
