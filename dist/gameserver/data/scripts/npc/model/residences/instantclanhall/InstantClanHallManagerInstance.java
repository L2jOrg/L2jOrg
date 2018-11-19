package npc.model.residences.instantclanhall;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ResidenceFunctionType;
import l2s.gameserver.model.entity.residence.clanhall.InstantClanHall;
import l2s.gameserver.model.entity.residence.ResidenceFunction;
import l2s.gameserver.model.instances.MerchantInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.SkillUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class InstantClanHallManagerInstance extends MerchantInstance
{
	private static final Logger _log = LoggerFactory.getLogger(InstantClanHallManagerInstance.class);

	private static final long serialVersionUID = 1L;

	private static final int CH_BLESSING_SKIL_ID_9 = 4375;
	private static final int TELEPORT_SKILL_ID = 5109; // Производство - Врата Клана

	private static final int Social_Reply_Timer_1 = 1000;
	private static final int Social_Reply_Timer_2 = 1001;
	private static final int Social_Reply_Timer_3 = 1002;
	private static final int Social_Reply_Timer_4 = 1003;
	private static final int instantAgit_StrotyTime = 1004;
	private static final int giran_tel_time = 1005;
	private static final int aden_tel_time = 1006;
	private static final int gludin_tel_time = 1007;

	public InstantClanHallManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onMenuSelect(Player player, int ask, long reply)
	{
		if(ask == 100)
		{
			if(reply == 1)
			{
				if(player.getClanId() == getOwnerId())
					showChatWindow(player, "residence2/instant_clanhall/agitbanish_in01.htm", false);
				else
					showChatWindow(player, "residence2/instant_clanhall/noauthority.htm", false);
			}
			if(reply == 2)
			{
				if(player.getClanId() == getOwnerId())
				{
					getResidence().banishForeigner(getOwnerId());
					showChatWindow(player, "residence2/instant_clanhall/agitbanish_in02.htm", false);
				}
				else
					showChatWindow(player, "residence2/instant_clanhall/noauthority.htm", false);
			}
		}
		else if(ask == 200)
		{
			if(reply == 1)
			{
				if(player.getClanId() == getOwnerId())
					showChatWindow(player, "residence2/instant_clanhall/agitwarehouse_inze.htm", false);
				else
					showChatWindow(player, "residence2/instant_clanhall/noauthority.htm", false);
			}
		}
		else if(ask == 300)
		{
			if(reply == 1)
			{
				if(player.getClanId() == getOwnerId())
					showShopWindow(player, 1, false); // Взымается ли налог?
				else
					showChatWindow(player, "residence2/instant_clanhall/noauthority.htm", false);
			}
		}
		else if(ask == 400)
		{
			if(reply == 1)
			{
				if(player.getClanId() == getOwnerId())
					showChatWindow(player, "residence2/instant_clanhall/agitbuff_in.htm", false, "<?MPLeft?>", (int) getCurrentMp());
				else
					showChatWindow(player, "residence2/instant_clanhall/noauthority.htm", false);
			}
		}
		else if(ask == 401)
		{
			String html = null;
			if(player.getClanId() == getOwnerId())
			{
				Skill skill = SkillHolder.getInstance().getSkill(SkillUtils.getSkillIdFromPTSHash((int) reply), SkillUtils.getSkillLevelFromPTSHash((int) reply));
				if(skill == null)
				{
					_log.warn("Cannot use skill ID[" + skill.getId() + "] LEVEL[" + skill.getLevel() + "]!");
					return;
				}

				if(skill.getMpConsume() < getCurrentMp())
				{
					if(!isSkillDisabled(skill))
					{
						altUseSkill(skill, player);
						html = "agitafterbuff_in.htm";
					}
					else
						html = "agitneedcooltime_in.htm";
				}
				else
					html = "agitnotenoughmp_in.htm";
			}

			if(html != null)
				showChatWindow(player, "residence2/instant_clanhall/" + html, false, "<?MPLeft?>", (int) getCurrentMp());
		}
		else if(ask == 500)
		{
			if(reply == 1)
				showChatWindow(player, "residence2/instant_clanhall/agitout_in.htm", false);
		}
		else if(ask == 700)
		{
			if(reply == 1)
			{
				if(ItemFunctions.haveItem(player, ItemTemplate.ITEM_ID_ADENA, 2000))
				{
					Skill skill = SkillHolder.getInstance().getSkill(TELEPORT_SKILL_ID, 1);
					if(skill != null)
						altUseSkill(skill, player);
					else
						_log.warn("Cannot use skill ID[" + skill.getId() + "] LEVEL[" + skill.getLevel() + "]!");
					getAI().addTask(giran_tel_time, player, 1500);
				}
				else
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else if(reply == 2)
			{
				if(ItemFunctions.haveItem(player, ItemTemplate.ITEM_ID_ADENA, 2000))
				{
					Skill skill = SkillHolder.getInstance().getSkill(TELEPORT_SKILL_ID, 1);
					if(skill != null)
						altUseSkill(skill, player);
					else
						_log.warn("Cannot use skill ID[" + skill.getId() + "] LEVEL[" + skill.getLevel() + "]!");
					getAI().addTask(aden_tel_time, player, 1500);
				}
				else
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else if(reply == 3)
			{
				Skill skill = SkillHolder.getInstance().getSkill(TELEPORT_SKILL_ID, 1);
				if(skill != null)
					altUseSkill(skill, player);
				else
					_log.warn("Cannot use skill ID[" + skill.getId() + "] LEVEL[" + skill.getLevel() + "]!");
				getAI().addTask(gludin_tel_time, player, 1500);
			}
		}
		else if(ask == 600)
		{
			if(reply == 1)
			{
				if(player.getClanId() == getOwnerId())
				{
					String hpRegen = "0";
					String mpRegen = "0";
					String expRegen = "0";

					ResidenceFunction function = getResidence().getActiveFunction(ResidenceFunctionType.RESTORE_HP);
					if(function != null)
						hpRegen = String.valueOf((int) (function.getTemplate().getHpRegen() * 100 - 100));

					function = getResidence().getActiveFunction(ResidenceFunctionType.RESTORE_MP);
					if(function != null)
						mpRegen = String.valueOf((int) (function.getTemplate().getMpRegen() * 100 - 100));

					function = getResidence().getActiveFunction(ResidenceFunctionType.RESTORE_EXP);
					if(function != null)
						expRegen = String.valueOf((int) (function.getTemplate().getExpRestore() * 100));

					showChatWindow(player, "residence2/instant_clanhall/agitdecofunction_01.htm", false, "<?HPDepth?>", hpRegen, "<?MPDepth?>", mpRegen, "<?XPDepth?>", expRegen);
				}
				else
					showChatWindow(player, "residence2/instant_clanhall/noauthority.htm", false);
			}
		}
		else
			super.onMenuSelect(player, ask, reply);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
			showChatWindow(player, "residence2/instant_clanhall/blackinzon_01.htm", firstTalk);
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}

	@Override
	protected void showShopWindow(Player player, int listId, boolean tax)
	{
		if(player.getClanId() == getOwnerId())
			super.showShopWindow(player, listId, tax);
	}

	@Override
	protected void onSpawn()
	{
		addSkill(SkillHolder.getInstance().getSkillEntry(CH_BLESSING_SKIL_ID_9, 1));
		super.onSpawn();
	}

	@Override
	public void onSeeSocialAction(Player talker, int actionId)
	{
		if(actionId == 2)
			getAI().addTimer(Social_Reply_Timer_1, 2000);
		else if(actionId == 7)
			getAI().addTimer(Social_Reply_Timer_2, 2000);
		else if(actionId == 13)
			getAI().addTimer(Social_Reply_Timer_3, 2000);
		else if(actionId == 14)
			getAI().addTimer(Social_Reply_Timer_4, 2000);
	}

	private int getOwnerId()
	{
		return (int) getReflection().getVariables().get("clan_owner_id");
	}

	private InstantClanHall getResidence()
	{
		return (InstantClanHall) getReflection().getVariables().get("instant_clanhall");
	}
}