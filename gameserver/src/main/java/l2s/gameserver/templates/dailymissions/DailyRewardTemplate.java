package l2s.gameserver.templates.dailymissions;

import gnu.trove.set.TIntSet;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.templates.item.data.ItemData;

/**
 * @author Bonux
 **/
public class DailyRewardTemplate
{
	private final TIntSet _classIds;
	private final List<ItemData> _rewardItems = new ArrayList<ItemData>();

	public DailyRewardTemplate(TIntSet classIds)
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