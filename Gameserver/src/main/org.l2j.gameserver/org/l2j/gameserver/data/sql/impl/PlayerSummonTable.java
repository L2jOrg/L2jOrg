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
package org.l2j.gameserver.data.sql.impl;

import io.github.joealisson.primitive.*;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.gameserver.data.database.dao.SummonDAO;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.PetData;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.settings.CharacterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Nyaran
 * @author JoeAlisson
 */
public class PlayerSummonTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerSummonTable.class);

    private final IntIntMap pets = new CHashIntIntMap();
    private final IntMap<IntSet> servitors = new CHashIntMap<>();

    private PlayerSummonTable(){
    }

    public IntIntMap getPets() {
        return pets;
    }

    public IntMap<IntSet> getServitors() {
        return servitors;
    }

    public void init() {
        if (getSettings(CharacterSettings.class).restoreSummonOnReconnect()) {

            getDAO(SummonDAO.class).findAllSummonOwners().forEach(data -> servitors.computeIfAbsent(data.getOwnerId(), id ->  CHashIntMap.newKeySet()).add(data.getSummonId()));
            getDAO(PetDAO.class).findAllPetOwnersByRestore().forEach(data -> pets.put(data.getOwnerId(), data.getItemObjectId()));
        }
    }

    public void removeServitor(Player player, int summonObjectId) {
        if(servitors.containsKey(player.getObjectId())) {
            servitors.get(player.getObjectId()).remove(summonObjectId);
            getDAO(SummonDAO.class).deleteByIdAndOwner(summonObjectId, player.getObjectId());
        }
    }

    public void restorePet(Player player) {
        final Item item = player.getInventory().getItemByObjectId(pets.get(player.getObjectId()));
        if (isNull(item)) {
            LOGGER.warn("Null pet summoning item for {}", player);
            return;
        }
        final PetData petData = PetDataTable.getInstance().getPetDataByItemId(item.getId());
        if (isNull(petData)) {
            LOGGER.warn("Null pet data for: {} and summoning item: {}", player, item);
            return;
        }
        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(petData.getNpcId());
        if (isNull(npcTemplate)) {
            LOGGER.warn("Null pet NPC template for: {} and pet Id: {}", player,petData.getNpcId());
            return;
        }

        final Pet pet = Pet.spawnPet(npcTemplate, player, item);
        if (isNull(pet)) {
            LOGGER.warn("Null pet instance for: {} and pet NPC template: {}", player, npcTemplate);
            return;
        }

        pet.setShowSummonAnimation(true);
        pet.setTitle(player.getName());

        if (!pet.isRespawned()) {
            pet.setCurrentHp(pet.getMaxHp());
            pet.setCurrentMp(pet.getMaxMp());
            pet.getStats().setExp(pet.getExpForThisLevel());
            pet.setCurrentFed(pet.getMaxFed());
        }

        pet.setRunning();

        if (!pet.isRespawned()) {
            pet.storeMe();
        }

        item.setEnchantLevel(pet.getLevel());
        player.setPet(pet);
        pet.spawnMe(player.getX() + 50, player.getY() + 100, player.getZ());
        pet.startFeed();
        pet.setFollowStatus(true);
        pet.getOwner().sendPacket(new PetItemList(pet.getInventory().getItems()));
        pet.broadcastStatusUpdate();
    }

    public void restoreServitor(Player player) {
        getDAO(SummonDAO.class).findSummonsByOwner(player.getObjectId()).forEach(data -> {

            var skill = SkillEngine.getInstance().getSkill(data.getSummonSkillId(), player.getSkillLevel(data.getSummonSkillId()));
            if (isNull(skill)) {
                removeServitor(player, data.getSummonId());
                return;
            }
            skill.applyEffects(player, player);

            if (player.hasServitors()) {
                final Servitor summon = player.getServitors().values().stream().map(s -> ((Servitor) s)).filter(s -> s.getReferenceSkill() == data.getSummonSkillId()).findAny().orElse(null);
                if(nonNull(summon)) {
                    summon.setCurrentHp(data.getCurHp());
                    summon.setCurrentMp(data.getCurMp());
                    summon.setLifeTimeRemaining(data.getTime());
                }
            }
        });
    }

    public void saveSummon(Servitor summon) {
        if (isNull(summon ) || summon.getLifeTimeRemaining() <= 0) {
            return;
        }

        servitors.computeIfAbsent(summon.getOwner().getObjectId(), k -> CHashIntMap.newKeySet()).add(summon.getObjectId());
        getDAO(SummonDAO.class).save(summon.getOwner().getObjectId(), summon.getObjectId(), summon.getReferenceSkill(), (int) summon.getCurrentHp(), (int) summon.getCurrentMp(), summon.getLifeTime());
    }

    public static PlayerSummonTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PlayerSummonTable INSTANCE = new PlayerSummonTable();
    }
}
