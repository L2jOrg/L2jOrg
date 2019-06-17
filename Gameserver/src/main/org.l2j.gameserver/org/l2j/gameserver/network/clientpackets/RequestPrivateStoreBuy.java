package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.OfflineTradersTable;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.ItemRequest;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;


/**
 * This class ...
 *
 * @version $Revision: 1.2.2.1.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestPrivateStoreBuy extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPrivateStoreBuy.class);
    private static final int BATCH_LENGTH = 20; // length of the one item

    private int _storePlayerId;
    private Set<ItemRequest> _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _storePlayerId = readInt();
        final int count = readInt();
        if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != available())) {
            throw new InvalidDataPacketException();
        }
        _items = new HashSet<>();

        for (int i = 0; i < count; i++) {
            final int objectId = readInt();
            final long cnt = readLong();
            final long price = readLong();

            if ((objectId < 1) || (cnt < 1) || (price < 0)) {
                _items = null;
                throw new InvalidDataPacketException();
            }

            _items.add(new ItemRequest(objectId, cnt, price));
        }
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (_items == null) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Cannot set private store in Ceremony of Chaos event.
        if (player.isOnEvent(CeremonyOfChaosEvent.class)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_OR_WORKSHOP_IN_THE_CEREMONY_OF_CHAOS);
            return;
        }

        if (player.isOnEvent()) // custom event message
        {
            player.sendMessage("You cannot open a private store while participating in an event.");
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("privatestorebuy")) {
            player.sendMessage("You are buying items too fast.");
            return;
        }

        final L2Object object = L2World.getInstance().getPlayer(_storePlayerId);
        if ((object == null) || player.isCursedWeaponEquipped()) {
            return;
        }

        final L2PcInstance storePlayer = (L2PcInstance) object;
        if (!player.isInsideRadius3D(storePlayer, L2Npc.INTERACTION_DISTANCE)) {
            return;
        }

        if (player.getInstanceWorld() != storePlayer.getInstanceWorld()) {
            return;
        }

        if (!((storePlayer.getPrivateStoreType() == PrivateStoreType.SELL) || (storePlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL))) {
            return;
        }

        final TradeList storeList = storePlayer.getSellList();
        if (storeList == null) {
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (storePlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL) {
            if (storeList.getItemCount() > _items.size()) {
                final String msgErr = "[RequestPrivateStoreBuy] player " + client.getActiveChar().getName() + " tried to buy less items than sold by package-sell, ban this player for bot usage!";
                Util.handleIllegalPlayerAction(client.getActiveChar(), msgErr, Config.DEFAULT_PUNISH);
                return;
            }
        }

        final int result = storeList.privateStoreBuy(player, _items);
        if (result > 0) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            if (result > 1) {
                LOGGER.warn("PrivateStore buy has failed due to invalid list or request. Player: " + player.getName() + ", Private store of: " + storePlayer.getName());
            }
            return;
        }

        // Update offline trade record, if realtime saving is enabled
        if (Config.OFFLINE_TRADE_ENABLE && Config.STORE_OFFLINE_TRADE_IN_REALTIME && ((storePlayer.getClient() == null) || storePlayer.getClient().isDetached())) {
            OfflineTradersTable.onTransaction(storePlayer, storeList.getItemCount() == 0, false);
        }

        if (storeList.getItemCount() == 0) {
            storePlayer.setPrivateStoreType(PrivateStoreType.NONE);
            storePlayer.broadcastUserInfo();
        }
    }
}
