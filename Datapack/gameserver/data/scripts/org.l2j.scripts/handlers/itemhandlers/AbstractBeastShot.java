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
package handlers.itemhandlers;

import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayeableChargeShots;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.Broadcast;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;

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

        var skills = item.getSkills(ItemSkillType.NORMAL);
        if (isNullOrEmpty(skills)) {
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
            chargeShot(owner, skills, shotType, pet);
        }

        aliveServitors.forEach(s -> chargeShot(owner, skills, shotType, s));
        return true;
    }

    private void chargeShot(Player owner, List<ItemSkillHolder> skills, ShotType shotType, Summon s) {
        sendUsesMessage(owner);
        s.chargeShot(shotType, getBonus(s));
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayeableChargeShots(s, shotType, isBlessed()), owner);
        skills.forEach(holder -> Broadcast.toSelfAndKnownPlayersInRadius(owner, new MagicSkillUse(s, s, holder.getSkillId(), holder.getLevel(), 0, 0), 600));
    }

    protected abstract boolean isBlessed();

    protected abstract double getBonus(Summon summon);

    protected abstract ShotType getShotType();

    protected abstract void sendUsesMessage(Player player);

}
