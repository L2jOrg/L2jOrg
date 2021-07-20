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
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayeableChargeShots;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.Broadcast;

/**
 * @author JoeAlisson
 */
public abstract class AbstractShot implements IItemHandler {

    @Override
    public boolean useItem(Playable playable, Item item, boolean forceUse) {
        var player = playable.getActingPlayer();

        if (!item.hasSkills(ItemSkillType.NORMAL)) {
            LOGGER.warn("item {} is missing skills!", item);
            return false;
        }

        // Check if Soul shot can be used
        if (!canUse(player)) {
            player.sendPacket(cantUseMessage());
            return false;
        }

        player.chargeShot(getShotType(), getBonus(player));
        player.sendPacket(getEnabledShotsMessage());
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayeableChargeShots(player, getShotType(), isBlessed()), player);
        item.forEachSkill(ItemSkillType.NORMAL, s -> Broadcast.toSelfAndKnownPlayersInRadius(player, new MagicSkillUse(player, s.skill(), 0), 600));
        return true;
    }

    protected abstract boolean isBlessed();

    protected abstract double getBonus(Player player);

    protected abstract boolean canUse(Player player);

    protected abstract ShotType getShotType();

    protected abstract SystemMessageId getEnabledShotsMessage();

    protected abstract SystemMessageId cantUseMessage();

}
