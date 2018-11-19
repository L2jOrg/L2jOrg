package l2s.gameserver.templates;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.StatTemplate;

/**
 * @author VISTALL
 * @date 19:17/19.05.2011
 */
public class OptionDataTemplate extends StatTemplate
{
	private final List<SkillEntry> _skills = new ArrayList<SkillEntry>(0);
	private final int _id;

	public OptionDataTemplate(int id)
	{
		_id = id;
	}

	public void addSkill(SkillEntry skill)
	{
		_skills.add(skill);
	}

	public List<SkillEntry> getSkills()
	{
		return _skills;
	}

	public int getId()
	{
		return _id;
	}
}