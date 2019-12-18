package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.instancemanager.DuelManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.entity.Duel;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

public class PcStatus extends PlayableStatus {
    private double _currentCp = 0; // Current CP of the Player

    public PcStatus(Player activeChar) {
        super(activeChar);
    }

    @Override
    public final void reduceCp(int value) {
        if (_currentCp > value) {
            setCurrentCp(_currentCp - value);
        } else {
            setCurrentCp(0);
        }
    }

    @Override
    public final void reduceHp(double value, Creature attacker) {
        reduceHp(value, attacker, true, false, false, false);
    }

    @Override
    public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHPConsumption) {
        reduceHp(value, attacker, awake, isDOT, isHPConsumption, false);
    }

    public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHPConsumption, boolean ignoreCP) {
        if (getActiveChar().isDead()) {
            return;
        }

        // If OFFLINE_MODE_NO_DAMAGE is enabled and player is offline and he is in store/craft mode, no damage is taken.
        if (Config.OFFLINE_MODE_NO_DAMAGE && (getActiveChar().getClient() != null) && getActiveChar().getClient().isDetached() && ((Config.OFFLINE_TRADE_ENABLE && ((getActiveChar().getPrivateStoreType() == PrivateStoreType.SELL) || (getActiveChar().getPrivateStoreType() == PrivateStoreType.BUY))) || (Config.OFFLINE_CRAFT_ENABLE && (getActiveChar().isCrafting() || (getActiveChar().getPrivateStoreType() == PrivateStoreType.MANUFACTURE))))) {
            return;
        }

        if (getActiveChar().isHpBlocked() && !(isDOT || isHPConsumption)) {
            return;
        }

        if (getActiveChar().isAffected(EffectFlag.DUELIST_FURY) && !attacker.isAffected(EffectFlag.FACEOFF)) {
            return;
        }

        if (!isHPConsumption) {
            if (awake) {
                getActiveChar().stopEffectsOnDamage();
            }
            // Attacked players in craft/shops stand up.
            if (getActiveChar().isCrafting() || getActiveChar().isInStoreMode()) {
                getActiveChar().setPrivateStoreType(PrivateStoreType.NONE);
                getActiveChar().standUp();
                getActiveChar().broadcastUserInfo();
            } else if (getActiveChar().isSitting()) {
                getActiveChar().standUp();
            }

            if (!isDOT) {
                if (Formulas.calcStunBreak(getActiveChar())) {
                    getActiveChar().stopStunning(true);
                }
                if (Formulas.calcRealTargetBreak()) {
                    getActiveChar().getEffectList().stopEffects(AbnormalType.REAL_TARGET);
                }
            }
        }

        int fullValue = (int) value;
        int tDmg = 0;
        int mpDam = 0;

        if ((attacker != null) && (attacker != getActiveChar())) {
            final Player attackerPlayer = attacker.getActingPlayer();

            if (attackerPlayer != null) {
                if (attackerPlayer.isGM() && !attackerPlayer.getAccessLevel().canGiveDamage()) {
                    return;
                }

                if (getActiveChar().isInDuel()) {
                    if (getActiveChar().getDuelState() == Duel.DUELSTATE_DEAD) {
                        return;
                    } else if (getActiveChar().getDuelState() == Duel.DUELSTATE_WINNER) {
                        return;
                    }

                    // cancel duel if player got hit by another player, that is not part of the duel
                    if (attackerPlayer.getDuelId() != getActiveChar().getDuelId()) {
                        getActiveChar().setDuelState(Duel.DUELSTATE_INTERRUPTED);
                    }
                }
            }

            // Check and calculate transfered damage
            final Summon summon = getActiveChar().getFirstServitor();
            if ((summon != null) && GameUtils.checkIfInRange(1000, getActiveChar(), summon, true)) {
                tDmg = ((int) value * (int) getActiveChar().getStats().getValue(Stat.TRANSFER_DAMAGE_SUMMON_PERCENT, 0)) / 100;

                // Only transfer dmg up to current HP, it should not be killed
                tDmg = Math.min((int) summon.getCurrentHp() - 1, tDmg);
                if (tDmg > 0) {
                    summon.reduceCurrentHp(tDmg, attacker, null);
                    value -= tDmg;
                    fullValue = (int) value; // reduce the announced value here as player will get a message about summon damage
                }
            }

            mpDam = ((int) value * (int) getActiveChar().getStats().getValue(Stat.MANA_SHIELD_PERCENT, 0)) / 100;

            if (mpDam > 0) {
                mpDam = (int) (value - mpDam);
                if (mpDam > getActiveChar().getCurrentMp()) {
                    getActiveChar().sendPacket(SystemMessageId.MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING);
                    getActiveChar().stopSkillEffects(true, 1556);
                    value = mpDam - getActiveChar().getCurrentMp();
                    getActiveChar().setCurrentMp(0);
                } else {
                    getActiveChar().reduceCurrentMp(mpDam);
                    final SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.ARCANE_SHIELD_DECREASED_YOUR_MP_BY_S1_INSTEAD_OF_HP);
                    smsg.addInt(mpDam);
                    getActiveChar().sendPacket(smsg);
                    return;
                }
            }

            final Player caster = getActiveChar().getTransferingDamageTo();
            if ((caster != null) && (getActiveChar().getParty() != null) && GameUtils.checkIfInRange(1000, getActiveChar(), caster, true) && !caster.isDead() && (getActiveChar() != caster) && getActiveChar().getParty().getMembers().contains(caster)) {
                int transferDmg = 0;

                transferDmg = ((int) value * (int) getActiveChar().getStats().getValue(Stat.TRANSFER_DAMAGE_TO_PLAYER, 0)) / 100;
                transferDmg = Math.min((int) caster.getCurrentHp() - 1, transferDmg);
                if (transferDmg > 0) {
                    int membersInRange = 0;
                    for (Player member : caster.getParty().getMembers()) {
                        if (GameUtils.checkIfInRange(1000, member, caster, false) && (member != caster)) {
                            membersInRange++;
                        }
                    }

                    if ((isPlayable(attacker)) && (caster.getCurrentCp() > 0)) {
                        if (caster.getCurrentCp() > transferDmg) {
                            caster.getStatus().reduceCp(transferDmg);
                        } else {
                            transferDmg = (int) (transferDmg - caster.getCurrentCp());
                            caster.getStatus().reduceCp((int) caster.getCurrentCp());
                        }
                    }

                    if (membersInRange > 0) {
                        caster.reduceCurrentHp(transferDmg / membersInRange, attacker, null);
                        value -= transferDmg;
                        fullValue = (int) value;
                    }
                }
            }

            if (!ignoreCP && isPlayable(attacker)) {
                if (_currentCp >= value) {
                    setCurrentCp(_currentCp - value); // Set Cp to diff of Cp vs value
                    value = 0; // No need to subtract anything from Hp
                } else {
                    value -= _currentCp; // Get diff from value vs Cp; will apply diff to Hp
                    setCurrentCp(0, false); // Set Cp to 0
                }
            }

            if ((fullValue > 0) && !isDOT) {
                // Send a System Message to the Player
                SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2);
                smsg.addString(getActiveChar().getName());
                smsg.addString(attacker.getName());
                smsg.addInt(fullValue);
                smsg.addPopup(getActiveChar().getObjectId(), attacker.getObjectId(), -fullValue);
                getActiveChar().sendPacket(smsg);

                if (tDmg > 0 && attackerPlayer != null) {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_DEALT_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR);
                    smsg.addInt(fullValue);
                    smsg.addInt(tDmg);
                    attackerPlayer.sendPacket(smsg);
                }
            }
        }

        if (value > 0) {
            double newHp = Math.max(getCurrentHp() - value, getActiveChar().isUndying() ? 1 : 0);
            if (newHp <= 0) {
                if (getActiveChar().isInDuel()) {
                    getActiveChar().disableAllSkills();
                    stopHpMpRegeneration();
                    if (attacker != null) {
                        attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                        attacker.sendPacket(ActionFailed.STATIC_PACKET);
                    }
                    // let the DuelManager know of his defeat
                    DuelManager.getInstance().onPlayerDefeat(getActiveChar());
                    newHp = 1;
                } else {
                    newHp = 0;
                }
            }
            setCurrentHp(newHp);
        }

        if ((getActiveChar().getCurrentHp() < 0.5) && !isHPConsumption && !getActiveChar().isUndying()) {
            getActiveChar().abortAttack();
            getActiveChar().abortCast();

            if (getActiveChar().isInOlympiadMode()) {
                stopHpMpRegeneration();
                getActiveChar().setIsDead(true);
                getActiveChar().setIsPendingRevive(true);
                final Summon pet = getActiveChar().getPet();
                if (pet != null) {
                    pet.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                }
                getActiveChar().getServitors().values().forEach(s -> s.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE));
                return;
            }

            getActiveChar().doDie(attacker);
        }
    }

    @Override
    public final double getCurrentCp() {
        return _currentCp;
    }

    @Override
    public final void setCurrentCp(double newCp) {
        setCurrentCp(newCp, true);
    }

    @Override
    public final void setCurrentCp(double newCp, boolean broadcastPacket) {
        // Get the Max CP of the Creature
        final int currentCp = (int) _currentCp;
        final int maxCp = getActiveChar().getStats().getMaxCp();

        synchronized (this) {
            if (getActiveChar().isDead()) {
                return;
            }

            if (newCp < 0) {
                newCp = 0;
            }

            if (newCp >= maxCp) {
                // Set the RegenActive flag to false
                _currentCp = maxCp;
                _flagsRegenActive &= ~REGEN_FLAG_CP;

                // Stop the HP/MP/CP Regeneration task
                if (_flagsRegenActive == 0) {
                    stopHpMpRegeneration();
                }
            } else {
                // Set the RegenActive flag to true
                _currentCp = newCp;
                _flagsRegenActive |= REGEN_FLAG_CP;

                // Start the HP/MP/CP Regeneration task with Medium priority
                startHpMpRegeneration();
            }
        }

        // Send the Server->Client packet StatusUpdate with current HP and MP to all other Player to inform
        if ((currentCp != _currentCp) && broadcastPacket) {
            getActiveChar().broadcastStatusUpdate();
        }
    }

    @Override
    protected void doRegeneration() {
        final PlayerStats charstat = getActiveChar().getStats();

        // Modify the current CP of the Creature and broadcast Server->Client packet StatusUpdate
        if (_currentCp < charstat.getMaxRecoverableCp()) {
            setCurrentCp(_currentCp + getActiveChar().getStats().getValue(Stat.REGENERATE_CP_RATE), false);
        }

        // Modify the current HP of the Creature and broadcast Server->Client packet StatusUpdate
        if (getCurrentHp() < charstat.getMaxRecoverableHp()) {
            setCurrentHp(getCurrentHp() + getActiveChar().getStats().getValue(Stat.REGENERATE_HP_RATE), false);
        }

        // Modify the current MP of the Creature and broadcast Server->Client packet StatusUpdate
        if (getCurrentMp() < charstat.getMaxRecoverableMp()) {
            setCurrentMp(getCurrentMp() + getActiveChar().getStats().getValue(Stat.REGENERATE_MP_RATE), false);
        }

        getActiveChar().broadcastStatusUpdate(); // send the StatusUpdate packet
    }

    @Override
    public Player getActiveChar() {
        return (Player) super.getActiveChar();
    }
}
