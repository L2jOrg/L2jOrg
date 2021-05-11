/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.network.clientpackets.ensoul;

import org.l2j.gameserver.engine.item.EnsoulOption;
import org.l2j.gameserver.engine.item.EnsoulType;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.item.ItemEnsoulEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.ensoul.ExEnSoulExtractionResult;

import java.util.Collection;

import static java.util.Objects.isNull;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class RequestTryEnSoulExtraction extends ClientPacket {
    private int _itemObjectId;
    private EnsoulType type;

    @Override
    public void readImpl() {
        _itemObjectId = readInt();
        type = EnsoulType.from(readByte());
    }

    @Override
    public void runImpl() {
        Player player = client.getPlayer();
        if (isNull(player)) {
            return;
        }

        final Item item = player.getInventory().getItemByObjectId(_itemObjectId);
        if (isNull(item)) {
            return;
        }

        EnsoulOption option = switch (type) {
            case COMMON -> item.getSpecialAbility();
            case SPECIAL -> item.getAdditionalSpecialAbility();
        };

        if (isNull(option)) {
            return;
        }

        final Collection<ItemHolder> removalFee = ItemEnsoulEngine.getInstance().getRemovalFee(item.getTemplate().getCrystalType());
        if (removalFee.isEmpty()) {
            return;
        }

        for (ItemHolder itemHolder : removalFee) {
            if (player.getInventory().getInventoryItemCount(itemHolder.getId(), -1) < itemHolder.getCount()) {
                player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
                player.sendPacket(new ExEnSoulExtractionResult(false, item));
                return;
            }
        }

        for (ItemHolder itemHolder : removalFee) {
            player.destroyItemByItemId("Rune Extract", itemHolder.getId(), itemHolder.getCount(), player, true);
        }

        // Remove equipped rune.
        item.removeSpecialAbility(type);
        final InventoryUpdate iu = new InventoryUpdate(item);

        // Add rune in player inventory.
        final int runeId = ItemEnsoulEngine.getInstance().getStone(type, option.id());
        if (runeId > 0) {
            iu.addItem(player.addItem("Rune Extract", runeId, 1, player, true));
        }

        player.sendInventoryUpdate(iu);
        player.sendPacket(new ExEnSoulExtractionResult(true, item));
    }
}