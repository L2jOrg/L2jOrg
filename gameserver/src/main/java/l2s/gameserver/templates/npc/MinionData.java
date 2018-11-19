package l2s.gameserver.templates.npc;

import l2s.gameserver.templates.StatsSet;

public class MinionData
{
	private final int _npcId;
    private final int _minionAmount;
    private final int _respawnTime;

    private StatsSet _parameters = StatsSet.EMPTY;

    public MinionData(int npcId, String aiType, int minionAmount, int respawnTime)
    {
        _npcId = npcId;
        _minionAmount = minionAmount;
        _respawnTime = respawnTime;

        if(aiType != null)
        {
            _parameters = new StatsSet();
            _parameters.set("ai_type", aiType);
        }
    }

	public int getMinionId()
	{
        return _npcId;
	}

	public int getAmount()
	{
		return _minionAmount;
	}

    public int getRespawnTime()
    {
        return _respawnTime;
    }

    public StatsSet getParameters()
    {
        return _parameters;
    }
}