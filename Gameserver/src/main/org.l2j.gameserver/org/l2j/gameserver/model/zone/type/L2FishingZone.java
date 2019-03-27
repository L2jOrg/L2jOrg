package org.l2j.gameserver.model.zone.type;

import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.Fishing;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.serverpackets.fishing.ExAutoFishAvailable;

import java.lang.ref.WeakReference;

/**
 * A fishing zone
 *
 * @author durgus
 */
public class L2FishingZone extends L2ZoneType {
    public L2FishingZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(L2Character character) {
        if (character.isPlayer()) {
            if ((Config.ALLOW_FISHING || character.canOverrideCond(PcCondOverride.ZONE_CONDITIONS)) && !character.isInsideZone(ZoneId.FISHING)) {
                final WeakReference<L2PcInstance> weakPlayer = new WeakReference<>(character.getActingPlayer());
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        final L2PcInstance player = weakPlayer.get();
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
                                ThreadPoolManager.getInstance().schedule(this, 1500);
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
    protected void onExit(L2Character character) {
        if (character.isPlayer()) {
            character.setInsideZone(ZoneId.FISHING, false);
            character.sendPacket(ExAutoFishAvailable.NO);
        }
    }

    /*
     * getWaterZ() this added function returns the Z value for the water surface. In effect this simply returns the upper Z value of the zone. This required some modification of L2ZoneForm, and zone form extensions.
     */
    public int getWaterZ() {
        return getZone().getHighZ();
    }
}
