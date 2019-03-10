package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.CropProcure;
import org.l2j.gameserver.mobius.gameserver.model.L2Seed;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author l3x
 */
public final class RequestSetCrop extends IClientIncomingPacket {
    private static final int BATCH_LENGTH = 21; // length of the one item

    private int _manorId;
    private List<CropProcure> _items;

    @Override
    public void readImpl(ByteBuffer packet) throws InvalidDataPacketException {
        _manorId = packet.getInt();
        final int count = packet.getInt();
        if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != packet.remaining())) {
            throw new InvalidDataPacketException();
        }

        _items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final int itemId = packet.getInt();
            final long sales = packet.getLong();
            final long price = packet.getLong();
            final int type = packet.get();
            if ((itemId < 1) || (sales < 0) || (price < 0)) {
                _items.clear();
                throw new InvalidDataPacketException();
            }

            if (sales > 0) {
                _items.add(new CropProcure(itemId, sales, type, sales, price));
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
        final L2PcInstance player = client.getActiveChar();
        if ((player == null) || (player.getClan() == null) || (player.getClan().getCastleId() != _manorId) || !player.hasClanPrivilege(ClanPrivilege.CS_MANOR_ADMIN) || !player.getLastFolkNPC().canInteract(player)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Filter crops with start amount lower than 0 and incorrect price
        final List<CropProcure> list = new ArrayList<>(_items.size());
        for (CropProcure cp : _items) {
            final L2Seed s = manor.getSeedByCrop(cp.getId(), _manorId);
            if ((s != null) && (cp.getStartAmount() <= s.getCropLimit()) && (cp.getPrice() >= s.getCropMinPrice()) && (cp.getPrice() <= s.getCropMaxPrice())) {
                list.add(cp);
            }
        }

        // Save crop list
        manor.setNextCropProcure(list, _manorId);
    }
}