package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.commission.CommissionItemType;
import org.l2j.gameserver.model.commission.CommissionTreeType;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;

import java.nio.ByteBuffer;
import java.util.function.Predicate;

/**
 * @author NosBit
 */
public class RequestCommissionList extends IClientIncomingPacket {
    private int _treeViewDepth;
    private int _itemType;
    private int _type;
    private int _grade;
    private String _query;

    @Override
    public void readImpl(ByteBuffer packet) {
        _treeViewDepth = packet.getInt();
        _itemType = packet.getInt();
        _type = packet.getInt();
        _grade = packet.getInt();
        _query = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (!CommissionManager.isPlayerAllowedToInteract(player)) {
            client.sendPacket(ExCloseCommission.STATIC_PACKET);
            return;
        }

        Predicate<L2Item> filter = i -> true;
        switch (_treeViewDepth) {
            case 1: {
                final CommissionTreeType commissionTreeType = CommissionTreeType.findByClientId(_itemType);
                if (commissionTreeType != null) {
                    filter = filter.and(i -> commissionTreeType.getCommissionItemTypes().contains(i.getCommissionItemType()));
                }
                break;
            }
            case 2: {
                final CommissionItemType commissionItemType = CommissionItemType.findByClientId(_itemType);
                if (commissionItemType != null) {
                    filter = filter.and(i -> i.getCommissionItemType() == commissionItemType);
                }
                break;
            }
        }

        switch (_type) {
            case 0: // General
            {
                filter = filter.and(i -> true); // TODO: condition
                break;
            }
            case 1: // Rare
            {
                filter = filter.and(i -> true); // TODO: condition
                break;
            }
        }

        switch (_grade) {
            case 0: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.NONE);
                break;
            }
            case 1: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.D);
                break;
            }
            case 2: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.C);
                break;
            }
            case 3: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.B);
                break;
            }
            case 4: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.A);
                break;
            }
        }

        filter = filter.and(i -> _query.isEmpty() || i.getName().toLowerCase().contains(_query.toLowerCase()));

        CommissionManager.getInstance().showAuctions(player, filter);
    }
}
