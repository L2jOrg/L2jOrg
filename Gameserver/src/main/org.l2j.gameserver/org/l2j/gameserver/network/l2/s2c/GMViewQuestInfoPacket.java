package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.primitive.maps.IntIntMap;
import io.github.joealisson.primitive.maps.impl.HashIntIntMap;
import io.github.joealisson.primitive.pair.IntIntPair;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.quest.QuestState;

public class GMViewQuestInfoPacket extends L2GameServerPacket
{
	private final String _characterName;
	private final IntIntMap _quests = new HashIntIntMap();

	public GMViewQuestInfoPacket(Player targetCharacter)
	{
		_characterName = targetCharacter.getName();
		for(QuestState quest : targetCharacter.getAllQuestsStates())
			if(quest.getQuest().isVisible(targetCharacter) && quest.isStarted())
				_quests.put(quest.getQuest().getId(), quest.getCondsMask());
	}

	@Override
	protected final void writeImpl()
	{
		writeString(_characterName);
		writeShort(_quests.size());
		for (IntIntPair pair : _quests.entrySet()) {
			writeInt(pair.getKey());
			writeInt(pair.getValue());
		}
		writeShort(0); //количество элементов типа: ddQd , как-то связано с предметами
	}
}