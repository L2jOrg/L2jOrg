/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.FishingData;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerFishing;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingEnd;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingEnd.FishingEndReason;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingEnd.FishingEndType;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingStart;
import org.l2j.gameserver.network.serverpackets.fishing.ExUserInfoFishing;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.FishingZone;
import org.l2j.gameserver.world.zone.type.WaterZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * @author bit
 */
public class Fishing {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Fishing.class);
    private final Player player;
    private volatile ILocational baitLocation = new Location(0, 0, 0);
    private ScheduledFuture<?> reelInTask;
    private ScheduledFuture<?> startFishingTask;
    private boolean isFishing = false;
    private Item currentBait;

    public Fishing(Player player) {
        this.player = player;
    }

    /**
     * Computes the Z of the bait.
     *
     * @param player      the player
     * @param baitX       the bait x
     * @param baitY       the bait y
     * @param fishingZone the fishing zone
     * @param waterZone   the water zone
     * @return the bait z or {@link Integer#MIN_VALUE} when you cannot fish here
     */
    private static int computeBaitZ(Player player, int baitX, int baitY, FishingZone fishingZone, WaterZone waterZone) {
        if (isNull(fishingZone)) {
            return Integer.MIN_VALUE;
        }

        if (isNull(waterZone)) {
            return Integer.MIN_VALUE;
        }

        // always use water zone, fishing zone high z is high in the air...
        final int baitZ = waterZone.getWaterZ();

        if (GeoEngine.getInstance().hasGeo(baitX, baitY)) {
            if (GeoEngine.getInstance().getHeight(baitX, baitY, baitZ) > baitZ) {
                return Integer.MIN_VALUE;
            }

            if (GeoEngine.getInstance().getHeight(baitX, baitY, player.getZ()) > baitZ) {
                return Integer.MIN_VALUE;
            }
        }

        return baitZ;
    }

    public boolean isFishing() {
        return isFishing;
    }

    public boolean isAtValidLocation() {
        // TODO: implement checking direction
        return player.isInsideZone(ZoneType.FISHING);
    }

    public boolean canFish() {
        return !player.isDead() && !player.isAlikeDead() && !player.hasBlockActions() && !player.isSitting();
    }

    private FishingBaitData getCurrentBaitData() {
        if(nonNull(currentBait) && currentBait.getCount() > 0 && nonNull(player.getInventory().getItemByObjectId(currentBait.getObjectId()))) {
            return FishingData.getInstance().getBaitData(currentBait.getId());
        }

        var baits = player.getInventory().getItems(item -> item.getItemType() == EtcItemType.LURE);
        if(Util.isNullOrEmpty(baits)) {
            currentBait = null;
        } else {
            currentBait = baits.iterator().next();
            return FishingData.getInstance().getBaitData(currentBait.getId());
        }
        return null;
    }

    private void cancelTasks() {
        if (nonNull(reelInTask)) {
            reelInTask.cancel(false);
            reelInTask = null;
        }

        if (nonNull(startFishingTask)) {
            startFishingTask.cancel(false);
            startFishingTask = null;
        }
    }

    public void startFishing() {
        synchronized (this) {
            if (isFishing) {
                return;
            }
            isFishing = true;
        }
        castLine();
    }

    private void castLine() {
        if (!Config.ALLOW_FISHING && !player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS)) {
            player.sendMessage("Fishing is disabled.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        cancelTasks();

        if (!canFish()) {
            if (isFishing) {
                player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
            }
            stopFishing(FishingEndType.ERROR);
            return;
        }

        final Item rod = player.getActiveWeaponInstance();
        if (isNull(rod) || rod.getItemType() != WeaponType.FISHING_ROD) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        final FishingBaitData baitData = getCurrentBaitData();
        if (baitData == null) {
            player.sendPacket(SystemMessageId.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        final int minPlayerLevel = baitData.getMinPlayerLevel();
        if (player.getLevel() < minPlayerLevel) {
            if (minPlayerLevel == 20) {
                player.sendPacket(SystemMessageId.FISHING_WILL_END_BECAUSE_THE_CONDITIONS_HAVE_NOT_BEEN_MET);
            } else // In case of custom fishing level.
            {
                player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_FISHING_LEVEL_REQUIREMENTS);
            }
            player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        if (player.isTransformed() || player.isInBoat()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT_OR_TRANSFORMED);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        if (player.isCrafting() || player.isInStoreMode()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_WORKSHOP_OR_PRIVATE_STORE);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        if (player.isInsideZone(ZoneType.WATER) || player.isInWater()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_UNDER_WATER);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        baitLocation = calculateBaitLocation();
        if (!player.isInsideZone(ZoneType.FISHING) || (baitLocation == null)) {
            if (isFishing) {
                // _player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
                player.sendPacket(ActionFailed.STATIC_PACKET);
            } else {
                player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE);
                player.sendPacket(ActionFailed.STATIC_PACKET);
            }
            stopFishing(FishingEndType.ERROR);
            return;
        }

        reelInTask = ThreadPool.schedule(() -> {
            player.getFishing().reelInWithReward();
            startFishingTask = ThreadPool.schedule(() -> player.getFishing().castLine(), Rnd.get(baitData.getWaitMin(), baitData.getWaitMax()));
        }, Rnd.get(baitData.getTimeMin(), baitData.getTimeMax()));
        player.stopMove(null);
        player.broadcastPacket(new ExFishingStart(player, -1, baitData.getLevel(), baitLocation));
        player.sendPacket(new ExUserInfoFishing(player, true, baitLocation));
        player.sendPacket(new PlaySound("SF_P_01"));
        player.sendPacket(SystemMessageId.YOU_CAST_YOUR_LINE_AND_START_TO_FISH);
    }

    public void reelInWithReward() {
        // Fish may or may not eat the hook. If it does - it consumes fishing bait and fishing shot.
        // Then player may or may not catch the fish. Using fishing shots increases chance to win.
        final FishingBaitData baitData = getCurrentBaitData();
        if (baitData == null) {
            reelIn(FishingEndReason.LOSE, false);
            LOGGER.warn("Player {} is fishing with unhandled bait", player);
            return;
        }

        double chance = baitData.getChance();
        if (player.isChargedShot(ShotType.SOULSHOTS)) {
            player.consumeAndRechargeShotCount(ShotType.SOULSHOTS, 1);
            chance *= 1.5; // +50 % chance to win
        }

        if (Rnd.chance(chance)) {
            reelIn(FishingEndReason.WIN, true);
        } else {
            reelIn(FishingEndReason.LOSE, true);
        }
    }

    private void reelIn(FishingEndReason reason, boolean consumeBait) {
        if (!isFishing) {
            return;
        }

        cancelTasks();

        try {
            if (consumeBait) {
                if ((currentBait == null) || !player.getInventory().updateItemCount(null, currentBait, -1, player, null)) {
                    reason = FishingEndReason.LOSE; // no bait - no reward
                    return;
                }
            }

            if ((reason == FishingEndReason.WIN) && (currentBait != null)) {
                final FishingBaitData baitData = FishingData.getInstance().getBaitData(currentBait.getId());
                final int numRewards = baitData.getRewards().size();
                if (numRewards > 0) {
                    final FishingData fishingData = FishingData.getInstance();
                    final int lvlModifier = player.getLevel() * player.getLevel();
                    player.addExpAndSp(Rnd.get(fishingData.getExpRateMin(), fishingData.getExpRateMax()) * lvlModifier, Rnd.get(fishingData.getSpRateMin(), fishingData.getSpRateMax()) * lvlModifier, true);
                    final int fishId = baitData.getRewards().get(Rnd.get(0, numRewards - 1));
                    player.getInventory().addItem("Fishing Reward", fishId, 1, player, null);
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
                    msg.addItemName(fishId);
                    player.sendPacket(msg);
                    player.consumeAndRechargeShots(ShotType.SOULSHOTS, 1);
                } else {
                    LOGGER.warn("Could not find fishing rewards for bait {}", currentBait.getId());
                }
            } else if (reason == FishingEndReason.LOSE) {
                player.sendPacket(SystemMessageId.THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
            }

            if (consumeBait) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFishing(player, reason), player);
            }
        } finally {
            player.broadcastPacket(new ExFishingEnd(player, reason));
            player.sendPacket(new ExUserInfoFishing(player, false));
        }
    }

    public void stopFishing() {
        stopFishing(FishingEndType.PLAYER_STOP);
    }

    public synchronized void stopFishing(FishingEndType endType) {
        if (isFishing) {
            reelIn(FishingEndReason.STOP, false);
            isFishing = false;
            switch (endType) {
                case PLAYER_STOP -> player.sendPacket(SystemMessageId.YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING);
                case PLAYER_CANCEL -> player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
            }
        }
    }

    public ILocational getBaitLocation() {
        return baitLocation;
    }

    private Location calculateBaitLocation() {
        // calculate a position in front of the player with a random distance
        final int distMin = FishingData.getInstance().getBaitDistanceMin();
        final int distMax = FishingData.getInstance().getBaitDistanceMax();
        int distance = Rnd.get(distMin, distMax);
        final double angle = convertHeadingToDegree(player.getHeading());
        final double radian = Math.toRadians(angle);
        final double sin = Math.sin(radian);
        final double cos = Math.cos(radian);
        int baitX = (int) (player.getX() + (cos * distance));
        int baitY = (int) (player.getY() + (sin * distance));

        // search for fishing zone
        FishingZone fishingZone = null;
        for (Zone zone : ZoneManager.getInstance().getZones(player)) {
            if (zone instanceof FishingZone) {
                fishingZone = (FishingZone) zone;
                break;
            }
        }
        // search for water zone
        WaterZone waterZone = null;
        for (Zone zone : ZoneManager.getInstance().getZones(baitX, baitY)) {
            if (zone instanceof WaterZone) {
                waterZone = (WaterZone) zone;
                break;
            }
        }

        int baitZ = computeBaitZ(player, baitX, baitY, fishingZone, waterZone);
        if (baitZ == Integer.MIN_VALUE) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE);
            return null;
        }

        return new Location(baitX, baitY, baitZ);
    }
}
