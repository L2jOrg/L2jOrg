package l2s.gameserver.data;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.quest.Quest;

/**
 * @author Bonux
 **/
public final class QuestHolder extends AbstractHolder
{
	private static QuestHolder _instance = new QuestHolder();

	public static QuestHolder getInstance()
	{
		return _instance;
	}

	private TIntObjectMap<Quest> _quests = new TIntObjectHashMap<Quest>();

	public Quest getQuest(int id)
	{
		return _quests.get(id);
	}

	public void addQuest(Quest quest)
	{
		if(_quests.containsKey(quest.getId()))
		{
			warn("Cannot added quest (ID[" + quest.getId() + "], CLASS[" + quest.getClass().getSimpleName() + ".java]). Quets with this ID already have!");
			return;
		}
		_quests.put(quest.getId(), quest);
	}

	public Collection<Quest> getQuests()
	{
		return _quests.valueCollection();
	}

	@Override
	public int size()
	{
		return _quests.size();
	}

	@Override
	public void clear()
	{
		_quests.clear();
	}
}