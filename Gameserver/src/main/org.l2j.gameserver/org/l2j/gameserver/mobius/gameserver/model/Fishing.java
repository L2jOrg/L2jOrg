/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model;

import com.l2jmobius.Config;
import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.data.xml.impl.FishingData;
import com.l2jmobius.gameserver.enums.ShotType;
import com.l2jmobius.gameserver.geoengine.GeoEngine;
import com.l2jmobius.gameserver.instancemanager.ZoneManager;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventDispatcher;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerFishing;
import com.l2jmobius.gameserver.model.interfaces.ILocational;
import com.l2jmobius.gameserver.model.itemcontainer.Inventory;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.model.items.type.WeaponType;
import com.l2jmobius.gameserver.model.zone.L2ZoneType;
import com.l2jmobius.gameserver.model.zone.ZoneId;
import com.l2jmobius.gameserver.model.zone.type.L2FishingZone;
import com.l2jmobius.gameserver.model.zone.type.L2WaterZone;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import com.l2jmobius.gameserver.network.serverpackets.PlaySound;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import com.l2jmobius.gameserver.network.serverpackets.fishing.ExFishingEnd;
import com.l2jmobius.gameserver.network.serverpackets.fishing.ExFishingEnd.FishingEndReason;
import com.l2jmobius.gameserver.network.serverpackets.fishing.ExFishingEnd.FishingEndType;
import com.l2jmobius.gameserver.network.serverpackets.fishing.ExFishingStart;
import com.l2jmobius.gameserver.network.serverpackets.fishing.ExUserInfoFishing;
import com.l2jmobius.gameserver.util.Util;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author bit
 */
public class Fishing
{
	protected static final Logger LOGGER = Logger.getLogger(Fishing.class.getName());
	private volatile ILocational _baitLocation = new Location(0, 0, 0);
	
	private final L2PcInstance _player;
	private ScheduledFuture<?> _reelInTask;
	private ScheduledFuture<?> _startFishingTask;
	private boolean _isFishing = false;
	
	public Fishing(L2PcInstance player)
	{
		_player = player;
	}
	
	public synchronized boolean isFishing()
	{
		return _isFishing;
	}
	
	public boolean isAtValidLocation()
	{
		// TODO: implement checking direction
		// if (calculateBaitLocation() == null)
		// {
		// return false;
		// }
		return _player.isInsideZone(ZoneId.FISHING);
	}
	
	public boolean canFish()
	{
		return !_player.isDead() && !_player.isAlikeDead() && !_player.hasBlockActions() && !_player.isSitting();
	}
	
	private FishingBaitData getCurrentBaitData()
	{
		final L2ItemInstance bait = _player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		return bait != null ? FishingData.getInstance().getBaitData(bait.getId()) : null;
	}
	
	private void cancelTasks()
	{
		if (_reelInTask != null)
		{
			_reelInTask.cancel(false);
			_reelInTask = null;
		}
		
		if (_startFishingTask != null)
		{
			_startFishingTask.cancel(false);
			_startFishingTask = null;
		}
	}
	
	public synchronized void startFishing()
	{
		if (_isFishing)
		{
			return;
		}
		_isFishing = true;
		castLine();
	}
	
