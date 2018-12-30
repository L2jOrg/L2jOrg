package org.l2j.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.skills.SkillEntryType;
import org.l2j.gameserver.utils.SkillUtils;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
**/
public final class SkillHolder extends AbstractHolder
{
	private static final SkillHolder _instance = new SkillHolder();

	private final IntObjectMap<SkillEntry> _skills = new HashIntObjectMap<SkillEntry>();
	private final IntObjectMap<SkillEntry> _skillsByIndex = new HashIntObjectMap<SkillEntry>();
	private final IntObjectMap<List<SkillEntry>> _skillsById = new HashIntObjectMap<List<SkillEntry>>();

	public static SkillHolder getInstance()
	{
		return _instance;
	}

	public void addSkill(Skill skill)
	{
		SkillEntry skillEntry = new SkillEntry(SkillEntryType.NONE, skill);

		_skills.put(skillEntry.hashCode(), skillEntry);

		List<SkillEntry> skills = _skillsById.get(skillEntry.getId());
		if(skills == null)
		{
			skills = new ArrayList<SkillEntry>();
			_skillsById.put(skillEntry.getId(), skills);
		}
		skills.add(skillEntry);

		_skillsByIndex.put(SkillUtils.generateSkillHashCode(skillEntry.getId(), skills.size()), skillEntry);
	}

	public SkillEntry getSkillEntry(int id, int level)
	{
		return _skills.get(SkillUtils.generateSkillHashCode(id, level));
	}

	public SkillEntry getSkillEntryByIndex(int id, int index)
	{
		return _skillsByIndex.get(SkillUtils.generateSkillHashCode(id, index));
	}

	public Skill getSkill(int id, int level)
	{
		SkillEntry skillEntry = getSkillEntry(id, level);
		if(skillEntry != null)
			return skillEntry.getTemplate();
		return null;
	}

	public List<SkillEntry> getSkills(int id)
	{
		return _skillsById.get(id);
	}

	@Override
	public int size()
	{
		return _skills.size();
	}

	@Override
	public void clear()
	{
		_skills.clear();
		_skillsByIndex.clear();
		_skillsById.clear();
	}
}