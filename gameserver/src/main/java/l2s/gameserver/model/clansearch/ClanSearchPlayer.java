package l2s.gameserver.model.clansearch;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.clansearch.base.ClanSearchListType;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ClanSearchPlayer
{
	private final int _charId;
	private final int _prefferedClanId;
	private final ClanSearchListType _searchType;
	private final String _desc;
	private String _charName;
	private int _charLevel;
	private int _charClassId;

	public ClanSearchPlayer(int charId, String charName, int charLevel, int charClassId, int prefferedClanId, ClanSearchListType searchType, String desc)
	{
		_charId = charId;
		_prefferedClanId = prefferedClanId;
		_searchType = searchType;
		_desc = desc;
		_charName = charName;
		_charLevel = charLevel;
		_charClassId = charClassId;
	}

	public ClanSearchPlayer(int charId, String charName, int charLevel, int charClassId, ClanSearchListType searchType)
	{
		this(charId, charName, charLevel, charClassId, -1, searchType, null);
	}

	public boolean isApplicant()
	{
		return _prefferedClanId > 0;
	}

	public int getCharId()
	{
		return _charId;
	}

	public int getPrefferedClanId()
	{
		return _prefferedClanId;
	}

	public ClanSearchListType getSearchType()
	{
		return _searchType;
	}

	public String getDesc()
	{
		return _desc;
	}

	public String getName()
	{
		Player player = World.getPlayer(_charId);

		if(player != null)
			_charName = player.getName();

		return _charName;
	}

	public int getLevel()
	{
		Player player = World.getPlayer(_charId);

		if(player != null)
			_charLevel = player.getLevel();

		return _charLevel;
	}

	public int getClassId()
	{
		Player player = World.getPlayer(_charId);

		if(player != null)
			_charClassId = player.getBaseClassId();

		return _charClassId;
	}
}