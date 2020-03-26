package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.FishingData;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerFishing;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.WeaponType;
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

import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * @author bit
 */
public class Fishing {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Fishing.class);
    private final Player _player;
    private volatile ILocational _baitLocation = new Location(0, 0, 0);
    private ScheduledFuture<?> _reelInTask;
    private ScheduledFuture<?> _startFishingTask;
    private boolean _isFishing = false;

    public Fishing(Player player) {
        _player = player;
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
        if ((fishingZone == null)) {
            return Integer.MIN_VALUE;
        }

        if ((waterZone == null)) {
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

    public synchronized boolean isFishing() {
        return _isFishing;
    }

    public boolean isAtValidLocation() {
        // TODO: implement checking direction
        return _player.isInsideZone(ZoneType.FISHING);
    }

    public boolean canFish() {
        return !_player.isDead() && !_player.isAlikeDead() && !_player.hasBlockActions() && !_player.isSitting();
    }

    private FishingBaitData getCurrentBaitData() {
        final Item bait = _player.getInventory().getPaperdollItem(InventorySlot.LEFT_HAND);
        return bait != null ? FishingData.getInstance().getBaitData(bait.getId()) : null;
    }

    private void cancelTasks() {
        if (_reelInTask != null) {
            _reelInTask.cancel(false);
            _reelInTask = null;
        }

        if (_startFishingTask != null) {
            _startFishingTask.cancel(false);
            _startFishingTask = null;
        }
    }

    public synchronized void startFishing() {
        if (_isFishing) {
            return;
        }
        _isFishing = true;
        castLine();
    }

    private void castLine() {
        if (!Config.ALLOW_FISHING && !_player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS)) {
            _player.sendMessage("Fishing is disabled.");
            _player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        cancelTasks();

        if (!canFish()) {
            if (_isFishing) {
                _player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
            }
            stopFishing(FishingEndType.ERROR);
            return;
        }

        final FishingBaitData baitData = getCurrentBaitData();
        final int minPlayerLevel = baitData == null ? 20 : baitData.getMinPlayerLevel();
        if (_player.getLevel() < minPlayerLevel) {
            if (minPlayerLevel == 20) {
                _player.sendPacket(SystemMessageId.FISHING_WILL_END_BECAUSE_THE_CONDITIONS_HAVE_NOT_BEEN_MET);
            } else // In case of custom fishing level.
            {
                _player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_FISHING_LEVEL_REQUIREMENTS);
            }
            _player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        final Item rod = _player.getActiveWeaponInstance();
        if ((rod == null) || (rod.getItemType() != WeaponType.FISHING_ROD)) {
            _player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
            _player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        if (baitData == null) {
            _player.sendPacket(SystemMessageId.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
            _player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        if (_player.isTransformed() || _player.isInBoat()) {
            _player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT_OR_TRANSFORMED);
            _player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        if (_player.isCrafting() || _player.isInStoreMode()) {
            _player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_WORKSHOP_OR_PRIVATE_STORE);
            _player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        if (_player.isInsideZone(ZoneType.WATER) || _player.isInWater()) {
            _player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_UNDER_WATER);
            _player.sendPacket(ActionFailed.STATIC_PACKET);
            stopFishing(FishingEndType.ERROR);
            return;
        }

        _baitLocation = calculateBaitLocation();
        if (!_player.isInsideZone(ZoneType.FISHING) || (_baitLocation == null)) {
            if (_isFishing) {
                // _player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
                _player.sendPacket(ActionFailed.STATIC_PACKET);
            } else {
                _player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE);
                _player.sendPacket(ActionFailed.STATIC_PACKET);
            }
            stopFishing(FishingEndType.ERROR);
            return;
        }

        _reelInTask = ThreadPool.schedule(() -> {
            _player.getFishing().reelInWithReward();
            _startFishingTask = ThreadPool.schedule(() -> _player.getFishing().castLine(), Rnd.get(baitData.getWaitMin(), baitData.getWaitMax()));
        }, Rnd.get(baitData.getTimeMin(), baitData.getTimeMax()));
        _player.stopMove(null);
        _player.broadcastPacket(new ExFishingStart(_player, -1, baitData.getLevel(), _baitLocation));
        _player.sendPacket(new ExUserInfoFishing(_player, true, _baitLocation));
        _player.sendPacket(new PlaySound("SF_P_01"));
        _player.sendPacket(SystemMessageId.YOU_CAST_YOUR_LINE_AND_START_TO_FISH);
    }

    public void reelInWithReward() {
        // Fish may or may not eat the hook. If it does - it consumes fishing bait and fishing shot.
        // Then player may or may not catch the fish. Using fishing shots increases chance to win.
        final FishingBaitData baitData = getCurrentBaitData();
        if (baitData == null) {
            reelIn(FishingEndReason.LOSE, false);
            LOGGER.warn("Player {} is fishing with unhandled bait: {}", _player,  _player.getInventory().getPaperdollItem(InventorySlot.LEFT_HAND));
            return;
        }

        double chance = baitData.getChance();
        if (_player.isChargedShot(ShotType.SOULSHOTS)) {
            chance *= 1.5; // +50 % chance to win
        }

        if (Rnd.get(0, 100) <= chance) {
            reelIn(FishingEndReason.WIN, true);
        } else {
            reelIn(FishingEndReason.LOSE, true);
        }
    }

    private void reelIn(FishingEndReason reason, boolean consumeBait) {
        if (!_isFishing) {
            return;
        }

        cancelTasks();

        try {
            final Item bait = _player.getInventory().getPaperdollItem(InventorySlot.LEFT_HAND);
            if (consumeBait) {
                if ((bait == null) || !_player.getInventory().updateItemCount(null, bait, -1, _player, null)) {
                    reason = FishingEndReason.LOSE; // no bait - no reward
                    return;
                }
            }

            if ((reason == FishingEndReason.WIN) && (bait != null)) {
                final FishingBaitData baitData = FishingData.getInstance().getBaitData(bait.getId());
                final int numRewards = baitData.getRewards().size();
                if (numRewards > 0) {
                    final FishingData fishingData = FishingData.getInstance();
                    final int lvlModifier = _player.getLevel() * _player.getLevel();
                    _player.addExpAndSp(Rnd.get(fishingData.getExpRateMin(), fishingData.getExpRateMax()) * lvlModifier, Rnd.get(fishingData.getSpRateMin(), fishingData.getSpRateMax()) * lvlModifier, true);
                    final int fishId = baitData.getRewards().get(Rnd.get(0, numRewards - 1));
                    _player.getInventory().addItem("Fishing Reward", fishId, 1, _player, null);
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
                    msg.addItemName(fishId);
                    _player.sendPacket(msg);
                    _player.consumeAndRechargeShots(ShotType.SOULSHOTS, 1);
                } else {
                    LOGGER.warn("Could not find fishing rewards for bait {}", bait.getId());
                }
            } else if (reason == FishingEndReason.LOSE) {
                _player.sendPacket(SystemMessageId.THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
            }

            if (consumeBait) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFishing(_player, reason), _player);
            }
        } finally {
            _player.broadcastPacket(new ExFishingEnd(_player, reason));
            _player.sendPacket(new ExUserInfoFishing(_player, false));
        }
    }

    public void stopFishing() {
        stopFishing(FishingEndType.PLAYER_STOP);
    }

    public synchronized void stopFishing(FishingEndType endType) {
        if (_isFishing) {
            reelIn(FishingEndReason.STOP, false);
            _isFishing = false;
            switch (endType) {
                case PLAYER_STOP -> _player.sendPacket(SystemMessageId.YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING);
                case PLAYER_CANCEL -> _player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
            }
        }
    }

    public ILocational getBaitLocation() {
        return _baitLocation;
    }

    private Location calculateBaitLocation() {
        // calculate a position in front of the player with a random distance
        final int distMin = FishingData.getInstance().getBaitDistanceMin();
        final int distMax = FishingData.getInstance().getBaitDistanceMax();
        int distance = Rnd.get(distMin, distMax);
        final double angle = convertHeadingToDegree(_player.getHeading());
        final double radian = Math.toRadians(angle);
        final double sin = Math.sin(radian);
        final double cos = Math.cos(radian);
        int baitX = (int) (_player.getX() + (cos * distance));
        int baitY = (int) (_player.getY() + (sin * distance));

        // search for fishing zone
        FishingZone fishingZone = null;
        for (Zone zone : ZoneManager.getInstance().getZones(_player)) {
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

        int baitZ = computeBaitZ(_player, baitX, baitY, fishingZone, waterZone);
        if (baitZ == Integer.MIN_VALUE) {
            _player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE);
            return null;
        }

        return new Location(baitX, baitY, baitZ);
    }
}
