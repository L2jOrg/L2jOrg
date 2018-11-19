package l2s.gameserver.templates.skill.restoration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bonux
 */
public final class RestorationGroup
{
	private final double _chance;
	private final List<RestorationItem> _restorationItems;

	public RestorationGroup(double chance)
	{
		_chance = chance;
		_restorationItems = new ArrayList<RestorationItem>();
	}

	public double getChance()
	{
		return _chance;
	}

	public void addRestorationItem(RestorationItem item)
	{
		_restorationItems.add(item);
	}

	public List<RestorationItem> getRestorationItems()
	{
		return _restorationItems;
	}
}