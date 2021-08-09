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
package org.l2j.gameserver.model.actor.transform;

import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.Sex;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerTransform;
import org.l2j.gameserver.model.holders.AdditionalItemHolder;
import org.l2j.gameserver.model.holders.AdditionalSkillHolder;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public final class Transform implements IIdentifiable {
    private final int _id;
    private final int _displayId;
    private final TransformType _type;
    private final boolean _canSwim;
    private final boolean _canAttack;
    private final String _name;
    private final String _title;

    private TransformTemplate _maleTemplate;
    private TransformTemplate _femaleTemplate;

    public Transform(StatsSet set) {
        _id = set.getInt("id");
        _displayId = set.getInt("displayId", _id);
        _type = set.getEnum("type", TransformType.class, TransformType.COMBAT);
        _canSwim = set.getInt("can_swim", 0) == 1;
        _canAttack = set.getInt("normal_attackable", 1) == 1;
        _name = set.getString("setName", null);
        _title = set.getString("setTitle", null);
    }

    /**
     * Gets the transformation ID.
     *
     * @return the transformation ID
     */
    @Override
    public int getId() {
        return _id;
    }

    public int getDisplayId() {
        return _displayId;
    }

    public TransformType getType() {
        return _type;
    }

    public boolean canSwim() {
        return _canSwim;
    }

    public boolean canAttack() {
        return _canAttack;
    }

    private TransformTemplate getTemplate(Creature creature) {
        if (isPlayer(creature)) {
            return (creature.getActingPlayer().getAppearance().isFemale() ? _femaleTemplate : _maleTemplate);
        } else if (isNpc(creature)) {
            return ((Npc) creature).getTemplate().getSex() == Sex.FEMALE ? _femaleTemplate : _maleTemplate;
        }

        return null;
    }

    public void setTemplate(boolean male, TransformTemplate template) {
        if (male) {
            _maleTemplate = template;
        } else {
            _femaleTemplate = template;
        }
    }

    /**
     * @return {@code true} if transform type is mode change, {@code false} otherwise
     */
    public boolean isStance() {
        return _type == TransformType.MODE_CHANGE;
    }

    /**
     * @return {@code true} if transform type is combat, {@code false} otherwise
     */
    public boolean isCombat() {
        return _type == TransformType.COMBAT;
    }

    /**
     * @return {@code true} if transform type is flying, {@code false} otherwise
     */
    public boolean isFlying() {
        return _type == TransformType.FLYING;
    }

    /**
     * @return {@code true} if transform type is raiding, {@code false} otherwise
     */
    public boolean isRiding() {
        return _type == TransformType.RIDING_MODE;
    }

    public double getCollisionHeight(Creature creature, double defaultCollisionHeight) {
        final TransformTemplate template = getTemplate(creature);
        if ((template != null) && (template.getCollisionHeight() != null)) {
            return template.getCollisionHeight();
        }

        return defaultCollisionHeight;
    }

    public double getCollisionRadius(Creature creature, double defaultCollisionRadius) {
        final TransformTemplate template = getTemplate(creature);
        if ((template != null) && (template.getCollisionRadius() != null)) {
            return template.getCollisionRadius();
        }

        return defaultCollisionRadius;
    }

    public void onTransform(Creature creature, boolean addSkills) {
        // Abort attacking and casting.
        creature.abortAttack();
        creature.abortCast();

        final Player player = creature.getActingPlayer();

        // Get off the strider or something else if character is mounted
        if (isPlayer(creature) && player.isMounted()) {
            player.dismount();
        }

        final TransformTemplate template = getTemplate(creature);
        if (template != null) {
            // Start flying.
            if (isFlying()) {
                creature.setIsFlying(true);
            }

            // Get player a bit higher so he doesn't drops underground after transformation happens
            creature.setXYZ(creature.getX(), creature.getY(), (int) (creature.getZ() + getCollisionHeight(creature, 0)));

            if (isPlayer(creature)) {
                onPlayerTransform(addSkills, player, template);
            } else {
                creature.broadcastInfo();
            }

            // I don't know why, but you need to broadcast this to trigger the transformation client-side.
            // Usually should be sent naturally after applying effect, but sometimes is sent before that... i just dont know...
            creature.updateAbnormalVisualEffects();
        }
    }

    private void onPlayerTransform(boolean addSkills, Player player, TransformTemplate template) {
        changeNameAndTitle(player);
        addSkills(addSkills, player, template);
        blockItems(player, template);

        if (template.hasBasicActionList()) {
            player.sendPacket(template.getBasicActionList());
        }

        player.getEffectList().stopAllToggles();

        if (player.hasTransformSkills()) {
            player.sendSkillList();
            player.sendPacket(new SkillCoolTime(player));
        }

        player.broadcastUserInfo();

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerTransform(player, getId()), player);
    }

    private void blockItems(Player player, TransformTemplate template) {
        if (!template.getAdditionalItems().isEmpty()) {
            final IntSet allowed = new HashIntSet();
            final IntSet notAllowed = new HashIntSet();
            for (AdditionalItemHolder holder : template.getAdditionalItems()) {
                if (holder.isAllowedToUse()) {
                    allowed.add(holder.getId());
                } else {
                    notAllowed.add(holder.getId());
                }
            }

            if (!allowed.isEmpty()) {
                player.getInventory().setInventoryBlock(allowed, InventoryBlockType.WHITELIST);
            }

            if (!notAllowed.isEmpty()) {
                player.getInventory().setInventoryBlock(notAllowed, InventoryBlockType.BLACKLIST);
            }
        }
    }

    private void addSkills(boolean addSkills, Player player, TransformTemplate template) {
        if (addSkills) {
            template.getSkills().forEach(player::addTransformSkill);

            for (var additionalSkill : template.getAdditionalSkills()) {
                if(player.getLevel() >= additionalSkill.minLevel()) {
                    player.addTransformSkill(additionalSkill.skill());
                }
            }
        }
    }

    private void changeNameAndTitle(Player player) {
        if (_name != null) {
            player.getAppearance().setVisibleName(_name);
        }
        if (_title != null) {
            player.getAppearance().setVisibleTitle(_title);
        }
    }

    public void onUntransform(Creature creature) {
        // Abort attacking and casting.
        creature.abortAttack();
        creature.abortCast();

        final TransformTemplate template = getTemplate(creature);
        if (template != null) {
            // Stop flying.
            if (isFlying()) {
                creature.setIsFlying(false);
            }

            if (isPlayer(creature)) {
                final Player player = creature.getActingPlayer();
                final boolean hasTransformSkills = player.hasTransformSkills();
                if (_name != null) {
                    player.getAppearance().setVisibleName(null);
                }
                if (_title != null) {
                    player.getAppearance().setVisibleTitle(null);
                }

                // Remove transformation skills.
                player.removeAllTransformSkills();

                // Remove inventory blocks if needed.
                if (!template.getAdditionalItems().isEmpty()) {
                    player.getInventory().unblock();
                }

                player.sendPacket(ExBasicActionList.STATIC_PACKET);

                player.getEffectList().stopEffects(AbnormalType.SPECIAL_RIDE);
                player.getEffectList().stopEffects(AbnormalType.TRANSFORM);
                player.getEffectList().stopEffects(AbnormalType.CHANGEBODY);

                if (hasTransformSkills) {
                    player.sendSkillList();
                    player.sendPacket(new SkillCoolTime(player));
                }

                player.broadcastUserInfo();
                player.sendPacket(new ExUserInfoEquipSlot(player));
                // Notify to scripts
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerTransform(player, 0), player);
            } else {
                creature.broadcastInfo();
            }
        }
    }

    public void onLevelUp(Player player) {
        final TransformTemplate template = getTemplate(player);
        if (template != null) {
            if (!template.getAdditionalSkills().isEmpty()) {
                for (AdditionalSkillHolder holder : template.getAdditionalSkills()) {
                    if (player.getLevel() >= holder.minLevel() && player.getSkillLevel(holder.skill().getId()) < holder.skill().getLevel()) {
                        player.addTransformSkill(holder.skill());
                    }
                }
            }
        }
    }

    public WeaponType getBaseAttackType(Creature creature, WeaponType defaultAttackType) {
        final TransformTemplate template = getTemplate(creature);
        if (template != null) {
            final WeaponType weaponType = template.getBaseAttackType();
            if (weaponType != null) {
                return weaponType;
            }
        }
        return defaultAttackType;
    }

    public double getStats(Creature creature, Stat stat, double defaultValue) {
        double val = defaultValue;
        final TransformTemplate template = getTemplate(creature);
        if (template != null) {
            val = template.getStats(stat, defaultValue);
            final TransformLevelData data = template.getData(creature.getLevel());
            if (data != null) {
                val = data.getStats(stat, defaultValue);
            }
        }
        return val;
    }

    public int getBaseDefBySlot(Player player, InventorySlot slot) {
        final int defaultValue = player.getTemplate().getBaseDefBySlot(slot);
        final TransformTemplate template = getTemplate(player);

        return template == null ? defaultValue : template.getDefense(slot, defaultValue);
    }

    /**
     * @return {@code -1} if this transformation doesn't alter levelmod, otherwise a new levelmod will be returned.
     */
    public double getLevelMod(Creature creature) {
        double val = 1;
        final TransformTemplate template = getTemplate(creature);
        if (template != null) {
            final TransformLevelData data = template.getData(creature.getLevel());
            if (data != null) {
                val = data.getLevelMod();
            }
        }
        return val;
    }
}
