package l2s.gameserver.utils;

import java.util.Collection;
import java.util.StringTokenizer;

import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.htm.HtmTemplates;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.AcquireSkillDonePacket;
import l2s.gameserver.network.l2.s2c.ExAcquirableSkillListByClass;

public final class MulticlassUtils
{
	public static void onBypass(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("list"))
		{
			if(st.hasMoreTokens())
			{
				int raceId = Integer.parseInt(st.nextToken());
				Race race = Race.VALUES[raceId];
				if(st.hasMoreTokens())
				{
					int classTypeId = Integer.parseInt(st.nextToken());
					ClassType classType = ClassType.VALUES[classTypeId];
					showMulticlassList(player, race, classType);
				}
				else
					showMulticlassList(player, race);
			}
			else
				showMulticlassList(player);
		}
		else if(cmd.equalsIgnoreCase("learn"))
		{
			if(!st.hasMoreTokens())
				return;

			int id = Integer.parseInt(st.nextToken());
			ClassId classId = ClassId.VALUES[id];
			if(!checkMulticlass(player.getClassId(), classId))
				return;

			showMulticlassAcquireList(player, classId);
		}
	}

	private static void showMulticlassList(Player player, Race race, ClassType classType)
	{
		if(!Config.MULTICLASS_SYSTEM_ENABLED)
			return;

		HtmTemplates tpls = HtmCache.getInstance().getTemplates("custom/multiclass.htm", player);
		String html = tpls.get(0);

		ClassId playerClassId = player.getClassId();
		boolean showByClassType = (race == Race.HUMAN) && (playerClassId.getClassLevel().ordinal() >= ClassLevel.SECOND.ordinal());

		String backButton = "";

		StringBuilder content = new StringBuilder();
		if(race == null)
		{
			for(Race r : Race.VALUES)
			{
				String tempClassButton = tpls.get(1);
				tempClassButton = tempClassButton.replace("<?bypass?>", "list_" + r.ordinal());
				tempClassButton = tempClassButton.replace("<?image?>", tpls.get(2).replace("<?image_mark?>", r.toString()));
				tempClassButton = tempClassButton.replace("<?button_name?>", r.getName(player));
				content.append(tempClassButton);
			}
		}
		else if(classType == null && showByClassType)
		{
			for(ClassType c : ClassType.VALUES)
			{
				for(ClassId classId : ClassId.VALUES)
				{
					if(classId.isDummy())
						continue;

					if(!classId.isOfRace(race))
						continue;

					if(!classId.isOfType(c))
						continue;

					if(!checkMulticlass(playerClassId, classId))
						continue;

					String tempClassButton = tpls.get(1);
					tempClassButton = tempClassButton.replace("<?bypass?>", "list_" + race.ordinal() + "_" + c.ordinal());
					if(classId.getClassLevel() == ClassLevel.NONE)
						tempClassButton = tempClassButton.replace("<?image?>", tpls.get(2).replace("<?image_mark?>", classId.getRace().toString()));
					else
						tempClassButton = tempClassButton.replace("<?image?>", tpls.get(2).replace("<?image_mark?>", String.valueOf(classId.getId())));
					tempClassButton = tempClassButton.replace("<?button_name?>", c.getName(player));
					content.append(tempClassButton);
					break;
				}
			}
			backButton = tpls.get(3);
			backButton = backButton.replace("<?bypass?>", "list");
		}
		else
		{
			for(ClassId classId : ClassId.VALUES)
			{
				if(classId.isDummy())
					continue;

				if(!classId.isOfRace(race))
					continue;

				if(classType != null && showByClassType && !classId.isOfType(classType))
					continue;

				if(!checkMulticlass(playerClassId, classId))
					continue;

				String tempClassButton = tpls.get(1);
				tempClassButton = tempClassButton.replace("<?bypass?>", "learn_" + classId.getId());
				if(classId.getClassLevel() == ClassLevel.NONE)
					tempClassButton = tempClassButton.replace("<?image?>", tpls.get(2).replace("<?image_mark?>", classId.getRace().toString()));
				else
					tempClassButton = tempClassButton.replace("<?image?>", tpls.get(2).replace("<?image_mark?>", String.valueOf(classId.getId())));
				tempClassButton = tempClassButton.replace("<?button_name?>", classId.getName(player));
				content.append(tempClassButton);
			}
			backButton = tpls.get(3);
			if(classType != null && showByClassType)
				backButton = backButton.replace("<?bypass?>", "list_" + race.ordinal());
			else
				backButton = backButton.replace("<?bypass?>", "list");
		}
		content.append(backButton);

		html = html.replace("<?content?>", content.toString());

		HtmlUtils.sendHtm(player, html);
	}

	private static void showMulticlassList(Player player, Race race)
	{
		showMulticlassList(player, race, null);
	}

	public static void showMulticlassList(Player player)
	{
		showMulticlassList(player, null, null);
	}

	public static void showMulticlassAcquireList(Player player, ClassId classId)
	{
		Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, classId, AcquireType.MULTICLASS, null);

		ExAcquirableSkillListByClass asl = new ExAcquirableSkillListByClass(AcquireType.MULTICLASS, skills.size());

		for(SkillLearn s : skills)
			asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getCost(), s.getMinLevel());

		if(skills.size() == 0)
		{
			player.sendPacket(AcquireSkillDonePacket.STATIC);
			player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
		}
		else
		{
			player.setSelectedMultiClassId(classId);
			player.sendPacket(asl);
		}

		player.sendActionFailed();
	}

	public static boolean checkMulticlass(ClassId playerClassId, ClassId multiClassId)
	{
		if(playerClassId == multiClassId)
			return false;

		if(multiClassId.isDummy())
			return false;

		if(!playerClassId.isOfLevel(multiClassId.getClassLevel()))
			return false;

		return true;
	}
}