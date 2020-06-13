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
package handlers.effecthandlers;

import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.PetData;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.holders.PetItemHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PetItemList;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Summon Pet effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class SummonPet extends AbstractEffect {

    private SummonPet() {
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.SUMMON_PET;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effector) || !isPlayer(effected) || effected.isAlikeDead()) {
            return;
        }

        final Player player = effector.getActingPlayer();

        if (player.hasPet() || player.isMounted()) {
            player.sendPacket(SystemMessageId.YOU_ALREADY_HAVE_A_PET);
            return;
        }

        final PetItemHolder holder = player.removeScript(PetItemHolder.class);
        if (isNull(holder)) {
            LOGGER.warn("Summoning pet without attaching PetItemHandler!", new Throwable());
            return;
        }

        final Item collar = holder.getItem();
        if (player.getInventory().getItemByObjectId(collar.getObjectId()) != collar) {
            LOGGER.warn("Player: {} is trying to summon pet from item that he doesn't owns.", player);
            return;
        }

        final PetData petData = PetDataTable.getInstance().getPetDataByItemId(collar.getId());
        if (isNull(petData ) || (petData.getNpcId() == -1)) {
            return;
        }

        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(petData.getNpcId());
        final Pet pet = Pet.spawnPet(npcTemplate, player, collar);

        pet.setShowSummonAnimation(true);
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

        collar.setEnchantLevel(pet.getLevel());
        player.setPet(pet);
        pet.spawnMe(player.getX() + 50, player.getY() + 100, player.getZ());
        pet.startFeed();
        pet.setFollowStatus(true);
        pet.getOwner().sendPacket(new PetItemList(pet.getInventory().getItems()));
        pet.broadcastStatusUpdate();
    }

    public static class Factory implements SkillEffectFactory {

        private static final SummonPet INSTANCE = new SummonPet();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "SummonPet";
        }
    }
}
