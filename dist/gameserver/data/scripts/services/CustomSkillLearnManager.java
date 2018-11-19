package services;

import java.util.Collection;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.listener.actor.player.OnLearnCustomSkillListener;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.AcquireSkillDonePacket;
import l2s.gameserver.network.l2.s2c.ExAcquirableSkillListByClass;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.item.data.ItemData;
import l2s.gameserver.utils.ItemFunctions;

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