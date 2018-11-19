package npc.model.residences.instantclanhall;

import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.residence.clanhall.InstantClanHall;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.templates.npc.NpcTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class InstantClanHallGatekeeperInstance extends NpcInstance
{
	private static final Logger _log = LoggerFactory.getLogger(InstantClanHallGatekeeperInstance.class);

	private static final long serialVersionUID = 1L;

	private static final int TELEPORT_SKILL_ID = 5109; // Производство - Врата Клана

	public InstantClanHallGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void altOnMagicUse(Creature aimingTarget, Skill skill)
	{
		super.altOnMagicUse(aimingTarget, skill);

		if(skill.getId() == TELEPORT_SKILL_ID && aimingTarget.isPlayer())
			aimingTarget.getPlayer().teleToClanhall();
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("instanthall"))
		{
			if(!st.hasMoreTokens())
				return;

			String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("enter"))
			{
				Clan clan = player.getClan();
				if(clan == null)
					showChatWindow(player, "residence2/instant_clanhall/AgitJanitorHi_01b.htm", false);
				else if(getInstantClanHall(player) != null)
				{
					Skill skill = SkillHolder.getInstance().getSkill(TELEPORT_SKILL_ID, 1);
					if(skill != null)
						altUseSkill(skill, player);
					else
						_log.warn("Cannot use skill ID[" + skill.getId() + "] LEVEL[" + skill.getLevel() + "]!");
				}
				else
					showChatWindow(player, "residence2/instant_clanhall/AgitJanitorHi_02.htm", false);
			}
			else if(cmd2.equalsIgnoreCase("info"))
			{
				Clan clan = player.getClan();
				if(clan == null)
					showChatWindow(player, "residence2/instant_clanhall/AgitJanitorHi_01b.htm", false);
				else if(getInstantClanHall(player) != null)
					showChatWindow(player, "residence2/instant_clanhall/AgitJanitorHi_01a.htm", false);
				else
					showChatWindow(player, "residence2/instant_clanhall/AgitJanitorHi_01b.htm", false);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
			showChatWindow(player, "residence2/instant_clanhall/AgitJanitorHi_01.htm", firstTalk);
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}

	private static InstantClanHall getInstantClanHall(Player player)
	{
		Clan clan = player.getClan();
		if(clan == null)
			return null;

		return ResidenceHolder.getInstance().getResidence(InstantClanHall.class, clan.getHasHideout());
	}
}
