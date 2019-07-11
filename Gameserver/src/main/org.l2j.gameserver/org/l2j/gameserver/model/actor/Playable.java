package org.l2j.gameserver.model.actor;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.stat.PlayableStat;
import org.l2j.gameserver.model.actor.status.PlayableStatus;
import org.l2j.gameserver.model.actor.templates.L2CharTemplate;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;

/**
 * This class represents all Playable characters in the world.<br>
 * Playable:
 * <ul>
 * <li>Player</li>
 * <li>Summon</li>
 * </ul>
 */
public abstract class Playable extends Creature {
    private Creature _lockedTarget = null;
    private Player transferDmgTo = null;

    /**
     * Constructor of Playable.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Call the Creature constructor to create an empty _skills slot and link copy basic Calculator set to this Playable</li>
     * </ul>
     *
     * @param objectId the object id
     * @param template The L2CharTemplate to apply to the Playable
     */
    public Playable(int objectId, L2CharTemplate template) {
        super(objectId, template);
        setInstanceType(InstanceType.L2Playable);
        setIsInvul(false);
    }

    public Playable(L2CharTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2Playable);
        setIsInvul(false);
    }

    @Override
    public PlayableStat getStat() {
        return (PlayableStat) super.getStat();
    }

    @Override
    public void initCharStat() {
        setStat(new PlayableStat(this));
    }

    @Override
    public PlayableStatus getStatus() {
        return (PlayableStatus) super.getStatus();
    }

    @Override
    public void initCharStatus() {
        setStatus(new PlayableStatus(this));
    }

    @Override
    public boolean doDie(Creature killer) {
        final TerminateReturn returnBack = EventDispatcher.getInstance().notifyEvent(new OnCreatureDeath(killer, this), this, TerminateReturn.class);
        if ((returnBack != null) && returnBack.terminate()) {
            return false;
        }

        // killing is only possible one time
        synchronized (this) {
            if (isDead()) {
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

        if (isNoblesseBlessedAffected()) {
            stopEffects(EffectFlag.NOBLESS_BLESSING);
            deleteBuffs = false;
        }
        if (isResurrectSpecialAffected()) {
            stopEffects(EffectFlag.RESURRECTION_SPECIAL);
            deleteBuffs = false;
        }
        if (isPlayer()) {
            final Player activeChar = getActingPlayer();

            if (activeChar.hasCharmOfCourage()) {
                if (activeChar.isInSiege()) {
                    getActingPlayer().reviveRequest(getActingPlayer(), null, false, 0);
                }
                activeChar.setCharmOfCourage(false);
                activeChar.sendPacket(new EtcStatusUpdate(activeChar));
            }
        }

        if (deleteBuffs) {
            stopAllEffectsExceptThoseThatLastThroughDeath();
        }

        // Send the Server->Client packet StatusUpdate with current HP and MP to all other Player to inform
        broadcastStatusUpdate();

        ZoneManager.getInstance().getRegion(this).onDeath(this);

        // Notify Quest of Playable's death
        final Player actingPlayer = getActingPlayer();

        if (!actingPlayer.isNotifyQuestOfDeathEmpty()) {
            for (QuestState qs : actingPlayer.getNotifyQuestOfDeath()) {
                qs.getQuest().notifyDeath((killer == null ? this : killer), this, qs);
            }
        }
        // Notify instance
        if (isPlayer()) {
            final Instance instance = getInstanceWorld();
            if (instance != null) {
                instance.onDeath(getActingPlayer());
            }
        }

        if (killer != null) {
            final Player killerPlayer = killer.getActingPlayer();
            if ((killerPlayer != null) && isPlayable()) {
                killerPlayer.onPlayerKill(this);
            }
        }

        // Notify Creature AI
        getAI().notifyEvent(CtrlEvent.EVT_DEAD);
        return true;
    }

    public boolean checkIfPvP(Player target) {
        final Player player = getActingPlayer();

        if ((player == null) //
                || (target == null) //
                || (player == target) //
                || (target.getReputation() < 0) //
                || (target.getPvpFlag() > 0) //
                || target.isOnDarkSide()) {
            return true;
        } else if (player.isInParty() && player.getParty().containsPlayer(target)) {
            return false;
        }

        final L2Clan playerClan = player.getClan();

        if ((playerClan != null) && !player.isAcademyMember() && !target.isAcademyMember()) {
            final ClanWar war = playerClan.getWarWith(target.getClanId());
            return (war != null) && (war.getState() == ClanWarState.MUTUAL);
        }
        return false;
    }

    /**
     * Return True.
     */
    @Override
    public boolean canBeAttacked() {
        return true;
    }

    // Support for Noblesse Blessing skill, where buffs are retained after resurrect
    public final boolean isNoblesseBlessedAffected() {
        return isAffected(EffectFlag.NOBLESS_BLESSING);
    }

    /**
     * @return {@code true} if char can resurrect by himself, {@code false} otherwise
     */
    public final boolean isResurrectSpecialAffected() {
        return isAffected(EffectFlag.RESURRECTION_SPECIAL);
    }

    /**
     * @return {@code true} if the Silent Moving mode is active, {@code false} otherwise
     */
    public boolean isSilentMovingAffected() {
        return isAffected(EffectFlag.SILENT_MOVE);
    }

    /**
     * For Newbie Protection Blessing skill, keeps you safe from an attack by a chaotic character >= 10 levels apart from you.
     *
     * @return
     */
    public final boolean isProtectionBlessingAffected() {
        return isAffected(EffectFlag.PROTECTION_BLESSING);
    }

    @Override
    public void updateEffectIcons(boolean partyOnly) {
        getEffectList().updateEffectIcons(partyOnly);
    }

    public boolean isLockedTarget() {
        return _lockedTarget != null;
    }

    public Creature getLockedTarget() {
        return _lockedTarget;
    }

    public void setLockedTarget(Creature cha) {
        _lockedTarget = cha;
    }

    public void setTransferDamageTo(Player val) {
        transferDmgTo = val;
    }

    public Player getTransferingDamageTo() {
        return transferDmgTo;
    }

    public abstract void doPickupItem(WorldObject object);

    public abstract boolean useMagic(Skill skill, Item item, boolean forceUse, boolean dontMove);

    public abstract void storeMe();

    public abstract void storeEffect(boolean storeEffects);

    public abstract void restoreEffects();

    @Override
    public boolean isPlayable() {
        return true;
    }
}
