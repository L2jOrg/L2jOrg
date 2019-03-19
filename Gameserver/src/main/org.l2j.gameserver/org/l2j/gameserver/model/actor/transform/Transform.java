package org.l2j.gameserver.model.actor.transform;

import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.enums.Sex;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerTransform;
import org.l2j.gameserver.model.holders.AdditionalItemHolder;
import org.l2j.gameserver.model.holders.AdditionalSkillHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author UnAfraid
 */
public final class Transform implements IIdentifiable {
    private final int _id;
    private final int _displayId;
    private final TransformType _type;
    private final boolean _canSwim;
    private final int _spawnHeight;
    private final boolean _canAttack;
    private final String _name;
    private final String _title;
    private final boolean _allowAllSkills;

    private TransformTemplate _maleTemplate;
    private TransformTemplate _femaleTemplate;

    public Transform(StatsSet set) {
        _id = set.getInt("id");
        _displayId = set.getInt("displayId", _id);
        _type = set.getEnum("type", TransformType.class, TransformType.COMBAT);
        _canSwim = set.getInt("can_swim", 0) == 1;
        _canAttack = set.getInt("normal_attackable", 1) == 1;
        _spawnHeight = set.getInt("spawn_height", 0);
        _name = set.getString("setName", null);
        _title = set.getString("setTitle", null);
        _allowAllSkills = set.getInt("allow_all_skills", 0) == 1;
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

    public int getSpawnHeight() {
        return _spawnHeight;
    }

    /**
     * @return name that's going to be set to the player while is transformed with current transformation
     */
    public String getName() {
        return _name;
    }

    /**
     * @return title that's going to be set to the player while is transformed with current transformation
     */
    public String getTitle() {
        return _title;
    }

    private TransformTemplate getTemplate(L2Character creature) {
        if (creature.isPlayer()) {
            return (creature.getActingPlayer().getAppearance().getSex() ? _femaleTemplate : _maleTemplate);
        } else if (creature.isNpc()) {
            return ((L2Npc) creature).getTemplate().getSex() == Sex.FEMALE ? _femaleTemplate : _maleTemplate;
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
     * @return Allow all skills for this transformation.
     */
    public boolean allowAllSkills() {
        return _allowAllSkills;
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
     * @return {@code true} if transform type is non combat, {@code false} otherwise
     */
    public boolean isNonCombat() {
        return _type == TransformType.NON_COMBAT;
    }

    /**
     * @return {@code true} if transform type is flying, {@code false} otherwise
     */
    public boolean isFlying() {
        return _type == TransformType.FLYING;
    }

    /**
     * @return {@code true} if transform type is cursed, {@code false} otherwise
     */
    public boolean isCursed() {
        return _type == TransformType.CURSED;
    }

    /**
     * @return {@code true} if transform type is raiding, {@code false} otherwise
     */
    public boolean isRiding() {
        return _type == TransformType.RIDING_MODE;
    }

    /**
     * @return {@code true} if transform type is pure stat, {@code false} otherwise
     */
    public boolean isPureStats() {
        return _type == TransformType.PURE_STAT;
    }

    public double getCollisionHeight(L2Character creature, double defaultCollisionHeight) {
        final TransformTemplate template = getTemplate(creature);
        if ((template != null) && (template.getCollisionHeight() != null)) {
            return template.getCollisionHeight();
        }

        return defaultCollisionHeight;
    }

    public double getCollisionRadius(L2Character creature, double defaultCollisionRadius) {
        final TransformTemplate template = getTemplate(creature);
        if ((template != null) && (template.getCollisionRadius() != null)) {
            return template.getCollisionRadius();
        }

        return defaultCollisionRadius;
    }

    public void onTransform(L2Character creature, boolean addSkills) {
        // Abort attacking and casting.
        creature.abortAttack();
        creature.abortCast();

        final L2PcInstance player = creature.getActingPlayer();

        // Get off the strider or something else if character is mounted
        if (creature.isPlayer() && player.isMounted()) {
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

            if (creature.isPlayer()) {
                if (_name != null) {
                    player.getAppearance().setVisibleName(_name);
                }
                if (_title != null) {
                    player.getAppearance().setVisibleTitle(_title);
                }

                if (addSkills) {
                    //@formatter:off
                    // Add common skills.
                    template.getSkills()
                            .stream()
                            .map(SkillHolder::getSkill)
                            .forEach(player::addTransformSkill);

                    // Add skills depending on level.
                    template.getAdditionalSkills()
                            .stream()
                            .filter(h -> player.getLevel() >= h.getMinLevel())
                            .map(SkillHolder::getSkill)
                            .forEach(player::addTransformSkill);

                    // Add collection skills.
                    SkillTreesData.getInstance().getCollectSkillTree().values()
                            .stream()
                            .map(s -> player.getKnownSkill(s.getSkillId()))
                            .filter(Objects::nonNull)
                            .forEach(player::addTransformSkill);
                    //@formatter:on
                }

                // Set inventory blocks if needed.
                if (!template.getAdditionalItems().isEmpty()) {
                    final List<Integer> allowed = new ArrayList<>();
                    final List<Integer> notAllowed = new ArrayList<>();
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

                // Send basic action list.
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
            } else {
                creature.broadcastInfo();
            }

            // I don't know why, but you need to broadcast this to trigger the transformation client-side.
            // Usually should be sent naturally after applying effect, but sometimes is sent before that... i just dont know...
            creature.updateAbnormalVisualEffects();
        }
    }

    public void onUntransform(L2Character creature) {
        // Abort attacking and casting.
        creature.abortAttack();
        creature.abortCast();

        final TransformTemplate template = getTemplate(creature);
        if (template != null) {
            // Stop flying.
            if (isFlying()) {
                creature.setIsFlying(false);
            }

            if (creature.isPlayer()) {
                final L2PcInstance player = creature.getActingPlayer();
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

    public void onLevelUp(L2PcInstance player) {
        final TransformTemplate template = getTemplate(player);
        if (template != null) {
            // Add skills depending on level.
            if (!template.getAdditionalSkills().isEmpty()) {
                for (AdditionalSkillHolder holder : template.getAdditionalSkills()) {
                    if (player.getLevel() >= holder.getMinLevel()) {
                        if (player.getSkillLevel(holder.getSkillId()) < holder.getSkillLevel()) {
                            player.addTransformSkill(holder.getSkill());
                        }
                    }
                }
            }
        }
    }

    public WeaponType getBaseAttackType(L2Character creature, WeaponType defaultAttackType) {
        final TransformTemplate template = getTemplate(creature);
        if (template != null) {
            final WeaponType weaponType = template.getBaseAttackType();
            if (weaponType != null) {
                return weaponType;
            }
        }
        return defaultAttackType;
    }

    public double getStats(L2Character creature, Stats stats, double defaultValue) {
        double val = defaultValue;
        final TransformTemplate template = getTemplate(creature);
        if (template != null) {
            val = template.getStats(stats, defaultValue);
            final TransformLevelData data = template.getData(creature.getLevel());
            if (data != null) {
                val = data.getStats(stats, defaultValue);
            }
        }
        return val;
    }

    public int getBaseDefBySlot(L2PcInstance player, int slot) {
        final int defaultValue = player.getTemplate().getBaseDefBySlot(slot);
        final TransformTemplate template = getTemplate(player);

        return template == null ? defaultValue : template.getDefense(slot, defaultValue);
    }

    /**
     * @param creature
     * @return {@code -1} if this transformation doesn't alter levelmod, otherwise a new levelmod will be returned.
     */
    public double getLevelMod(L2Character creature) {
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
