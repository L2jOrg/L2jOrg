package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneEnter;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneExit;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A Peace Zone
 *
 * @author durgus
 */
public class PeaceZone extends Zone {
    public PeaceZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        if (!isEnabled()) {
            return;
        }

        if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            // PVP possible during siege, now for siege participants only
            // Could also check if this town is in siege, or if any siege is going on
            if ((player.getSiegeState() != 0) && (Config.PEACE_ZONE_MODE == 1)) {
                return;
            }
        }

        if (Config.PEACE_ZONE_MODE != 2) {
            creature.setInsideZone(ZoneType.PEACE, true);
            if(isPlayer(creature)) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPeaceZoneEnter(creature.getActingPlayer(), this), creature);
            }
        }

        if (!getAllowStore()) {
            creature.setInsideZone(ZoneType.NO_STORE, true);
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (Config.PEACE_ZONE_MODE != 2) {
            creature.setInsideZone(ZoneType.PEACE, false);
            if(isPlayer(creature)) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPeaceZoneExit(creature.getActingPlayer()), creature);
            }
        }

        if (!getAllowStore()) {
            creature.setInsideZone(ZoneType.NO_STORE, false);
        }
    }

    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        if (state) {
            forEachPlayer(player -> {
                revalidateInZone(player);

                if(nonNull(player.getPet())) {
                    revalidateInZone(player.getPet());
                }

                player.getServitors().values().forEach(this::revalidateInZone);
            });
        } else {
            forEachCreature(this::removeCreature);
        }
    }
}
