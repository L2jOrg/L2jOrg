/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.actor;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.stat.PlayableStats;
import org.l2j.gameserver.model.actor.status.PlayableStatus;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.quest.QuestState;

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
     * @param template The CreatureTemplate to apply to the Playable
     */
    public Playable(int objectId, CreatureTemplate template) {
        super(objectId, template);
        setInstanceType(InstanceType.Playable);
        setIsInvul(false);
    }

    public Playable(CreatureTemplate template) {
        super(template);
        setInstanceType(InstanceType.Playable);
        setIsInvul(false);
    }

    @Override
    public PlayableStats getStats() {
        return (PlayableStats) super.getStats();
    }

    @Override
    public void initCharStat() {
        setStat(new PlayableStats(this));
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
        if(!super.doDie(killer)) {
            return false;
        }

        boolean deleteBuffs = true;

        if (isNoblesseBlessedAffected()) {
            stopEffects(EffectFlag.NOBLESS_BLESSING);
            deleteBuffs = false;
        }

        if (isResurrectSpecialAffected()) {
            stopEffects(EffectFlag.RESURRECTION_SPECIAL);
            deleteBuffs = false;
        }

        if (deleteBuffs) {
            stopAllEffectsExceptThoseThatLastThroughDeath();
        }

        // Notify Quest of Playable's death
        final Player actingPlayer = getActingPlayer();

        if (!actingPlayer.isNotifyQuestOfDeathEmpty()) {
            for (QuestState qs : actingPlayer.getNotifyQuestOfDeath()) {
                qs.getQuest().notifyDeath((killer == null ? this : killer), this, qs);
            }
        }

        if (killer != null) {
            final Player killerPlayer = killer.getActingPlayer();
            if ((killerPlayer != null)) {
                killerPlayer.onPlayeableKill(this);
            }
        }

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

        final Clan playerClan = player.getClan();

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
}
