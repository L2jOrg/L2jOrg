package org.l2j.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.fish.FishRewardsTemplate;
import org.l2j.gameserver.templates.fish.LureTemplate;
import org.l2j.gameserver.templates.fish.RodTemplate;

/**
 * @author Bonux
 **/
public class FishDataHolder extends AbstractHolder
{
	private static final FishDataHolder _instance = new FishDataHolder();

	private final TIntObjectMap<LureTemplate> _lures = new TIntObjectHashMap<LureTemplate>();
	private final TIntObjectMap<FishRewardsTemplate> _rewards = new TIntObjectHashMap<FishRewardsTemplate>();
	private final TIntObjectMap<RodTemplate> _rods = new TIntObjectHashMap<RodTemplate>();

	public static FishDataHolder getInstance()
	{
		return _instance;
	}

	public void addLure(LureTemplate lure)
	{
		_lures.put(lure.getId(), lure);
	}

	public LureTemplate getLure(int id)
	{
		return _lures.get(id);
	}

	public void addRewards(FishRewardsTemplate rewards)
	{
		_rewards.put(rewards.getType(), rewards);
	}

	public FishRewardsTemplate getRewards(int type)
	{
		return _rewards.get(type);
	}

	public void addRod(RodTemplate rod)
	{
		_rods.put(rod.getId(), rod);
	}

	public RodTemplate getRod(int id)
	{
		return _rods.get(id);
	}

	@Override
	public void log()
	{
		logger.info("load " + _lures.size() + " lure(s).");
		logger.info("load " + _rewards.size() + " lure reward(s).");
		logger.info("load " + _rods.size() + " rod(s).");
	}

	//@Deprecated
	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		_lures.clear();
		_rewards.clear();
		_rods.clear();
	}
}