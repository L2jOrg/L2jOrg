package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.Seed;
import org.l2j.gameserver.data.database.data.SeedProduction;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

import java.util.ArrayList;
import java.util.List;

/**
 * @author l3x
 * @author JoeAlisson
 */
public class RequestSetSeed extends ClientPacket {
    private static final int BATCH_LENGTH = 20; // length of the one item

    private int _manorId;
    private List<SeedProduction> _items;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _manorId = readInt();
        final int count = readInt();
        if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != available())) {
            throw new InvalidDataPacketException();
        }

        _items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final int itemId = readInt();
            final long sales = readLong();
            final long price = readLong();
            if ((itemId < 1) || (sales < 0) || (price < 0)) {
                _items.clear();
                throw new InvalidDataPacketException();
            }

            if (sales > 0) {
                _items.add(new SeedProduction(itemId, sales, price, sales, _manorId, true));
            }
        }
    }

    @Override
    public void runImpl() {
        if (_items.isEmpty()) {
            return;
        }

        final CastleManorManager manor = CastleManorManager.getInstance();
        if (!manor.isModifiablePeriod()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Check player privileges
        final Player player = client.getPlayer();
        if ((player == null) || (player.getClan() == null) || (player.getClan().getCastleId() != _manorId) || !player.hasClanPrivilege(ClanPrivilege.CS_MANOR_ADMIN) || !player.getLastFolkNPC().canInteract(player)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Filter seeds with start amount lower than 0 and incorrect price
        final List<SeedProduction> list = new ArrayList<>(_items.size());
        for (SeedProduction sp : _items) {
            final Seed s = manor.getSeed(sp.getSeedId());
            if ((s != null) && (sp.getStartAmount() <= s.getSeedLimit()) && (sp.getPrice() >= s.getSeedMinPrice()) && (sp.getPrice() <= s.getSeedMaxPrice())) {
                list.add(sp);
            }
        }

        manor.setNextSeedProduction(list, _manorId);
    }

}