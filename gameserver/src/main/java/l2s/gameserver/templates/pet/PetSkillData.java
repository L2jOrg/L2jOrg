package l2s.gameserver.templates.pet;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Skill;

public class PetSkillData
{
	private final int _id;
	private final int _level;
	private final int _petMinLevel;

	public PetSkillData(int id, int level, int petMinLevel)
	{
		_id = id;
		_level = level;
		_petMinLevel = petMinLevel;
	}

	public int getId()
	{
		return _id;
	}

	public int getLevel(int petLevel)
	{
		// TODO: Избавиться от хардкода.
		if(_level <= 0)
		{
			int level = 0;
			if(petLevel < 70)
			{
				level = petLevel / 10;
				if(level <= 0)
					level = 1;
			}
			else
				level = 7 + (petLevel - 70) / 5;

			// formula usable for skill that have 10 or more skill levels
			Skill skill = SkillHolder.getInstance().getSkill(getId(), 1);
			if(skill == null)
				return 0;

			return Math.min(level, skill.getMaxLevel());
		}
		return _level;
	}

	public int getLevel()
	{
		return _level;
	}

	public int getPetMinLevel()
	{
		return _petMinLevel;
	}
}