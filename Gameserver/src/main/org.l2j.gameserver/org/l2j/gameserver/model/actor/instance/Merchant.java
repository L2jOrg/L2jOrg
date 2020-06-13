/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.BuyList;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.util.GameUtils;

/**
 * This class ...
 *
 * @version $Revision: 1.10.4.9 $ $Date: 2005/04/11 10:06:08 $
 */
public class Merchant extends Folk {
    public Merchant(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2MerchantInstance);
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (GameUtils.isMonster(attacker)) {
            return true;
        }

        return super.isAutoAttackable(attacker);
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String pom;
        if (val == 0) {
            pom = Integer.toString(npcId);
        } else {
            pom = npcId + "-" + val;
        }
        return "data/html/merchant/" + pom + ".htm";
    }

    public final void showBuyWindow(Player player, int val) {
        showBuyWindow(player, val, true);
    }

    public final void showBuyWindow(Player player, int val, boolean applyCastleTax) {
        final ProductList buyList = BuyListData.getInstance().getBuyList(val);
        if (buyList == null) {
            Creature.LOGGER.warn("BuyList not found! BuyListId:" + val);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!buyList.isNpcAllowed(getId())) {
            Creature.LOGGER.warn("Npc not allowed in BuyList! BuyListId:" + val + " NpcId:" + getId());
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        player.setInventoryBlockingStatus(true);

        player.sendPacket(new BuyList(buyList, player, (applyCastleTax) ? getCastleTaxRate(TaxType.BUY) : 0));
        player.sendPacket(new ExBuySellList(player, false, (applyCastleTax) ? getCastleTaxRate(TaxType.SELL) : 0));
    }
}
