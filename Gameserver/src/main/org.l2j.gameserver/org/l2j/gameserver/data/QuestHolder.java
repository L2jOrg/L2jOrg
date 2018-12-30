package org.l2j.gameserver.data;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.quest.Quest;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

import java.util.Collection;

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

	private IntObjectMap<Quest> _quests = new HashIntObjectMap<Quest>();

	public Quest getQuest(int id)
	{
		return _quests.get(id);
	}

	public void addQuest(Quest quest)
	{
		if(_quests.containsKey(quest.getId()))
		{
			logger.warn("Cannot added quest (ID[" + quest.getId() + "], CLASS[" + quest.getClass().getSimpleName() + ".java]). Quets with this ID already have!");
			return;
		}
		_quests.put(quest.getId(), quest);
	}

	public Collection<Quest> getQuests()
	{
		return _quests.values();
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