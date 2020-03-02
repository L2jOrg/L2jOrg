package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.ManufactureItem;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.RecipeShopMsg;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameUtils;

import java.util.Arrays;
import java.util.List;

import static org.l2j.gameserver.model.itemcontainer.Inventory.MAX_ADENA;

/**
 * RequestRecipeShopListSet client packet class.
 */
public final class RequestRecipeShopListSet extends ClientPacket {
    private static final int BATCH_LENGTH = 12;

    private ManufactureItem[] _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        final int count = readInt();
        if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != available())) {
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
            _items[i] = new ManufactureItem(id, cost);
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (_items == null) {
            player.setPrivateStoreType(PrivateStoreType.NONE);
            player.broadcastUserInfo();
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

        for (ManufactureItem i : _items) {
            final RecipeList list = RecipeData.getInstance().getRecipeList(i.getRecipeId());
            if (!dwarfRecipes.contains(list) && !commonRecipes.contains(list)) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Player " + player.getName() + " of account " + player.getAccountName() + " tried to set recipe which he dont have.");
                return;
            }

            if (i.getCost() > MAX_ADENA) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to set price more than " + MAX_ADENA + " adena in Private Manufacture.");
                return;
            }

            player.getManufactureItems().put(i.getRecipeId(), i);
        }

        player.setStoreName(!player.hasManufactureShop() ? "" : player.getStoreName());
        player.setPrivateStoreType(PrivateStoreType.MANUFACTURE);
        player.sitDown();
        player.broadcastUserInfo();
        Broadcast.toSelfAndKnownPlayers(player, new RecipeShopMsg(player));
    }
}
