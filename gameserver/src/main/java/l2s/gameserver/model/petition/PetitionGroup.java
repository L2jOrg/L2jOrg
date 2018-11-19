package l2s.gameserver.model.petition;

import java.util.HashMap;
import java.util.Map;

import l2s.gameserver.utils.Language;

/**
 * @author VISTALL
 * @date 7:32/25.07.2011
 */
public abstract class PetitionGroup
{
	private final Map<Language, String> _name = new HashMap<Language, String>(Language.VALUES.length);
	private final Map<Language, String> _description = new HashMap<Language, String>(Language.VALUES.length);

	private final int _id;

	public PetitionGroup(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public String getName(Language lang)
	{
		return _name.get(lang);
	}

	public void setName(Language lang, String name)
	{
		_name.put(lang, name);
	}

	public String getDescription(Language lang)
	{
		return _description.get(lang);
	}

	public void setDescription(Language lang, String name)
	{
		_description.put(lang, name);
	}
}