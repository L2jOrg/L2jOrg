package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.SkillConditionScope;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.ToIntFunction;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public final class ArmorSetListener implements PlayerInventoryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArmorSetListener.class);

    private ArmorSetListener() {

    }

    private static boolean applySkills(Player player, Item item, ArmorSet armorSet, ToIntFunction<Item> idProvider) {
        final long piecesCount = armorSet.getPiecesCount(player, idProvider);
        if (piecesCount >= armorSet.getMinimumPieces()) {
            // Applying all skills that matching the conditions
            final AtomicBoolean updateTimeStamp = new AtomicBoolean();
            final AtomicBoolean update = new AtomicBoolean();
            for (ArmorsetSkillHolder holder : armorSet.getSkills()) {
                if (holder.validateConditions(player, armorSet, idProvider)) {
                    final Skill itemSkill = holder.getSkill();

                    if (itemSkill == null) {
                        LOGGER.warn("Inventory.ArmorSetListener.addSkills: Incorrect skill: " + holder);
                        continue;
                    }

                    if (itemSkill.isPassive() && !itemSkill.checkConditions(SkillConditionScope.PASSIVE, player, player))
                    {
                        continue;
                    }

                    player.addSkill(itemSkill, false);
                    if (itemSkill.isActive() && (item != null)) {
                        if (!player.hasSkillReuse(itemSkill.getReuseHashCode())) {
                            final int equipDelay = item.getEquipReuseDelay();
                            if (equipDelay > 0) {
                                player.addTimeStamp(itemSkill, equipDelay);
                                player.disableSkill(itemSkill, equipDelay);
                            }
                        }
                        updateTimeStamp.compareAndSet(false, true);
                    }
                    update.compareAndSet(false, true);
                }
            }
            if (updateTimeStamp.get()) {
                player.sendPacket(new SkillCoolTime(player));
            }
            return update.get();
        }
        return false;
    }

    private static boolean verifyAndApply(Player player, Item item, ToIntFunction<Item> idProvider) {
        boolean update = false;
        final List<ArmorSet> armorSets = ArmorSetsData.getInstance().getSets(idProvider.applyAsInt(item));
        for (ArmorSet armorSet : armorSets) {
            if (applySkills(player, item, armorSet, idProvider)) {
                update = true;
            }
        }
        return update;
    }

    private static boolean verifyAndRemove(Player player, Item item, ToIntFunction<Item> idProvider) {
        boolean update = false;
        final List<ArmorSet> armorSets = ArmorSetsData.getInstance().getSets(idProvider.applyAsInt(item));
        for (ArmorSet armorSet : armorSets) {
            // Remove all skills that doesn't matches the conditions
            for (ArmorsetSkillHolder holder : armorSet.getSkills()) {
                if (!holder.validateConditions(player, armorSet, idProvider)) {
                    final Skill itemSkill = holder.getSkill();
                    if (itemSkill == null) {
                        LOGGER.warn("Inventory.ArmorSetListener.removeSkills: Incorrect skill: " + holder);
                        continue;
                    }

                    // Update if a skill has been removed.
                    if (player.removeSkill(itemSkill, false, itemSkill.isPassive()) != null) {
                        update = true;
                    }
                }
            }

            // Attempt to apply lower level skills if possible
            if (applySkills(player, item, armorSet, idProvider)) {
                update = true;
            }
        }

        return update;
    }

    @Override
    public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
        if (!isPlayer(inventory.getOwner())) {
            return;
        }

        final Player player = (Player) inventory.getOwner();
        boolean update = false;

        // Verify and apply normal set
        if (verifyAndApply(player, item, Item::getId)) {
            update = true;
        }

        if (update) {
            player.sendSkillList();
        }
    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        if (!isPlayer(inventory.getOwner())) {
            return;
        }

        final Player player = (Player) inventory.getOwner();
        boolean remove = false;

        // verify and remove normal set bonus
        if (verifyAndRemove(player, item, Item::getId)) {
            remove = true;
        }

        if (remove) {
            player.checkItemRestriction();
            player.sendSkillList();
        }
    }

    public static ArmorSetListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ArmorSetListener INSTANCE = new ArmorSetListener();
    }
}