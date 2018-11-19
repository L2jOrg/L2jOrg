package l2s.gameserver.model.instances.residences.clanhall;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import l2s.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import l2s.gameserver.model.entity.events.objects.CTBTeamObject;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.templates.npc.NpcTemplate;

import org.apache.commons.lang3.StringUtils;

/**
 * @author VISTALL
 * @date 16:55/21.04.2011
 */
public abstract class CTBBossInstance extends MonsterInstance
{
	public static final SkillEntry SKILL = SkillHolder.getInstance().getSkillEntry(5456, 1);
	private CTBTeamObject _matchTeamObject;

	public CTBBossInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		setHasChatWindow(false);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{
		if(attacker.getLevel() > (getLevel() + 8) && !attacker.getAbnormalList().contains(SKILL.getId()))
		{
			doCast(SKILL, attacker, false);
			return;
		}

		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld);
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		CTBSiegeClanObject clan = _matchTeamObject.getSiegeClan();
		if(clan != null && attacker.isPlayable())
		{
			Player player = attacker.getPlayer();
			if(player.getClan() == clan.getClan())
				return false;
		}
		return true;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return isAttackable(attacker);
	}

	@Override
	public void onDeath(Creature killer)
	{
		ClanHallTeamBattleEvent event = getEvent(ClanHallTeamBattleEvent.class);
		event.processStep(_matchTeamObject);

		super.onDeath(killer);
	}

	@Override
	public String getTitle()
	{
		CTBSiegeClanObject clan = _matchTeamObject.getSiegeClan();
		return clan == null ? StringUtils.EMPTY : clan.getClan().getName();
	}

	public void setMatchTeamObject(CTBTeamObject matchTeamObject)
	{
		_matchTeamObject = matchTeamObject;
	}
}