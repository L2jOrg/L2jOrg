package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillConditionScope;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public final class ItemSkillsListener implements PlayerInventoryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemSkillsListener.class);

    private ItemSkillsListener() {

    }

    @Override
    public void notifyUnequiped(int slot, Item item, Inventory inventory) {
        if (!isPlayer(inventory.getOwner())) {
            return;
        }

        final Player player = (Player) inventory.getOwner();
        final ItemTemplate it = item.getTemplate();
        final AtomicBoolean update = new AtomicBoolean();
        final AtomicBoolean updateTimestamp = new AtomicBoolean();

        // Remove augmentation bonuses on unequip
        if (item.isAugmented()) {
            item.getAugmentation().removeBonus(player);
        }

        // Recalculate all stats
        player.getStat().recalculateStats(true);

        it.forEachSkill(ItemSkillType.ON_ENCHANT, holder ->
        {
            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }
            // Remove skills bestowed from +4 armor
            if (item.getEnchantLevel() >= holder.getValue()) {
                player.removeSkill(holder.getSkill(), false, holder.getSkill().isPassive());
                update.compareAndSet(false, true);
            }
        });

        // Clear enchant bonus
        item.clearEnchantStats();

        // Clear SA Bonus
        item.clearSpecialAbilities();

        it.forEachSkill(ItemSkillType.NORMAL, holder ->
        {
            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }

            final Skill Skill = holder.getSkill();

            if (Skill != null) {
                player.removeSkill(Skill, false, Skill.isPassive());
                update.compareAndSet(false, true);
            } else {
                LOGGER.warn("Inventory.ItemSkillsListener.Weapon: Incorrect skill: " + holder);
            }
        });

        if (item.isArmor()) {
            for (Item itm : inventory.getItems()) {
                if (!itm.isEquipped() || (itm.getTemplate().getSkills(ItemSkillType.NORMAL) == null) || itm.equals(item)) {
                    continue;
                }

                itm.getTemplate().forEachSkill(ItemSkillType.NORMAL, holder ->
                {

                    if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                        return;
                    }

                    if (player.getSkillLevel(holder.getSkillId()) != 0) {
                        return;
                    }

                    final Skill skill = holder.getSkill();
                    if (skill != null) {
                        player.addSkill(skill, false);

                        if (skill.isActive()) {
                            if (!player.hasSkillReuse(skill.getReuseHashCode())) {
                                final int equipDelay = item.getEquipReuseDelay();
                                if (equipDelay > 0) {
                                    player.addTimeStamp(skill, equipDelay);
                                    player.disableSkill(skill, equipDelay);
                                }
                                updateTimestamp.compareAndSet(false, true);
                            }
                        }
                        update.compareAndSet(false, true);
                    }
                });
            }
        }

        // Must check all equipped item for enchant conditions.
        for (Item equipped : inventory.getPaperdollItems())
        {
            equipped.getTemplate().forEachSkill(ItemSkillType.ON_ENCHANT, holder ->
            {
                // Add skills bestowed from +4 armor
                if (equipped.getEnchantLevel() >= holder.getValue())
                {
                    final Skill skill = holder.getSkill();
                    // Check passive skill conditions.
                    if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
                    {
                        player.removeSkill(holder.getSkill(), false, holder.getSkill().isPassive());
                        update.compareAndSet(false, true);
                    }
                }
            });
        }
        // Must check for toggle skill item conditions.
        for (Skill skill : player.getAllSkills())
        {
            if (skill.isToggle() && player.isAffectedBySkill(skill.getId()) && !skill.checkConditions(SkillConditionScope.GENERAL, player, player))
            {
                player.stopSkillEffects(true, skill.getId());
                update.compareAndSet(false, true);
            }
        }

        // Apply skill, if weapon have "skills on unequip"
        it.forEachSkill(ItemSkillType.ON_UNEQUIP, holder -> {

            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }
            holder.getSkill().activateSkill(player, player);
        });

        if (update.get()) {
            player.sendSkillList();
        }
        if (updateTimestamp.get()) {
            player.sendPacket(new SkillCoolTime(player));
        }

        if (item.isWeapon()) {
            player.unchargeAllShots();
        }
    }

    @Override
    public void notifyEquiped(int slot, Item item, Inventory inventory) {
        if (!isPlayer(inventory.getOwner())) {
            return;
        }

        final Player player = (Player) inventory.getOwner();

        // Any item equipped that result in expertise penalty do not give any skills at all.
        if (item.getTemplate().getCrystalType().getId() > player.getExpertiseLevel()) {
            return;
        }

        final AtomicBoolean update = new AtomicBoolean();
        final AtomicBoolean updateTimestamp = new AtomicBoolean();

        // Apply augmentation bonuses on equip
        if (item.isAugmented()) {
            item.getAugmentation().applyBonus(player);
        }

        // Recalculate all stats
        player.getStat().recalculateStats(true);

        item.getTemplate().forEachSkill(ItemSkillType.ON_ENCHANT, holder -> {

            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }
            // Add skills bestowed from +4 armor
            if (item.getEnchantLevel() >= holder.getValue()) {
                final Skill skill = holder.getSkill();
                // Check passive skill conditions.
                if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
                {
                    return;
                }
                player.addSkill(skill, false);
                update.compareAndSet(false, true);
            }
        });

        // Apply enchant stats
        item.applyEnchantStats();

        // Apply SA skill
        item.applySpecialAbilities();

        item.getTemplate().forEachSkill(ItemSkillType.NORMAL, holder ->
        {

            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }

            final Skill skill = holder.getSkill();
            if (skill != null) {

                if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
                {
                    return;
                }
                player.addSkill(skill, false);

                if (skill.isActive()) {
                    if (!player.hasSkillReuse(skill.getReuseHashCode())) {
                        final int equipDelay = item.getEquipReuseDelay();
                        if (equipDelay > 0) {
                            player.addTimeStamp(skill, equipDelay);
                            player.disableSkill(skill, equipDelay);
                        }
                        updateTimestamp.compareAndSet(false, true);
                    }
                }
                update.compareAndSet(false, true);
            } else {
                LOGGER.warn("Inventory.ItemSkillsListener.Weapon: Incorrect skill: " + holder);
            }
        });

        // Must check all equipped item for enchant conditions.
        for (Item equipped : inventory.getPaperdollItems())
        {
            equipped.getTemplate().forEachSkill(ItemSkillType.ON_ENCHANT, holder ->
            {
                // Add skills bestowed from +4 armor
                if (equipped.getEnchantLevel() >= holder.getValue())
                {
                    final Skill skill = holder.getSkill();
                    // Check passive skill conditions.
                    if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
                    {
                        return;
                    }
                    player.addSkill(skill, false);
                    update.compareAndSet(false, true);
                }
            });
        }

        // Apply skill, if weapon have "skills on equip"
        item.getTemplate().forEachSkill(ItemSkillType.ON_EQUIP, holder -> {

            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }

            holder.getSkill().activateSkill(player, player);
        });

        if (update.get()) {
            player.sendSkillList();
        }
        if (updateTimestamp.get()) {
            player.sendPacket(new SkillCoolTime(player));
        }
    }

    private boolean verifySkillActiveIfAddtionalAgathion(int slot, ItemSkillHolder holder) {
        if(slot > Inventory.PAPERDOLL_AGATHION1 && slot <= Inventory.PAPERDOLL_AGATHION5) {
            return holder.getSkill().isActive();
        }
        return false;
    }

    public static ItemSkillsListener provider() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final ItemSkillsListener INSTANCE = new ItemSkillsListener();
    }
}
