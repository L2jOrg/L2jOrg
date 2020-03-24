package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.gameserver.model.Seed;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

import java.util.ArrayList;
import java.util.List;

/**
 * @author l3x
 */
public final class RequestSetCrop extends ClientPacket {
    private static final int BATCH_LENGTH = 21; // length of the one item

    private int _manorId;
    private List<CropProcure> _items;

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
            final int type = readByte();
            if ((itemId < 1) || (sales < 0) || (price < 0)) {
                _items.clear();
                throw new InvalidDataPacketException();
            }

            if (sales > 0) {
                _items.add(new CropProcure(itemId, sales, type, sales, price, _manorId, true));
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

        // Filter crops with start amount lower than 0 and incorrect price
        final List<CropProcure> list = new ArrayList<>(_items.size());
        for (CropProcure cp : _items) {
            final Seed s = manor.getSeedByCrop(cp.getSeedId(), _manorId);
            if ((s != null) && (cp.getStartAmount() <= s.getCropLimit()) && (cp.getPrice() >= s.getCropMinPrice()) && (cp.getPrice() <= s.getCropMaxPrice())) {
                list.add(cp);
            }
        }

        // Save crop list
        manor.setNextCropProcure(list, _manorId);
    }
}