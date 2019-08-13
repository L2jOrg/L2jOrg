package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A no landing zone
 *
 * @author durgus
 */
public class NoLandingZone extends Zone {
    private int dismountDelay = 5;

    public NoLandingZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("dismountDelay")) {
            dismountDelay = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_LANDING, true);
            if (creature.getActingPlayer().getMountType() == MountType.WYVERN) {
                creature.sendPacket(SystemMessageId.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN_YOU_WILL_BE_DISMOUNTED_FROM_YOUR_WYVERN_IF_YOU_DO_NOT_LEAVE);
                creature.getActingPlayer().enteredNoLanding(dismountDelay);
            }
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_LANDING, false);
            if (creature.getActingPlayer().getMountType() == MountType.WYVERN) {
                creature.getActingPlayer().exitedNoLanding();
            }
        }
    }
}
