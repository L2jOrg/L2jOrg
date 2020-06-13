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
package org.l2j.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Gnacik, UnAfraid
 */
public class ExBRBuyProduct extends ServerPacket {
    private final int _reply;

    public ExBRBuyProduct(ExBrProductReplyType type) {
        _reply = type.getId();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BR_BUY_PRODUCT_ACK);

        writeInt(_reply);
    }


    public enum ExBrProductReplyType implements IIdentifiable {
        SUCCESS(1),
        LACK_OF_POINT(-1),
        INVALID_PRODUCT(-2),
        INCORRECT_COUNT(-3),
        INVENTROY_OVERFLOW(-4),
        CLOSED_PRODUCT(-5),
        SERVER_ERROR(-6),
        BEFORE_SALE_DATE(-7),
        AFTER_SALE_DATE(-8),
        INVENTORY_FULL0(-9),
        INVALID_ITEM(-10),
        INVENTORY_FULL(-11),
        NOT_DAY_OF_WEEK(-12),
        NOT_TIME_OF_DAY(-13),
        SOLD_OUT(-14),
        RECIPIENT_OR_CHARACTER_DELETED(-17),
        CANNOT_SEND_EMAIL_TO_YOURSELF(-18),
        EMAIL_LIMIT_REACHED(-19),
        EMAIL_OTHER_LIMIT_REACHED(-20),
        CANNOT_SEND_EMAIL_BLOCKED(-21),
        MAXIMUM_QUANTITY_REACHED(-22),
        NOT_A_GIFT(-23),
        INVALID_LEVEL(-24),
        NOT_ENOUGH_ADENA(-25),
        NOT_ENOUGH_HERO_COIN(-26),
        INVALID_DATE_CREATION(-27),
        ALREADY_BOUGHT(-28);

        private final int _id;

        ExBrProductReplyType(int id) {
            _id = id;
        }
        @Override
        public int getId() {
            return _id;
        }

    }
}
