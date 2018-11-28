package services;

import org.l2j.gameserver.data.xml.holder.SkillAcquireHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.handler.bypass.Bypass;
import org.l2j.gameserver.listener.actor.player.OnLearnCustomSkillListener;
import org.l2j.gameserver.listener.script.OnInitScriptListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.listener.CharListenerList;
import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.AcquireSkillDonePacket;
import org.l2j.gameserver.network.l2.s2c.ExAcquirableSkillListByClass;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.templates.item.data.ItemData;
import org.l2j.gameserver.utils.ItemFunctions;

import java.util.Collection;

/**
 * @author Bonux
**/
public class CustomSkillLearnManager implements OnInitScriptListener
{
	private class LearnCustomSkillListener implements OnLearnCustomSkillListener
	{
		@Override
		public void onLearnCustomSkill(Player player, SkillLearn skillLearn)
		{
			final int skillLevel = player.getSkillLevel(skillLearn.getId(), 0);
			if(skillLevel != skillLearn.getLevel() - 1)
				return;

			SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(skillLearn.getId(), skillLearn.getLevel());
			if(skillEntry == null)
				return;

			if(player.getSp() < skillLearn.getCost())
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
				return;
			}

			player.getInventory().writeLock();
			try
			{
				for(ItemData item : skillLearn.getRequiredItemsForLearn(AcquireType.NORMAL))
				{
					if(!ItemFunctions.haveItem(player, item.getId(), item.getCount()))
						return;
				}

				for(ItemData item : skillLearn.getRequiredItemsForLearn(AcquireType.NORMAL))
					ItemFunctions.deleteItem(player, item.getId(), item.getCount(), true);
			}
			finally
			{
				player.getInventory().writeUnlock();
			}

			player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skillEntry.getId(), skillEntry.getLevel()));

			player.setSp(player.getSp() - skillLearn.getCost());
			player.addSkill(skillEntry, true);

			player.rewardSkills(false);

			player.sendUserInfo();
			player.updateStats();

			player.sendSkillList(skillEntry.getId());

			player.updateSkillShortcuts(skillEntry.getId(), skillEntry.getLevel());
			showAcquireList(player, null, null);
		}
	}

	@Override
	public void onInit()
	{
		CharListenerList.addGlobal(new LearnCustomSkillListener());
	}

	@Bypass("services.CustomSkillLearnManager:showAcquireList")
	public void showAcquireList(Player player, NpcInstance npc, String[] param)
	{
		final Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.CUSTOM);

		final ExAcquirableSkillListByClass asl = new ExAcquirableSkillListByClass(AcquireType.CUSTOM, skills.size());

		for(SkillLearn s : skills)
			asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getCost(), s.getMinLevel());

		if(skills.size() == 0)
		{
			player.sendPacket(AcquireSkillDonePacket.STATIC);
			player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
		}
		else
			player.sendPacket(asl);

		player.sendActionFailed();
	}
}