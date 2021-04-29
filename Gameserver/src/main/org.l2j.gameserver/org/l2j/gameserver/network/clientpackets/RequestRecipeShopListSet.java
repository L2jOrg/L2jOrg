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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.ManufactureItem;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.RecipeShopMsg;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.Arrays;
import java.util.List;

/**
 * RequestRecipeShopListSet client packet class.
 */
public final class RequestRecipeShopListSet extends ClientPacket {
    private static final int BATCH_LENGTH = 12;

    private ManufactureItem[] _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        final int count = readInt();
        if (count <= 0 || count > CharacterSettings.maxItemInPacket() || count * BATCH_LENGTH != available()) {
            throw new InvalidDataPacketException();
        }

        _items = new ManufactureItem[count];
        for (int i = 0; i < count; i++) {
            final int id = readInt();
            final long cost = readLong();
            if (cost < 0) {
                _items = null;
                throw new InvalidDataPacketException();
            }
            _items[i] = ManufactureItem.of(id, cost);
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player) || player.isInDuel()) {
            client.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (player.isInsideZone(ZoneType.NO_STORE)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final List<RecipeList> dwarfRecipes = Arrays.asList(player.getDwarvenRecipeBook());
        final List<RecipeList> commonRecipes = Arrays.asList(player.getCommonRecipeBook());

        player.getManufactureItems().clear();

        var maxAdena = CharacterSettings.maxAdena();
        for (ManufactureItem item : _items) {
            final RecipeList list = RecipeData.getInstance().getRecipeList(item.getRecipeId());
            if (!dwarfRecipes.contains(list) && !commonRecipes.contains(list)) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! " + player + " tried to set recipe which he dont have.");
                return;
            }

            if (item.getPrice() > maxAdena) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! " + player + " tried to set price more than " + maxAdena + " adena in Private Manufacture.");
                return;
            }

            item.setDwarven(list.isDwarvenRecipe());
            item.setOwner(player.getObjectId());
            player.getManufactureItems().put(item.getRecipeId(), item);
        }

        player.setStoreName(!player.hasManufactureShop() ? "" : player.getStoreName());
        player.setPrivateStoreType(PrivateStoreType.MANUFACTURE);
        player.sitDown();
        player.broadcastUserInfo();
        Broadcast.toSelfAndKnownPlayers(player, new RecipeShopMsg(player));
    }
}
