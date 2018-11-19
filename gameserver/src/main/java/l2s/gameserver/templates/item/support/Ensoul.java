package l2s.gameserver.templates.item.support;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.skills.SkillEntry;

/**
 * @author Bonux
 **/
public class Ensoul
{
	private final int _id;
	private final int _itemId;
	private final List<SkillEntry> _skills = new ArrayList<SkillEntry>();

	public Ensoul(int id, int itemId)
	{
		_id = id;
		_itemId = itemId;
	}

	public int getId()
	{
		return _id;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public void addSkill(int id, int level)
	{
		SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(id, level);
		if(skillEntry != null)
			_skills.add(skillEntry);
	}

	public List<SkillEntry> getSkills()
	{
		return _skills;
	}
}