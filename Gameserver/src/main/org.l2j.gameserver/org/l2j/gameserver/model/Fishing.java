/*
 * Copyright © 2019-2021 L2JOrg
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
import org.l2j.gameserver.engine.fishing.FishingBait;
import org.l2j.gameserver.engine.fishing.FishingEngine;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerFishing;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingEnd;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingEnd.FishingEndReason;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingEnd.FishingEndType;
import org.l2j.gameserver.network.serverpackets.fishing.ExFishingStart;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.FishingZone;
import org.l2j.gameserver.world.zone.type.WaterZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * @author bit
 * @author JoeAlisson
 */
public class Fishing {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Fishing.class);
    private static final Location DEFAULT_BAIT_LOCATION = new Location(0, 0, 0);
    private final Player player;
    private ILocational baitLocation = DEFAULT_BAIT_LOCATION;
    private ScheduledFuture<?> reelInTask;
    private ScheduledFuture<?> startFishingTask;
    private boolean isFishing = false;

    public Fishing(Player player) {
        this.player = player;
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
        if (!GeneralSettings.allowFishing()) {
            player.sendMessage("Fishing is disabled.");
            stopFishing(FishingEndType.ERROR);
            return;
        }

        cancelTasks();

        var bait = validateCastBait();
        if (bait != null) {
            castLineWithBait(bait);
        }
    }

    private void castLineWithBait(FishingBait bait) {
        player.broadcastPacket(new ExFishingStart(player, 1, bait.level(), baitLocation));
        reelInTask = ThreadPool.schedule(this::reelInWithReward, Rnd.get(bait.minTime(), bait.maxTime()));
        player.stopMove(null);
        player.sendPacket(PlaySound.sound("SF_P_01"));
        player.sendPacket(SystemMessageId.YOU_CAST_YOUR_LINE_AND_START_TO_FISH);
        player.consumeAndRechargeShots(ShotType.SOULSHOTS, 1);
    }

    private FishingBait validateCastBait() {
        if (!canFish()) {
            if (isFishing) {
                player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
            }
            stopFishing(FishingEndType.ERROR);
            return null;
        }

        final Item rod = player.getActiveWeaponInstance();
        if (isNull(rod) || rod.getItemType() != WeaponType.FISHING_ROD) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
            stopFishing(FishingEndType.ERROR);
            return null;
        }

        final FishingBait baitData = getCurrentBait();
        if (baitData == null) {
            player.sendPacket(SystemMessageId.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
            stopFishing(FishingEndType.ERROR);
            return null;
        }

        if (!checkCastLine(baitData)) {
            stopFishing(FishingEndType.ERROR);
            return null;
        }
        return baitData;
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

    private FishingBait getCurrentBait() {
        var item = player.getSecondaryWeapon();
        if(item != null && item.getItemType() == EtcItemType.LURE) {
            return FishingEngine.getInstance().getBaitData(item.getId());
        }
        return null;
    }

    private boolean checkCastLine(FishingBait baitData) {
        final int minPlayerLevel = baitData.minPlayerLevel();
        if (player.getLevel() < minPlayerLevel) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_FISHING_LEVEL_REQUIREMENTS);
            return false;
        }

        if (player.isTransformed()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT_OR_TRANSFORMED);
            return false;
        }

        if (player.isCrafting() || player.isInStoreMode()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_WORKSHOP_OR_PRIVATE_STORE);
            return false;
        }

        if (player.isInsideZone(ZoneType.WATER) || player.isInWater()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_UNDER_WATER);
            return false;
        }

        baitLocation = calculateBaitLocation();
        if (!player.isInsideZone(ZoneType.FISHING) || baitLocation == DEFAULT_BAIT_LOCATION) {
            if (!isFishing) {
                player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE);
            }
            return false;
        }
        return true;
    }

    private void reelInWithReward() {
        final FishingBait baitData = getCurrentBait();
        if (baitData == null) {
            reelIn(FishingEndReason.LOSE, false);
            LOGGER.warn("Player {} is fishing with unhandled bait", player);
            return;
        }

        double chance = baitData.chance() * player.chargedShotBonus(ShotType.SOULSHOTS);

        if (Rnd.chance(chance)) {
            reelIn(FishingEndReason.WIN, true);
        } else {
            reelIn(FishingEndReason.LOSE, true);
        }

        startFishingTask = ThreadPool.schedule(this::castLine, Rnd.get(baitData.minWait(), baitData.maxWait()));
    }

    private void reelIn(FishingEndReason reason, boolean consumeBait) {
        if (!isFishing) {
            return;
        }

        cancelTasks();

        try {
            var bait = player.getSecondaryWeapon();

            if (consumeBait && !player.getInventory().updateItemCount(null, bait, -1, player, null)) {
                reason = FishingEndReason.LOSE;
                return;
            }

            if (reason == FishingEndReason.WIN && (bait != null)) {
                giveFishingReward(bait);
            } else if (reason == FishingEndReason.LOSE) {
                player.sendPacket(SystemMessageId.THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
            }

            if (consumeBait) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFishing(player, reason), player);
            }
        } finally {
            player.broadcastPacket(new ExFishingEnd(player, reason));
        }
    }

    private void giveFishingReward(Item bait) {
        final FishingBait baitData = FishingEngine.getInstance().getBaitData(bait.getId());
        final int numRewards = baitData.rewards().size();
        if (numRewards > 0) {
            final var fishingEngine = FishingEngine.getInstance();
            final int lvlModifier = player.getLevel() * player.getLevel();
            player.addExpAndSp(Rnd.get(fishingEngine.getExpRateMin(), fishingEngine.getExpRateMax()) * lvlModifier, Rnd.get(fishingEngine.getSpRateMin(), fishingEngine.getSpRateMax()) * lvlModifier, true);
            final var fish = baitData.rewards().get(Rnd.get(0, numRewards - 1));
            player.getInventory().addItem("Fishing Reward", fish.getId(), 1, player, null);
            player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1).addItemName(fish.getId()));
        } else {
            LOGGER.warn("Could not find fishing rewards for bait {}", bait.getId());
        }
    }

    public void stopFishing() {
        stopFishing(FishingEndType.PLAYER_STOP);
    }

    private synchronized void stopFishing(FishingEndType endType) {
        if (isFishing) {
            reelIn(FishingEndReason.STOP, false);
            isFishing = false;
            switch (endType) {
                case PLAYER_STOP -> player.sendPacket(SystemMessageId.YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING);
                case PLAYER_CANCEL -> player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
            }
        }
    }

    private Location calculateBaitLocation() {
        final int distMin = FishingEngine.getInstance().getBaitDistanceMin();
        final int distMax = FishingEngine.getInstance().getBaitDistanceMax();
        int distance = Rnd.get(distMin, distMax);
        final double angle = convertHeadingToDegree(player.getHeading());
        final double radian = Math.toRadians(angle);
        final double sin = Math.sin(radian);
        final double cos = Math.cos(radian);
        int baitX = (int) (player.getX() + (cos * distance));
        int baitY = (int) (player.getY() + (sin * distance));

        var fishingZone = ZoneEngine.getInstance().findFirstZone(player, FishingZone.class);
        var waterZone = ZoneEngine.getInstance().findFirstZone(baitX, baitY, WaterZone.class);
        int baitZ = computeBaitZ(player, baitX, baitY, fishingZone, waterZone);
        if (baitZ == Integer.MIN_VALUE) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE);
            return DEFAULT_BAIT_LOCATION;
        }
        return new Location(baitX, baitY, baitZ);
    }

    private int computeBaitZ(Player player, int baitX, int baitY, FishingZone fishingZone, WaterZone waterZone) {
        if (isNull(fishingZone)) {
            return Integer.MIN_VALUE;
        }

        if (isNull(waterZone)) {
            return Integer.MIN_VALUE;
        }

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

    public boolean canFish() {
        return !player.isDead() && !player.isAlikeDead() && !player.hasBlockActions() && !player.isSitting();
    }

    public boolean isFishing() {
        return isFishing;
    }

    public ILocational getBaitLocation() {
        return baitLocation;
    }
}
