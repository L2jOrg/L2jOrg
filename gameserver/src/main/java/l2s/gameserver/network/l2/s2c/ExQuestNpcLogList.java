package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.model.quest.QuestNpcLogInfo;
import l2s.gameserver.model.quest.QuestState;

/**
 * @author VISTALL
 * @date 14:50/26.02.2011
 */
public class ExQuestNpcLogList extends L2GameServerPacket
{
	private int _questId;
	private List<int[]> _logList = Collections.emptyList();

	public ExQuestNpcLogList(QuestState state)
	{
		_questId = state.getQuest().getId();
		int cond = state.getCond();

		_logList = new ArrayList<int[]>();

		List<QuestNpcLogInfo> vars = state.getQuest().getNpcLogList(cond);
		if(vars != null)
		{
			for(QuestNpcLogInfo entry : vars)
			{
				int[] i = new int[3];
				int npcStringId = entry.getNpcStringId();
				if(npcStringId == 0)
				{
					i[0] = entry.getNpcIds()[0] + 1000000;
					i[1] = 0;
				}
				else
				{
					i[0] = npcStringId;
					i[1] = 1;
				}
				i[2] = state.getInt(entry.getVarName());
				_logList.add(i);
			}
		}

		vars = state.getQuest().getItemsLogList(cond);
		if(vars != null)
		{
			for(QuestNpcLogInfo entry : vars)
			{
				int[] i = new int[3];
				int npcStringId = entry.getNpcStringId();
				if(npcStringId == 0)
				{
					i[0] = entry.getNpcIds()[0];
					i[1] = 0;
				}
				else
				{
					i[0] = npcStringId;
					i[1] = 1;
				}

				for(int itemId : entry.getNpcIds())
					i[2] += (int) state.getQuestItemsCount(itemId);
				i[2] = Math.min(i[2], entry.getMaxCount());

				_logList.add(i);
			}
		}

		vars = state.getQuest().getCustomLogList(cond);
		if(vars != null)
		{
			for(QuestNpcLogInfo entry : vars)
			{
				int npcStringId = entry.getNpcStringId();
				if(npcStringId == 0)
					continue;

				int[] i = new int[3];
				i[0] = npcStringId;
				i[1] = 1;
				i[2] = state.getInt(entry.getVarName());

				_logList.add(i);
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		writeD(_questId);
		writeC(_logList.size());
		for(int i = 0; i < _logList.size(); i++)
		{
			int[] values = _logList.get(i);
			writeD(values[0]);
			writeC(values[1]);
			writeD(values[2]);
		}
	}
}
