package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.HeroDiary;
import l2s.gameserver.templates.npc.NpcTemplate;

public class BossInstance extends RaidBossInstance
{
	private static final long serialVersionUID = 1L;

	private boolean _teleportedToNest;

	/**
	 * Constructor<?> for L2BossInstance. This represent all grandbosses:
	 * <ul>
	 * <li>29001	Queen Ant</li>
	 * <li>29014	Orfen</li>
	 * <li>29019	Antharas</li>
	 * <li>29020	Baium</li>
	 * <li>29022	Zaken</li>
	 * <li>29028	Valakas</li>
	 * <li>29006	Core</li>
	 * </ul>
	 * <br>
	 * <b>For now it's nothing more than a L2Monster but there'll be a scripting<br>
	 * engine for AI soon and we could add special behaviour for those boss</b><br>
	 * <br>
	 * @param objectId ID of the instance
	 * @param template L2NpcTemplate of the instance
	 */
	public BossInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
    public double getRewardRate(Player player)
    {
        return Config.RATE_DROP_ITEMS_BOSS;
    }

    @Override
    public double getDropChanceMod(Player player)
    {
        return Config.DROP_CHANCE_MODIFIER_BOSS;
    }

	@Override
	public boolean isBoss()
	{
		return true;
	}

	@Override
	public final boolean isMovementDisabled()
	{
		// Core should stay anyway
		return getNpcId() == 29006 || super.isMovementDisabled();
	}

	@Override
	protected void onDeath(Creature killer)
	{
		if(killer != null && killer.isPlayable())
		{
			Player player = killer.getPlayer();
			if(player.isInParty())
			{
				for(Player member : player.getParty().getPartyMembers())
					if(member.isHero())
						Hero.getInstance().addHeroDiary(member.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
			}
			else if(player.isHero())
				Hero.getInstance().addHeroDiary(player.getObjectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
		}
		super.onDeath(killer);
	}

	/**
	 * Used by Orfen to set 'teleported' flag, when hp goes to <50%
	 * @param flag
	 */
	public void setTeleported(boolean flag)
	{
		_teleportedToNest = flag;
	}

	public boolean isTeleported()
	{
		return _teleportedToNest;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}