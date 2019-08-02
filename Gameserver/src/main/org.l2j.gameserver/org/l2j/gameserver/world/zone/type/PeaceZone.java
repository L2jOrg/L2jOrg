package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneId;

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
    protected void onEnter(Creature character) {
        if (!isEnabled()) {
            return;
        }

        if (isPlayer(character)) {
            final Player player = character.getActingPlayer();
            // PVP possible during siege, now for siege participants only
            // Could also check if this town is in siege, or if any siege is going on
            if ((player.getSiegeState() != 0) && (Config.PEACE_ZONE_MODE == 1)) {
                return;
            }
        }

        if (Config.PEACE_ZONE_MODE != 2) {
            character.setInsideZone(ZoneId.PEACE, true);
        }

        if (!getAllowStore()) {
            character.setInsideZone(ZoneId.NO_STORE, true);
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (Config.PEACE_ZONE_MODE != 2) {
            character.setInsideZone(ZoneId.PEACE, false);
        }

        if (!getAllowStore()) {
            character.setInsideZone(ZoneId.NO_STORE, false);
        }
    }

    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        if (state) {
            for (Player player : World.getInstance().getPlayers()) {
                if ((player != null) && isInsideZone(player)) {
                    revalidateInZone(player);

                    if (player.getPet() != null) {
                        revalidateInZone(player.getPet());
                    }

                    for (Summon summon : player.getServitors().values()) {
                        revalidateInZone(summon);
                    }
                }
            }
        } else {
            for (Creature character : getCharactersInside()) {
                if (character != null) {
                    removeCharacter(character);
                }
            }
        }
    }
}
