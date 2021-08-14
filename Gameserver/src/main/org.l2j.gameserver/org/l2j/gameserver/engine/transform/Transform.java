/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.engine.transform;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.Sex;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerTransform;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;

import java.util.List;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public record Transform(
        int id,
        TransformType type,
        int displayId,
        boolean canSwim,
        boolean canAttack,
        String name,
        String title,
        TransformAttributes attributes,
        ExBasicActionList basicActionList,
        List<Skill> skills) {

    /**
     * @return {@code true} if transform type is mode change, {@code false} otherwise
     */
    public boolean isStance() {
        return type == TransformType.MODE_CHANGE;
    }

    /**
     * @return {@code true} if transform type is combat, {@code false} otherwise
     */
    public boolean isCombat() {
        return type == TransformType.COMBAT;
    }

    /**
     * @return {@code true} if transform type is flying, {@code false} otherwise
     */
    public boolean isFlying() {
        return type == TransformType.FLYING;
    }

    /**
     * @return {@code true} if transform type is raiding, {@code false} otherwise
     */
    public boolean isRiding() {
        return type == TransformType.RIDING_MODE;
    }

    public double getCollisionHeight(Creature creature, double defaultCollisionHeight) {
        if (creature instanceof Player player) {
            return player.getAppearance().isFemale() ? attributes.femaleHeight() : attributes.height();
        } else if (creature instanceof Npc npc) {
            return npc.getTemplate().getSex() == Sex.FEMALE ? attributes.femaleHeight() : attributes.height();
        }
        return defaultCollisionHeight;
    }

    public double getCollisionRadius(Creature creature, double defaultCollisionRadius) {
        if (creature instanceof Player player) {
            return player.getAppearance().isFemale() ? attributes.femaleRadius() : attributes.radius();
        } else if (creature instanceof Npc npc) {
            return npc.getTemplate().getSex() == Sex.FEMALE ? attributes.femaleRadius() : attributes.radius();
        }
        return defaultCollisionRadius;
    }

    void onTransform(Creature creature, boolean addSkills) {
        if(!canTransform(creature)) {
            return;
        }

        creature.abortAttack();
        creature.abortCast();

        if(creature instanceof Player player && player.isMounted()) {
            player.dismount();
        }

        if(isFlying()) {
            creature.setIsFlying(true);
        }

        creature.setXYZ(creature.getX(), creature.getY(), (int) (creature.getZ() + getCollisionHeight(creature, 0)));

        if (creature instanceof Player player) {
            onPlayerTransform(player, addSkills);
        } else {
            creature.broadcastInfo();
        }
    }

    private boolean canTransform(Creature creature) {
        return creature instanceof Player || creature instanceof Npc;
    }

    private void onPlayerTransform(Player player, boolean addSkills) {
        changeNameAndTitle(player);
        if(addSkills) {
            addSkills(player);
        }

        if(basicActionList != null) {
            player.sendPacket(basicActionList);
        }

        player.getEffectList().stopAllToggles();

        if (player.hasTransformSkills()) {
            player.sendSkillList();
            player.sendPacket(new SkillCoolTime(player));
        }

        player.updateAbnormalVisualEffects();
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerTransform(player, id), player);
    }


    private void addSkills(Player player) {
        skills.forEach(player::addTransformSkill);
    }

    private void changeNameAndTitle(Player player) {
        if (Util.isNotEmpty(name)) {
            player.getAppearance().setVisibleName(name);
        }
        if (Util.isNotEmpty(title)) {
            player.getAppearance().setVisibleTitle(title);
        }
    }

    public void onUntransform(Creature creature) {
        creature.abortAttack();
        creature.abortCast();

        if (isFlying()) {
            creature.setIsFlying(false);
        }

        if (creature instanceof Player player) {
            final boolean hasTransformSkills = player.hasTransformSkills();
            removeTransformationAttribute(player, hasTransformSkills);

            player.updateAbnormalVisualEffects();
            player.sendPacket(new ExUserInfoEquipSlot(player));
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerTransform(player, 0), player);
        } else {
            creature.broadcastInfo();
        }
    }

    private void removeTransformationAttribute(Player player, boolean hasTransformSkills) {
        if (Util.isNotEmpty(name)) {
            player.getAppearance().setVisibleName(null);
        }
        if (Util.isNotEmpty(title)) {
            player.getAppearance().setVisibleTitle(null);
        }

        player.removeAllTransformSkills();

        player.sendPacket(ExBasicActionList.STATIC_PACKET);

        player.getEffectList().stopEffects(AbnormalType.SPECIAL_RIDE);
        player.getEffectList().stopEffects(AbnormalType.TRANSFORM);
        player.getEffectList().stopEffects(AbnormalType.CHANGEBODY);

        if (hasTransformSkills) {
            player.sendSkillList();
            player.sendPacket(new SkillCoolTime(player));
        }
    }

    public WeaponType attackType() {
        return attributes.attackType();
    }
}
