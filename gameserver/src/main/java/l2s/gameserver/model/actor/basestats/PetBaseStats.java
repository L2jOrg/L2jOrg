package l2s.gameserver.model.actor.basestats;

import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.stats.Stats;

/**
 * @author Bonux
**/
public class PetBaseStats extends PlayableBaseStats
{
	public PetBaseStats(PetInstance owner)
	{
		super(owner);
	}

	@Override
	public PetInstance getOwner()
	{
		return (PetInstance) _owner;
	}

	@Override
	public double getHpMax()
	{
		return getOwner().getData().getHP(getOwner().getLevel());
	}

	@Override
	public double getMpMax()
	{
		return getOwner().getData().getMP(getOwner().getLevel());
	}

	@Override
	public double getHpReg()
	{
		return getOwner().getData().getHPRegen(getOwner().getLevel());
	}

	@Override
	public double getMpReg()
	{
		return getOwner().getData().getMPRegen(getOwner().getLevel());
	}

	@Override
	public double getPAtk()
	{
		return getOwner().getData().getPAtk(getOwner().getLevel());
	}

	@Override
	public double getMAtk()
	{
		return getOwner().getData().getMAtk(getOwner().getLevel());
	}

	@Override
	public double getPDef()
	{
		return getOwner().getData().getPDef(getOwner().getLevel());
	}

	@Override
	public double getMDef()
	{
		return getOwner().getData().getMDef(getOwner().getLevel());
	}
}