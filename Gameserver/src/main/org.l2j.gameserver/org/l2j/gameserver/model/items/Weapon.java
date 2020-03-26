package org.l2j.gameserver.model.items;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillSee;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.constrain;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * This class is dedicated to the management of weapons.
 *
 * @author JoeAlisson
 */
public final class Weapon extends ItemTemplate implements EquipableItem {
    private WeaponType type;
    private boolean magic;
    private int soulShot;
    private int spiritShot;
    private int manaConsume;
    private int damageRadius;
    private int attackangle;
    private int changeWeapon;

    private int _reducedSoulshot;
    private int _reducedSoulshotChance;

    private int _reducedMpConsume;
    private int _reducedMpConsumeChance;

    private boolean isAttackWeapon;
    private boolean useWeaponSkillsOnly;

    public Weapon(int id, String name, WeaponType type, BodyPart bodyPart) {
        super(id, name);
        this.type = type;
        this.bodyPart = bodyPart;
        type1 = ItemTemplate.TYPE1_WEAPON_RING_EARRING_NECKLACE;
        type2 = ItemTemplate.TYPE2_WEAPON;
    }

    /**
     * @return the type of Weapon
     */
    @Override
    public WeaponType getItemType() {
        return type;
    }

    /**
     * @return the ID of the Etc item after applying the mask.
     */
    @Override
    public int getItemMask() {
        return type.mask();
    }

    /**
     * @return {@code true} if the weapon is magic, {@code false} otherwise.
     */
    @Override
    public boolean isMagicWeapon() {
        return magic;
    }

    /**
     * @return the quantity of SoulShot used.
     */
    public int getSoulShot() {
        return soulShot;
    }

    /**
     * @return the quantity of SpiritShot used.
     */
    public int getSpiritShotCount() {
        return spiritShot;
    }

    /**
     * @return the reduced quantity of SoultShot used.
     */
    public int getReducedSoulShot() {
        return _reducedSoulshot;
    }

    /**
     * @return the chance to use Reduced SoultShot.
     */
    public int getReducedSoulShotChance() {
        return _reducedSoulshotChance;
    }

    /**
     * @return the MP consumption with the weapon.
     */
    public int getMpConsume() {
        return manaConsume;
    }

    public int getBaseAttackRadius() {
        return damageRadius;
    }

    public int getBaseAttackAngle() {
        return attackangle;
    }

    /**
     * @return the reduced MP consumption with the weapon.
     */
    public int getReducedMpConsume() {
        return _reducedMpConsume;
    }

    /**
     * @return the chance to use getReducedMpConsume()
     */
    public int getReducedMpConsumeChance() {
        return _reducedMpConsumeChance;
    }

    /**
     * @return the Id in which weapon this weapon can be changed.
     */
    public int getChangeWeaponId() {
        return changeWeapon;
    }

    /**
     * @return {@code true} if the weapon is attack weapon, {@code false} otherwise.
     */
    public boolean isAttackWeapon() {
        return isAttackWeapon;
    }

    /**
     * @return {@code true} if the weapon is skills only, {@code false} otherwise.
     */
    public boolean useWeaponSkillsOnly() {
        return useWeaponSkillsOnly;
    }

    /**
     * @param caster  the Creature pointing out the caster
     * @param target  the Creature pointing out the target
     * @param trigger trigger skill
     * @param type type of skill
     */
    public void applyConditionalSkills(Creature caster, Creature target, Skill trigger, ItemSkillType type) {
        forEachSkill(type, holder ->
        {
            final Skill skill = holder.getSkill();
            if (Rnd.get(100) >= holder.getChance()) {
                return;
            }

            if (type == ItemSkillType.ON_MAGIC_SKILL) {
                // Trigger only if both are good or bad magic.
                if (trigger.isBad() != skill.isBad()) {
                    return;
                }

                // No Trigger if not Magic Skill or is toggle
                if (trigger.isMagic() != skill.isMagic()) {
                    return;
                }

                // No Trigger if skill is toggle
                if (trigger.isToggle()) {
                    return;
                }

                if (skill.isBad() && (Formulas.calcShldUse(caster, target) == Formulas.SHIELD_DEFENSE_PERFECT_BLOCK)) {
                    return;
                }
            }

            // Skill condition not met
            if (!skill.checkCondition(caster, target)) {
                return;
            }

            skill.activateSkill(caster, target);

            // TODO: Verify if this applies ONLY to ON_MAGIC_SKILL!
            if (type == ItemSkillType.ON_MAGIC_SKILL) {
                // notify quests of a skill use
                if (isPlayer(caster)) {
                    World.getInstance().forEachVisibleObjectInRange(caster, Npc.class, 1000, npc -> EventDispatcher.getInstance().notifyEventAsync(new OnNpcSkillSee(npc, caster.getActingPlayer(), skill, false, target), npc));
                }
                if (isPlayer(caster)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ACTIVATED);
                    sm.addSkillName(skill);
                    caster.sendPacket(sm);
                }
            }
        });
    }

    public void setMagic(boolean magic) {
        this.magic = magic;
    }

    public void setSoulshots(int soulshots) {
        this.soulShot = soulshots;
    }

    public void setSpiritshots(int spiritshots) {
        this.spiritShot = spiritshots;
    }

    public void setManaConsume(int mana) {
        this.manaConsume = mana;
    }

    public void setDamageRadius(int radius) {
        this.damageRadius = radius;
    }

    public void setDamageAngle(int angle) {
        this.attackangle = angle;
    }

    public void setEnchantable(Boolean enchantable) {
        this.enchantable = enchantable;
    }

    public void setChangeWeapon(int changeWeapon) {
        this.changeWeapon = changeWeapon;
    }

    public void setCanAttack(Boolean canAttack) {
        isAttackWeapon = canAttack;
    }

    public void setRestrictSkills(Boolean restrictSkills) {
        useWeaponSkillsOnly = restrictSkills;
    }

    public void setEquipReuseDelay(int equipReuseDelay) {
        this.equipReuseDelay = equipReuseDelay;
    }

    public void setCrystalType(CrystalType crystalType) {
        this.crystalType = crystalType;
    }

    public void setCrystalCount(int count) {
        this.crystalCount = count;
    }

    public int getConsumeShotsCount() {
        int count = switch (crystalType) {
                case S -> 4;
                case A -> 3;
                case B -> 2;
                default -> 1;
            };

        if(type == WeaponType.BOW) {
            count ++;
        }
        return count;
    }
}
