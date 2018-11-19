package l2s.gameserver.model.pledge;

public class RankPrivs
{
	private int _rank;
	private int _party;
	private int _privs;

	public RankPrivs(int rank, int party, int privs)
	{
		_rank = rank;
		_party = party;
		_privs = privs;
	}

	public int getRank()
	{
		return _rank;
	}

	public int getParty()
	{
		return _party;
	}

	public void setParty(int party)
	{
		_party = party;
	}

	public int getPrivs()
	{
		return _privs;
	}

	public void setPrivs(int privs)
	{
		_privs = privs;
	}
}