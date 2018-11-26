package org.l2j.gameserver.data.xml.holder;

import java.util.Collection;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.item.data.AttendanceRewardData;

public final class AttendanceRewardHolder extends AbstractHolder
{
	private static final AttendanceRewardHolder _instance = new AttendanceRewardHolder();

	private final TIntObjectMap<AttendanceRewardData> _normalRewards = new TIntObjectHashMap<>();
	private final TIntObjectMap<AttendanceRewardData> _premiumRewards = new TIntObjectHashMap<>();

	public static AttendanceRewardHolder getInstance()
	{
		return _instance;
	}

	public void addNormalReward(AttendanceRewardData reward)
	{
		_normalRewards.put(_normalRewards.size() + 1, reward);
	}

	public void addPremiumReward(AttendanceRewardData reward)
	{
		_premiumRewards.put(_premiumRewards.size() + 1, reward);
	}

	public Collection<AttendanceRewardData> getRewards(boolean premium)
	{
		return premium ? _premiumRewards.valueCollection() : _normalRewards.valueCollection();
	}

	public AttendanceRewardData getReward(int index, boolean premium)
	{
		return premium ? _premiumRewards.get(index) : _normalRewards.get(index);
	}

	@Override
	public int size()
	{
		return _normalRewards.size() + _premiumRewards.size();
	}

	@Override
	public void clear()
	{
		_normalRewards.clear();
		_premiumRewards.clear();
	}
}