	private void castLine()
	{
		if (!Config.ALLOW_FISHING && !_player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS))
		{
			_player.sendMessage("Fishing is disabled.");
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		cancelTasks();
		
		if (!canFish())
		{
			if (_isFishing)
			{
				_player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
			}
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		final FishingBaitData baitData = getCurrentBaitData();
		final int minPlayerLevel = baitData == null ? 20 : baitData.getMinPlayerLevel();
		if (_player.getLevel() < minPlayerLevel)
		{
			if (minPlayerLevel == 20)
			{
				_player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_AS_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
			}
			else // In case of custom fishing level.
			{
				_player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_FISHING_LEVEL_REQUIREMENTS);
			}
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		final L2ItemInstance rod = _player.getActiveWeaponInstance();
		if ((rod == null) || (rod.getItemType() != WeaponType.FISHINGROD))
		{
			_player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		if (baitData == null)
		{
			_player.sendPacket(SystemMessageId.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		if (_player.isTransformed() || _player.isInBoat())
		{
			_player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT_OR_TRANSFORMED);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		if (_player.isCrafting() || _player.isInStoreMode())
		{
			_player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_WORKSHOP_OR_PRIVATE_STORE);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		if (_player.isInsideZone(ZoneId.WATER) || _player.isInWater())
		{
			_player.sendPacket(SystemMessageId.YOU_CANNOT_FISH_WHILE_UNDER_WATER);
			_player.sendPacket(ActionFailed.STATIC_PACKET);
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		_baitLocation = calculateBaitLocation();
		if (!_player.isInsideZone(ZoneId.FISHING) || (_baitLocation == null))
		{
			if (_isFishing)
			{
				// _player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
				_player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			else
			{
				_player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE);
				_player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			stopFishing(FishingEndType.ERROR);
			return;
		}
		
		if (!_player.isChargedShot(ShotType.FISH_SOULSHOTS))
		{
			_player.rechargeShots(false, false, true);
		}
		
		_reelInTask = ThreadPool.schedule(() ->
		{
			_player.getFishing().reelInWithReward();
			_startFishingTask = ThreadPool.schedule(() -> _player.getFishing().castLine(), Rnd.get(baitData.getWaitMin(), baitData.getWaitMax()));
		}, Rnd.get(baitData.getTimeMin(), baitData.getTimeMax()));
		_player.stopMove(null);
		_player.broadcastPacket(new ExFishingStart(_player, -1, baitData.getLevel(), _baitLocation));
		_player.sendPacket(new ExUserInfoFishing(_player, true, _baitLocation));
		_player.sendPacket(new PlaySound("SF_P_01"));
		_player.sendPacket(SystemMessageId.YOU_CAST_YOUR_LINE_AND_START_TO_FISH);
	}
	
	public void reelInWithReward()
	{
		// Fish may or may not eat the hook. If it does - it consumes fishing bait and fishing shot.
		// Then player may or may not catch the fish. Using fishing shots increases chance to win.
		final FishingBaitData baitData = getCurrentBaitData();
		if (baitData == null)
		{
			reelIn(FishingEndReason.LOSE, false);
			LOGGER.warning("Player " + _player + " is fishing with unhandled bait: " + _player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND));
			return;
		}
		
		double chance = baitData.getChance();
		if (_player.isChargedShot(ShotType.FISH_SOULSHOTS))
		{
			chance *= 1.5; // +50 % chance to win
		}
		
		if (Rnd.get(0, 100) <= chance)
		{
			reelIn(FishingEndReason.WIN, true);
		}
		else
		{
			reelIn(FishingEndReason.LOSE, true);
		}
	}
	
	private void reelIn(FishingEndReason reason, boolean consumeBait)
	{
		if (!_isFishing)
		{
			return;
		}
		
		cancelTasks();
		
		try
		{
			final L2ItemInstance bait = _player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
			if (consumeBait)
			{
				if ((bait == null) || !_player.getInventory().updateItemCount(null, bait, -1, _player, null))
				{
					reason = FishingEndReason.LOSE; // no bait - no reward
					return;
				}
			}
			
			if ((reason == FishingEndReason.WIN) && (bait != null))
			{
				final FishingBaitData baitData = FishingData.getInstance().getBaitData(bait.getId());
				final int numRewards = baitData.getRewards().size();
				if (numRewards > 0)
				{
					final FishingData fishingData = FishingData.getInstance();
					final int lvlModifier = _player.getLevel() * _player.getLevel();
					_player.addExpAndSp(Rnd.get(fishingData.getExpRateMin(), fishingData.getExpRateMax()) * lvlModifier, Rnd.get(fishingData.getSpRateMin(), fishingData.getSpRateMax()) * lvlModifier, true);
					final int fishId = baitData.getRewards().get(Rnd.get(0, numRewards - 1));
					_player.getInventory().addItem("Fishing Reward", fishId, 1, _player, null);
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
					msg.addItemName(fishId);
					_player.sendPacket(msg);
				}
				else
				{
					LOGGER.log(Level.WARNING, "Could not find fishing rewards for bait ", bait.getId());
				}
			}
			else if (reason == FishingEndReason.LOSE)
			{
				_player.sendPacket(SystemMessageId.THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
			}
			
			if (consumeBait)
			{
				EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFishing(_player, reason), _player);
			}
		}
		finally
		{
			_player.broadcastPacket(new ExFishingEnd(_player, reason));
			_player.sendPacket(new ExUserInfoFishing(_player, false));
		}
	}
	
	public void stopFishing()
	{
		stopFishing(FishingEndType.PLAYER_STOP);
	}
	
	public synchronized void stopFishing(FishingEndType endType)
	{
		if (_isFishing)
		{
			reelIn(FishingEndReason.STOP, false);
			_isFishing = false;
			switch (endType)
			{
				case PLAYER_STOP:
				{
					_player.sendPacket(SystemMessageId.YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING);
					break;
				}
				case PLAYER_CANCEL:
				{
					_player.sendPacket(SystemMessageId.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
					break;
				}
			}
		}
	}
	
	public ILocational getBaitLocation()
	{
		return _baitLocation;
	}
	
	private Location calculateBaitLocation()
	{
		// calculate a position in front of the player with a random distance
		final int distMin = FishingData.getInstance().getBaitDistanceMin();
		final int distMax = FishingData.getInstance().getBaitDistanceMax();
		int distance = Rnd.get(distMin, distMax);
		final double angle = Util.convertHeadingToDegree(_player.getHeading());
		final double radian = Math.toRadians(angle);
		final double sin = Math.sin(radian);
		final double cos = Math.cos(radian);
		int baitX = (int) (_player.getX() + (cos * distance));
		int baitY = (int) (_player.getY() + (sin * distance));
		
		// search for fishing zone
		L2FishingZone fishingZone = null;
		for (L2ZoneType zone : ZoneManager.getInstance().getZones(_player))
		{
			if (zone instanceof L2FishingZone)
			{
				fishingZone = (L2FishingZone) zone;
				break;
			}
		}
		// search for water zone
		L2WaterZone waterZone = null;
		for (L2ZoneType zone : ZoneManager.getInstance().getZones(baitX, baitY))
		{
			if (zone instanceof L2WaterZone)
			{
				waterZone = (L2WaterZone) zone;
				break;
			}
		}
		
		int baitZ = computeBaitZ(_player, baitX, baitY, fishingZone, waterZone);
		if (baitZ == Integer.MIN_VALUE)
		{
			_player.sendPacket(SystemMessageId.YOU_CAN_T_FISH_HERE);
			return null;
		}
		
		return new Location(baitX, baitY, baitZ);
	}
	
	/**
	 * Computes the Z of the bait.
	 * @param player the player
	 * @param baitX the bait x
	 * @param baitY the bait y
	 * @param fishingZone the fishing zone
	 * @param waterZone the water zone
	 * @return the bait z or {@link Integer#MIN_VALUE} when you cannot fish here
	 */
	private static int computeBaitZ(L2PcInstance player, int baitX, int baitY, L2FishingZone fishingZone, L2WaterZone waterZone)
	{
		if ((fishingZone == null))
		{
			return Integer.MIN_VALUE;
		}
		
		if ((waterZone == null))
		{
			return Integer.MIN_VALUE;
		}
		
		// always use water zone, fishing zone high z is high in the air...
		final int baitZ = waterZone.getWaterZ();
		
		// if (!GeoEngine.getInstance().canSeeTarget(player.getX(), player.getY(), player.getZ(), baitX, baitY, baitZ))
		//
		// return Integer.MIN_VALUE;
		// }
		
		if (GeoEngine.getInstance().hasGeo(baitX, baitY))
		{
			if (GeoEngine.getInstance().getHeight(baitX, baitY, baitZ) > baitZ)
			{
				return Integer.MIN_VALUE;
			}
			
			if (GeoEngine.getInstance().getHeight(baitX, baitY, player.getZ()) > baitZ)
			{
				return Integer.MIN_VALUE;
			}
		}
		
		return baitZ;
	}
}
