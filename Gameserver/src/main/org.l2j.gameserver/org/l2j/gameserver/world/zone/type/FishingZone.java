package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.Fishing;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneId;
import org.l2j.gameserver.network.serverpackets.fishing.ExAutoFishAvailable;

import java.lang.ref.WeakReference;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A fishing zone
 *
 * @author durgus
 */
public class FishingZone extends Zone {
    public FishingZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature character) {
        if (isPlayer(character)) {
            if ((Config.ALLOW_FISHING || character.canOverrideCond(PcCondOverride.ZONE_CONDITIONS)) && !character.isInsideZone(ZoneId.FISHING)) {
                final WeakReference<Player> weakPlayer = new WeakReference<>(character.getActingPlayer());
                ThreadPoolManager.execute(new Runnable() {
                    @Override
                    public void run() {
                        final Player player = weakPlayer.get();
                        if (player != null) {
                            final Fishing fishing = player.getFishing();
                            if (player.isInsideZone(ZoneId.FISHING)) {
                                if (fishing.canFish() && !fishing.isFishing()) {
                                    if (fishing.isAtValidLocation()) {
                                        player.sendPacket(ExAutoFishAvailable.YES);
                                    } else {
                                        player.sendPacket(ExAutoFishAvailable.NO);
                                    }
                                }
                                ThreadPoolManager.schedule(this, 1500);
                            } else {
                                player.sendPacket(ExAutoFishAvailable.NO);
                            }
                        }
                    }
                });
            }
            character.setInsideZone(ZoneId.FISHING, true);
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (isPlayer(character)) {
            character.setInsideZone(ZoneId.FISHING, false);
            character.sendPacket(ExAutoFishAvailable.NO);
        }
    }

    /*
     * getWaterZ() this added function returns the Z value for the water surface. In effect this simply returns the upper Z value of the zone. This required some modification of ZoneForm, and zone form extensions.
     */
    public int getWaterZ() {
        return getForm().getHighZ();
    }
}
