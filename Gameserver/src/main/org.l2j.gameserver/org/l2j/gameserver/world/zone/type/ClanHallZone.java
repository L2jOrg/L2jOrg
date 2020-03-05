package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.world.zone.ZoneType;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A clan hall zone
 *
 * @author durgus
 */
public class ClanHallZone extends ResidenceZone {

    public ClanHallZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("clanHallId")) {
            setResidenceId(Integer.parseInt(value));
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature character) {
        if (isPlayer(character)) {
            character.setInsideZone(ZoneType.CLAN_HALL, true);
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (isPlayer(character)) {
            character.setInsideZone(ZoneType.CLAN_HALL, false);
        }
    }

    @Override
    public final Location getBanishSpawnLoc() {
        final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(getResidenceId());
        return isNull(clanHall) ? null : clanHall.getBanishLocation();
    }
}