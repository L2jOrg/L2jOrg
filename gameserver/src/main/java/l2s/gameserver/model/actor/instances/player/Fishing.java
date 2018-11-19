package l2s.gameserver.model.actor.instances.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.FishDataHolder;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAutoFishAvailable;
import l2s.gameserver.network.l2.s2c.ExFishingEndPacket;
import l2s.gameserver.network.l2.s2c.ExUserInfoFishing;
import l2s.gameserver.templates.fish.FishRewardTemplate;
import l2s.gameserver.templates.fish.FishRewardsTemplate;
import l2s.gameserver.templates.fish.FishTemplate;
import l2s.gameserver.templates.fish.LureTemplate;
import l2s.gameserver.templates.fish.RodTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

public class Fishing
{
	private class FishingTask extends RunnableImpl
	{
		private final FishTemplate _fish;

		public FishingTask(FishTemplate fish)
		{
			_fish = fish;
		}

		@Override
		public void runImpl()
		{
			_inProcess = false;

			if(_fish.getId() == -1)
			{
				_owner.sendPacket(SystemMsg.THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY);
				_owner.broadcastPacket(new ExUserInfoFishing(_owner));
				_owner.broadcastPacket(new ExFishingEndPacket(_owner, ExFishingEndPacket.FAIL));
				_owner.getListeners().onFishing(false);
			}
			else
			{
				FishRewardsTemplate rewards = FishDataHolder.getInstance().getRewards(_fish.getRewardType());
				if(rewards != null)
				{
					long exp = 0L;
					long sp = 0L;
					for(FishRewardTemplate reward : rewards.getRewards())
					{
						if(_owner.getLevel() >= reward.getMinLevel() && _owner.getLevel() <= reward.getMaxLevel())
						{
							exp += reward.getExp();
							sp += reward.getSp();
						}
					}

					// TODO: Проверить, влияет ли виталити и прочие бонусы на оффе.
					exp = (long) (exp * _rod.getRewardModifier() * Config.RATE_XP_BY_LVL[_owner.getLevel()]);
					sp = (long) (sp * _rod.getRewardModifier() * Config.RATE_SP_BY_LVL[_owner.getLevel()]);

					_owner.addExpAndSp(exp, sp, 0, 0, false, false, false, false, true);
				}

				ItemFunctions.addItem(_owner, _fish.getId(), Config.RATE_FISH_DROP_COUNT * 1, true);

				_owner.broadcastPacket(new ExUserInfoFishing(_owner));
				_owner.broadcastPacket(new ExFishingEndPacket(_owner, ExFishingEndPacket.WIN));
				_owner.getListeners().onFishing(true);
			}
			_processTask = ThreadPoolManager.getInstance().schedule(new ThrowHookTask(), 15000L);
		}
	}

