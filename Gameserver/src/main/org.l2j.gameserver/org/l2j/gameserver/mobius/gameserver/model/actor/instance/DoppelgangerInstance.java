/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.actor.instance;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.mobius.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.mobius.gameserver.ai.DoppelgangerAI;
import org.l2j.gameserver.mobius.gameserver.ai.L2CharacterAI;
import org.l2j.gameserver.mobius.gameserver.enums.Team;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.mobius.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.mobius.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.mobius.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.util.logging.Logger;

/**
 * @author Nik
 */
public class DoppelgangerInstance extends L2Npc
{
	protected static final Logger log = Logger.getLogger(DoppelgangerInstance.class.getName());
	
	private boolean _copySummonerEffects = true;
	
	public DoppelgangerInstance(L2NpcTemplate template, L2PcInstance owner)
	{
		super(template);
		
		setSummoner(owner);
		setCloneObjId(owner.getObjectId());
		setClanId(owner.getClanId());
		setInstance(owner.getInstanceWorld()); // set instance to same as owner
		setXYZInvisible(owner.getX() + Rnd.get(-100, 100), owner.getY() + Rnd.get(-100, 100), owner.getZ());
		((DoppelgangerAI) getAI()).setStartFollowController(true);
		followSummoner(true);
	}
	
	@Override
	protected L2CharacterAI initAI()
	{
		return new DoppelgangerAI(this);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		if (_copySummonerEffects && (getSummoner() != null))
		{
			for (BuffInfo summonerInfo : getSummoner().getEffectList().getEffects())
			{
				if (summonerInfo.getAbnormalTime() > 0)
				{
					final BuffInfo info = new BuffInfo(getSummoner(), this, summonerInfo.getSkill(), false, null, null);
					info.setAbnormalTime(summonerInfo.getAbnormalTime());
					getEffectList().add(info);
				}
			}
		}
	}
	
	public void followSummoner(boolean followSummoner)
	{
		if (followSummoner)
		{
			if ((getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE) || (getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE))
			{
				setRunning();
				getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getSummoner());
			}
		}
		else if (getAI().getIntention() == CtrlIntention.AI_INTENTION_FOLLOW)
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		}
	}
	
	public void setCopySummonerEffects(boolean copySummonerEffects)
	{
		_copySummonerEffects = copySummonerEffects;
	}
	
	@Override
	public final byte getPvpFlag()
	{
		return getSummoner() != null ? getSummoner().getPvpFlag() : 0;
	}
	
	@Override
	public final Team getTeam()
	{
		return getSummoner() != null ? getSummoner().getTeam() : Team.NONE;
	}
	
	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return (getSummoner() != null) ? getSummoner().isAutoAttackable(attacker) : super.isAutoAttackable(attacker);
	}
	
	@Override
	public void doAttack(double damage, L2Character target, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect)
	{
		super.doAttack(damage, target, skill, isDOT, directlyToHp, critical, reflect);
		sendDamageMessage(target, skill, (int) damage, critical, false);
	}
	
	@Override
	public void sendDamageMessage(L2Character target, Skill skill, int damage, boolean crit, boolean miss)
	{
		if (miss || (getSummoner() == null) || !getSummoner().isPlayer())
		{
			return;
		}
		
		// Prevents the double spam of system messages, if the target is the owning player.
		if (target.getObjectId() != getSummoner().getObjectId())
		{
			if (getActingPlayer().isInOlympiadMode() && (target.isPlayer()) && ((L2PcInstance) target).isInOlympiadMode() && (((L2PcInstance) target).getOlympiadGameId() == getActingPlayer().getOlympiadGameId()))
			{
				OlympiadGameManager.getInstance().notifyCompetitorDamage(getSummoner().getActingPlayer(), damage);
			}
			
			final SystemMessage sm;
			
			if ((target.isHpBlocked() && !target.isNpc()) || (target.isPlayer() && target.isAffected(EffectFlag.DUELIST_FURY) && !getActingPlayer().isAffected(EffectFlag.FACEOFF)))
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_INFLICTED_S3_DAMAGE_ON_C2);
				sm.addNpcName(this);
				sm.addString(target.getName());
				sm.addInt(damage);
				sm.addPopup(target.getObjectId(), getObjectId(), (damage * -1));
			}
			
			sendPacket(sm);
		}
	}
	
	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, Skill skill)
	{
		super.reduceCurrentHp(damage, attacker, skill);
		
		if ((getSummoner() != null) && getSummoner().isPlayer() && (attacker != null) && !isDead() && !isHpBlocked())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2);
			sm.addNpcName(this);
			sm.addString(attacker.getName());
			sm.addInt((int) damage);
			sm.addPopup(getObjectId(), attacker.getObjectId(), (int) -damage);
			sendPacket(sm);
		}
	}
	
	@Override
	public L2PcInstance getActingPlayer()
	{
		return getSummoner() != null ? getSummoner().getActingPlayer() : super.getActingPlayer();
	}
	
	@Override
	public void onTeleported()
	{
		deleteMe(); // In retail, doppelgangers disappear when summoner teleports.
	}
	
	@Override
	public void sendPacket(IClientOutgoingPacket... packets)
	{
		if (getSummoner() != null)
		{
			getSummoner().sendPacket(packets);
		}
	}
	
	@Override
	public void sendPacket(SystemMessageId id)
	{
		if (getSummoner() != null)
		{
			getSummoner().sendPacket(id);
		}
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "(" + getId() + ") Summoner: " + getSummoner();
	}
}
