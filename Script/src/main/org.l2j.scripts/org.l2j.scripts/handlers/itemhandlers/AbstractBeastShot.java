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
package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayeableChargeShots;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.Broadcast;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public abstract class AbstractBeastShot implements IItemHandler {

    @Override
    public boolean useItem(Playable playable, Item item, boolean forceUse) {
        var owner = playable.getActingPlayer();
        if (!owner.hasSummon()) {
            owner.sendPacket(SystemMessageId.SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return false;
        }

        var pet = playable.getPet();
        if (nonNull(pet) && pet.isDead()) {
            owner.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_SERVITOR_SAD_ISN_T_IT);
            return false;
        }

        var aliveServitors = playable.getServitors().values().stream().filter(Predicate.not(Creature::isDead)).collect(Collectors.toList());
        if (isNull(pet) && aliveServitors.isEmpty()) {
            owner.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_SERVITOR_SAD_ISN_T_IT);
            return false;
        }


        if (!item.hasSkills(ItemSkillType.NORMAL)) {
            LOGGER.warn("item {} is missing skills!", item);
            return false;
        }

        short shotConsumption = 0;
        var shotType = getShotType();

        if (nonNull(pet)) {
            if (!pet.isChargedShot(shotType)) {
                shotConsumption += pet.getSoulShotsPerHit();
            }
        }

        for (Summon servitor : aliveServitors) {
            if (!servitor.isChargedShot(shotType)) {
                shotConsumption += servitor.getSoulShotsPerHit();
            }
        }

        if(item.getCount() < shotConsumption) {
            return false;
        }

        if (nonNull(pet)) {
            chargeShot(owner, item, shotType, pet);
        }

        for (var servitor : aliveServitors) {
            chargeShot(owner, item, shotType, servitor);
        }
        return true;
    }

    private void chargeShot(Player owner, Item item, ShotType shotType, Summon summon) {
        sendUsesMessage(owner);
        summon.chargeShot(shotType, getBonus(summon));
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayeableChargeShots(summon, shotType, isBlessed()), owner);
        item.forEachSkill(ItemSkillType.NORMAL, s -> Broadcast.toSelfAndKnownPlayersInRadius(owner, new MagicSkillUse(summon, s.getSkill(), 0), 600));
    }

    protected abstract boolean isBlessed();

    protected abstract double getBonus(Summon summon);

    protected abstract ShotType getShotType();

    protected abstract void sendUsesMessage(Player player);

}
