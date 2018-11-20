package org.l2j.gameserver.model.entity.events.actions;

import java.util.List;

import org.l2j.gameserver.data.xml.holder.NpcHolder;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.entity.events.EventAction;
import org.l2j.gameserver.model.reward.RewardGroup;
import org.l2j.gameserver.model.reward.RewardList;
import org.l2j.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 18:12/29.04.2012
 */
public class GlobalRewardListAction implements EventAction
{
	private final boolean _add;
	private final String _name;
	private final int _minLevel;
	private final int _maxLevel;

	public GlobalRewardListAction(boolean add, String name, int minLevel, int maxLevel)
	{
		_add = add;
		_name = name;
		_minLevel = minLevel;
		_maxLevel = maxLevel;
	}

	@Override
	public void call(Event event)
	{
		List<Object> list = event.getObjects(_name);

		for(NpcTemplate npc: NpcHolder.getInstance().getAll())
		{
			loop: if(npc != null && !npc.getRewards().isEmpty())
			{
				if(npc.level >= _minLevel && npc.level <= _maxLevel)
				{
					for(RewardList rl : npc.getRewards())
					{
						for(RewardGroup rg : rl)
						{
							if(!rg.isAdena())
							{
								for(Object o : list)
								{
									if(o instanceof RewardList)
									{
										if(_add)
											npc.addRewardList((RewardList) o);
										else
											npc.removeRewardList((RewardList) o);
									}
								}
								break loop;
							}
						}
					}
				}
			}
		}
	}
}