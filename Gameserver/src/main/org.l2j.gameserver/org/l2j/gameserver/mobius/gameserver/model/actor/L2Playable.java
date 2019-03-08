package org.l2j.gameserver.mobius.gameserver.model.actor;

import org.l2j.gameserver.mobius.gameserver.enums.InstanceType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.mobius.gameserver.model.ClanWar;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.stat.PlayableStat;
import org.l2j.gameserver.mobius.gameserver.model.actor.status.PlayableStatus;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2CharTemplate;
import org.l2j.gameserver.mobius.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.mobius.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.mobius.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.mobius.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.EtcStatusUpdate;

/**
 * This class represents all Playable characters in the world.<br>
 * L2Playable:
 * <ul>
 * <li>L2PcInstance</li>
 * <li>L2Summon</li>
 * </ul>
 */
public abstract class L2Playable extends L2Character
{
	private L2Character _lockedTarget = null;
	private L2PcInstance transferDmgTo = null;
	
	/**
	 * Constructor of L2Playable.<br>
	 * <B><U> Actions</U> :</B>
	 * <ul>
	 * <li>Call the L2Character constructor to create an empty _skills slot and link copy basic Calculator set to this L2Playable</li>
	 * </ul>
	 * @param objectId the object id
	 * @param template The L2CharTemplate to apply to the L2Playable
	 */
	public L2Playable(int objectId, L2CharTemplate template)
	{
		super(objectId, template);
		setInstanceType(InstanceType.L2Playable);
		setIsInvul(false);
	}
	
	public L2Playable(L2CharTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2Playable);
		setIsInvul(false);
	}
	
	@Override
	public PlayableStat getStat()
	{
		return (PlayableStat) super.getStat();
	}
	
	@Override
	public void initCharStat()
	{
		setStat(new PlayableStat(this));
	}
	
	@Override
	public PlayableStatus getStatus()
	{
		return (PlayableStatus) super.getStatus();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new PlayableStatus(this));
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		final TerminateReturn returnBack = EventDispatcher.getInstance().notifyEvent(new OnCreatureDeath(killer, this), this, TerminateReturn.class);
		if ((returnBack != null) && returnBack.terminate())
		{
			return false;
		}
		
		// killing is only possible one time
		synchronized (this)
		{
			if (isDead())
			{
				return false;
			}
			// now reset currentHp to zero
			setCurrentHp(0);
			setIsDead(true);
		}
		
		abortAttack();
		abortCast();
		
		// Set target to null and cancel Attack or Cast
		setTarget(null);
		
		// Stop movement
		stopMove(null);
		
		// Stop HP/MP/CP Regeneration task
		getStatus().stopHpMpRegeneration();
		
		boolean deleteBuffs = true;
		
		if (isNoblesseBlessedAffected())
		{
			stopEffects(EffectFlag.NOBLESS_BLESSING);
			deleteBuffs = false;
		}
		if (isResurrectSpecialAffected())
		{
			stopEffects(EffectFlag.RESURRECTION_SPECIAL);
			deleteBuffs = false;
		}
		if (isPlayer())
		{
			final L2PcInstance activeChar = getActingPlayer();
			
			if (activeChar.hasCharmOfCourage())
			{
				if (activeChar.isInSiege())
				{
					getActingPlayer().reviveRequest(getActingPlayer(), null, false, 0);
				}
				activeChar.setCharmOfCourage(false);
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
		
		if (deleteBuffs)
		{
			stopAllEffectsExceptThoseThatLastThroughDeath();
		}
		
		// Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		broadcastStatusUpdate();
		
		ZoneManager.getInstance().getRegion(this).onDeath(this);
		
		// Notify Quest of L2Playable's death
		final L2PcInstance actingPlayer = getActingPlayer();
		
		if (!actingPlayer.isNotifyQuestOfDeathEmpty())
		{
			for (QuestState qs : actingPlayer.getNotifyQuestOfDeath())
			{
				qs.getQuest().notifyDeath((killer == null ? this : killer), this, qs);
			}
		}
		// Notify instance
		if (isPlayer())
		{
			final Instance instance = getInstanceWorld();
			if (instance != null)
			{
				instance.onDeath(getActingPlayer());
			}
		}
		
		if (killer != null)
		{
			final L2PcInstance killerPlayer = killer.getActingPlayer();
			if ((killerPlayer != null) && isPlayable())
			{
				killerPlayer.onPlayerKill(this);
			}
		}
		
		// Notify L2Character AI
		getAI().notifyEvent(CtrlEvent.EVT_DEAD);
		return true;
	}
	
	public boolean checkIfPvP(L2PcInstance target)
	{
		final L2PcInstance player = getActingPlayer();
		
		if ((player == null) //
			|| (target == null) //
			|| (player == target) //
			|| (target.getReputation() < 0) //
			|| (target.getPvpFlag() > 0) //
			|| target.isOnDarkSide())
		{
			return true;
		}
		else if (player.isInParty() && player.getParty().containsPlayer(target))
		{
			return false;
		}
		
		final L2Clan playerClan = player.getClan();
		
		if ((playerClan != null) && !player.isAcademyMember() && !target.isAcademyMember())
		{
			final ClanWar war = playerClan.getWarWith(target.getClanId());
			return (war != null) && (war.getState() == ClanWarState.MUTUAL);
		}
		return false;
	}
	
	/**
	 * Return True.
	 */
	@Override
	public boolean canBeAttacked()
	{
		return true;
	}
	
	// Support for Noblesse Blessing skill, where buffs are retained after resurrect
	public final boolean isNoblesseBlessedAffected()
	{
		return isAffected(EffectFlag.NOBLESS_BLESSING);
	}
	
	/**
	 * @return {@code true} if char can resurrect by himself, {@code false} otherwise
	 */
	public final boolean isResurrectSpecialAffected()
	{
		return isAffected(EffectFlag.RESURRECTION_SPECIAL);
	}
	
	/**
	 * @return {@code true} if the Silent Moving mode is active, {@code false} otherwise
	 */
	public boolean isSilentMovingAffected()
	{
		return isAffected(EffectFlag.SILENT_MOVE);
	}
	
	/**
	 * For Newbie Protection Blessing skill, keeps you safe from an attack by a chaotic character >= 10 levels apart from you.
	 * @return
	 */
	public final boolean isProtectionBlessingAffected()
	{
		return isAffected(EffectFlag.PROTECTION_BLESSING);
	}
	
	@Override
	public void updateEffectIcons(boolean partyOnly)
	{
		getEffectList().updateEffectIcons(partyOnly);
	}
	
	public boolean isLockedTarget()
	{
		return _lockedTarget != null;
	}
	
	public L2Character getLockedTarget()
	{
		return _lockedTarget;
	}
	
	public void setLockedTarget(L2Character cha)
	{
		_lockedTarget = cha;
	}
	
	public void setTransferDamageTo(L2PcInstance val)
	{
		transferDmgTo = val;
	}
	
	public L2PcInstance getTransferingDamageTo()
	{
		return transferDmgTo;
	}
	
	public abstract void doPickupItem(L2Object object);
	
	public abstract boolean useMagic(Skill skill, L2ItemInstance item, boolean forceUse, boolean dontMove);
	
	public abstract void storeMe();
	
	public abstract void storeEffect(boolean storeEffects);
	
	public abstract void restoreEffects();
	
	@Override
	public boolean isPlayable()
	{
		return true;
	}
}