	private class ThrowHookTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(!throwHook(false))
				stop();
		}
	}

	private static final int MIN_BAIT_DISTANCE = 90;
	private static final int MAX_BAIT_DISTANCE = 250;

	private final Player _owner;

	private boolean _started = false;
	private boolean _inProcess = false;
	private Location _hookLoc = new Location();

	private RodTemplate _rod = null;
	private LureTemplate _lure = null;

	private ScheduledFuture<?> _processTask = null;

	public Fishing(Player owner)
	{
		_owner = owner;
	}

	public boolean inStarted()
	{
		return _started;
	}

	public boolean isInProcess()
	{
		return _inProcess;
	}

	public Location getHookLocation()
	{
		return _hookLoc;
	}

	public void start(RodTemplate rod, LureTemplate lure)
	{
		_started = true;
		_rod = rod;
		_lure = lure;

		_owner.sendPacket(SystemMsg.YOU_CAST_YOUR_LINE_AND_START_TO_FISH);

		throwHook(true);
	}

	public void stop()
	{
		_started = false;
		_inProcess = false;
		_hookLoc = new Location();
		_rod = null;
		_lure = null;

		stopProcessTask();

		_owner.sendPacket(SystemMsg.YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING);
		_owner.sendPacket(ExAutoFishAvailable.REMOVE);
		_owner.broadcastPacket(new ExUserInfoFishing(_owner));
		_owner.broadcastPacket(new ExFishingEndPacket(_owner, ExFishingEndPacket.CANCELED));
	}

	private boolean throwHook(boolean start)
	{
		WeaponTemplate weaponItem = _owner.getActiveWeaponTemplate();
		if(weaponItem == null || weaponItem.getItemType() != WeaponTemplate.WeaponType.ROD)
			return false;

		if(_rod.getId() != weaponItem.getItemId())
			return false;

		ItemInstance lureItem = _owner.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
		if(lureItem == null || lureItem.getCount() < _rod.getShotConsumeCount())
		{
			_owner.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_BAIT);
			return false;
		}

		if(_lure.getId() != lureItem.getItemId())
		{
			_owner.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_BAIT);
			return false;
		}

		List<FishTemplate> fishes = _lure.getFishes();
		if(fishes.isEmpty())
			return false;

		if(!ItemFunctions.deleteItem(_owner, lureItem, _rod.getShotConsumeCount(), false))
		{
			_owner.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_BAIT);
			return false;
		}

		double shotPower = 1 + (_owner.getChargedFishshotPower() / 100.);

		double chancesAmount = 0;
		for(FishTemplate fish : fishes)
			chancesAmount += fish.getChance() / (fish.getId() == -1 ? shotPower : 1);

		double chanceMod = (100. - chancesAmount) / fishes.size();
		List<FishTemplate> successFishes = new ArrayList<FishTemplate>();
		int tryCount = 0;
		while(successFishes.isEmpty())
		{
			tryCount++;
			for(FishTemplate fish : fishes)
			{
				if((tryCount % 10) == 0) //Немного теряем шанс, но зато зацикливания будут меньше.
					chanceMod += 1.;
				if(Rnd.chance((fish.getChance() / (fish.getId() == 0 ? shotPower : 1)) + chanceMod))
					successFishes.add(fish);
			}
		}
		FishTemplate fish = Rnd.get(successFishes);
		if(fish == null) // В принципе не может произойти.
			return false;

		_hookLoc = findHookLocation();

		if(_hookLoc == null)
			return false;

		if(!start)
			_owner.unChargeFishShot();

		stopProcessTask();

		_owner.sendPacket(ExAutoFishAvailable.FISHING);
		_owner.broadcastPacket(new ExUserInfoFishing(_owner));

		_inProcess = true;
		_processTask = ThreadPoolManager.getInstance().schedule(new FishingTask(fish), (long) (fish.getDuration() * _rod.getDurationModifier() * 1000));
		return true;
	}

	private void stopProcessTask()
	{
		if(_processTask != null)
		{
			_processTask.cancel(false);
			_processTask = null;
		}
	}

	public Location findHookLocation()
	{
		int distance = Rnd.get(MIN_BAIT_DISTANCE, MAX_BAIT_DISTANCE);
		final double angle = PositionUtils.convertHeadingToDegree(_owner.getHeading());
		final double radian = Math.toRadians(angle);
		final double sin = Math.sin(radian);
		final double cos = Math.cos(radian);
		int baitX = (int) (_owner.getX() + (cos * distance));
		int baitY = (int) (_owner.getY() + (sin * distance));
		int baitZ = (int) (_owner.getZ() + _owner.getCollisionHeight() + 50);

		Set<Zone> zones = new HashSet<Zone>();

		if(GeoEngine.canSeeCoord(_owner, baitX, baitY, baitZ, false))
		{
			World.getZones(zones, baitX, baitY, _owner.getReflection());
			for(Zone zone : zones)
			{
				if(zone.getType() == ZoneType.water)
				{
					int waterZ = zone.getTerritory().getZmax();
					if(!GeoEngine.canSeeCoord(baitX, baitY, baitZ, baitX, baitY, waterZ, false, _owner.getGeoIndex()))
						continue;

					return new Location(baitX, baitY, waterZ);
				}
			}
		}
		for(distance = MAX_BAIT_DISTANCE; distance >= MIN_BAIT_DISTANCE; --distance)
		{
			baitX = (int) (_owner.getX() + cos * distance);
			baitY = (int) (_owner.getY() + sin * distance);

			if(GeoEngine.canSeeCoord(_owner, baitX, baitY, baitZ, false))
			{
				zones.clear();

				World.getZones(zones, baitX, baitY, _owner.getReflection());
				for(Zone zone : zones)
				{
					if(zone.getType() == ZoneType.water)
					{
						int waterZ = zone.getTerritory().getZmax();
						if(!GeoEngine.canSeeCoord(baitX, baitY, baitZ, baitX, baitY, waterZ, false, _owner.getGeoIndex()))
							continue;

						return new Location(baitX, baitY, waterZ);
					}
				}
			}
		}
		return null;
	}
}