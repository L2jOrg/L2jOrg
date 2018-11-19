package l2s.gameserver.network.l2.s2c;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.QuestState;

public class GMViewQuestInfoPacket extends L2GameServerPacket
{
	private final String _characterName;
	private final TIntIntMap _quests = new TIntIntHashMap();

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
		writeS(_characterName);
		writeH(_quests.size());
		for(TIntIntIterator iterator = _quests.iterator(); iterator.hasNext();)
		{
			iterator.advance();

			writeD(iterator.key());
			writeD(iterator.value());
		}
		writeH(0); //количество элементов типа: ddQd , как-то связано с предметами
	}
}