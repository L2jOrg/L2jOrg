package l2s.gameserver.model.quest;

/**
* @author VISTALL
* @date 15:30/26.02.2011
*/
public class QuestNpcLogInfo
{
	private final int[] _npcIds;
	private final String _varName;
	private final int _maxCount;
	private final int _npcStringId;

	public QuestNpcLogInfo(int[] npcIds, String varName, int maxCount, int npcStringId)
	{
		_npcIds = npcIds;
		_varName = varName;
		_maxCount = maxCount;
		_npcStringId = npcStringId;
	}

	public int[] getNpcIds()
	{
		return _npcIds;
	}

	public String getVarName()
	{
		return _varName;
	}

	public int getMaxCount()
	{
		return _maxCount;
	}

	public int getNpcStringId()
	{
		return _npcStringId;
	}
}