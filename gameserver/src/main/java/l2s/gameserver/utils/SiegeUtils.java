package l2s.gameserver.utils;

import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.Residence;

/**
 * @author VISTALL
 * @date 12:23/21.02.2011
 */
public class SiegeUtils
{
	public static final int MIN_CLAN_SIEGE_LEVEL = 3;

	public static void addSiegeSkills(Player character)
	{
		character.addSkill(SkillHolder.getInstance().getSkillEntry(19034, 1), false); // Печать Света
		character.addSkill(SkillHolder.getInstance().getSkillEntry(19035, 1), false); // Печать Тьмы
		character.addSkill(SkillHolder.getInstance().getSkillEntry(247, 1), false);

		if(character.getClan() != null && character.getClan().getCastle() != 0)
		{
			character.addSkill(SkillHolder.getInstance().getSkillEntry(844, 1), false);
			character.addSkill(SkillHolder.getInstance().getSkillEntry(845, 1), false);
		}
	}

	public static void removeSiegeSkills(Player character)
	{
		character.removeSkill(SkillHolder.getInstance().getSkillEntry(19034, 1), false); // Печать Света
		character.removeSkill(SkillHolder.getInstance().getSkillEntry(19035, 1), false); // Печать Тьмы
		character.removeSkill(SkillHolder.getInstance().getSkillEntry(247, 1), false);

		if(character.getClan() != null && character.getClan().getCastle() != 0)
		{
			character.removeSkill(SkillHolder.getInstance().getSkillEntry(844, 1), false);
			character.removeSkill(SkillHolder.getInstance().getSkillEntry(845, 1), false);
		}
	}

	public static boolean getCanRide()
	{
		for(Residence residence : ResidenceHolder.getInstance().getResidences())
			if(residence != null && residence.getSiegeEvent().isInProgress())
				return false;
		return true;
	}
}