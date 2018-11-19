package l2s.gameserver.network.l2.s2c;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.QuestRepeatType;
import l2s.gameserver.model.quest.QuestState;

/**
 * format: h[dd]b
 */
public class QuestListPacket extends L2GameServerPacket
{
	/**
	 * This text was wrote by XaKa
	 * QuestList packet structure:
	 * {
	 * 		1 byte - 0x80
	 * 		2 byte - Number of Quests
	 * 		for Quest in AvailibleQuests
	 * 		{
	 * 			4 byte - Quest ID
	 * 			4 byte - Quest Status
	 * 		}
	 * }
	 *
	 * NOTE: The following special constructs are true for the 4-byte Quest Status:
	 * If the most significant bit is 0, this means that no progress-step got skipped.
	 * In this case, merely passing the rank of the latest step gets the client to mark
	 * it as current and mark all previous steps as complete.
	 * If the most significant bit is 1, it means that some steps may have been skipped.
	 * In that case, each bit represents a quest step (max 30) with 0 indicating that it was
	 * skipped and 1 indicating that it either got completed or is currently active (the client
	 * will automatically assume the largest step as active and all smaller ones as completed).
	 * For example, the following bit sequences will yield the same results:
	 * 1000 0000 0000 0000 0000 0011 1111 1111: Indicates some steps may be skipped but each of
	 * the first 10 steps did not get skipped and current step is the 10th.
	 * 0000 0000 0000 0000 0000 0000 0000 1010: Indicates that no steps were skipped and current is the 10th.
	 * It is speculated that the latter will be processed faster by the client, so it is preferred when no
	 * steps have been skipped.
	 * However, the sequence "1000 0000 0000 0000 0000 0010 1101 1111" indicates that the current step is
	 * the 10th but the 6th and 9th are not to be shown at all (not completed, either).
	 */

	private static byte[] _completedQuestsMask = new byte[128];

	private final TIntIntMap _quests = new TIntIntHashMap();

	public QuestListPacket(Player player)
	{
		for(QuestState quest : player.getAllQuestsStates())
			if(quest.getQuest().isVisible(player) && quest.isStarted())
				_quests.put(quest.getQuest().getId(), quest.getCondsMask());
			else if(quest.isCompleted() && quest.getQuest().getRepeatType() == QuestRepeatType.ONETIME)
			{
				int questId = quest.getQuest().getId();
				if (questId >= 10000)
					questId -= 10000;

				int byteIndex = questId / 8;
				_completedQuestsMask[byteIndex] |= 1 << (questId - (byteIndex * 8));
			}
	}

	@Override
	protected final void writeImpl()
	{
		writeH(_quests.size());
		for(TIntIntIterator iterator = _quests.iterator(); iterator.hasNext();)
		{
			iterator.advance();

			writeD(iterator.key());
			writeD(iterator.value());
		}
		writeB(_completedQuestsMask);
	}
}