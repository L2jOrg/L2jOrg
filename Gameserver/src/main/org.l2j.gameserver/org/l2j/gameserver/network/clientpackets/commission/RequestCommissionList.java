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
package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.commission.CommissionItemType;
import org.l2j.gameserver.model.commission.CommissionTreeType;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;

import java.util.function.Predicate;

/**
 * @author NosBit
 */
public class RequestCommissionList extends ClientPacket {
    private int _treeViewDepth;
    private int _itemType;
    private int _type;
    private int _grade;
    private String _query;

    @Override
    public void readImpl() {
        _treeViewDepth = readInt();
        _itemType = readInt();
        _type = readInt();
        _grade = readInt();
        _query = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!CommissionManager.isPlayerAllowedToInteract(player)) {
            client.sendPacket(ExCloseCommission.STATIC_PACKET);
            return;
        }

        Predicate<ItemTemplate> filter = i -> true;
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
            case 5: {
                filter = filter.and(i -> i.getCrystalType() == CrystalType.S);
                break;
            }
        }

        filter = filter.and(i -> _query.isEmpty() || i.getName().toLowerCase().contains(_query.toLowerCase()));

        CommissionManager.getInstance().showAuctions(player, filter);
    }
}
