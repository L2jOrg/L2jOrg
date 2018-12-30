package org.l2j.gameserver.templates.dailymissions;

import io.github.joealisson.primitive.sets.IntSet;
import org.l2j.gameserver.templates.item.data.ItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bonux
 **/
public class DailyRewardTemplate
{
	private final IntSet _classIds;
	private final List<ItemData> _rewardItems = new ArrayList<ItemData>();

	public DailyRewardTemplate(IntSet classIds)
	{
		_classIds = classIds;
	}

	public boolean containsClassId(int classId)
	{
		if(_classIds == null)
			return true;
		return _classIds.contains(classId);
	}

	public void addRewardItem(ItemData item)
	{
		_rewardItems.add(item);
	}

	public ItemData[] getRewardItems()
	{
		return _rewardItems.toArray(new ItemData[_rewardItems.size()]);
	}
}