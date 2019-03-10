package org.l2j.gameserver.mobius.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.mobius.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public class ExBRBuyProduct extends IClientOutgoingPacket {
    private final int _reply;

    public ExBRBuyProduct(ExBrProductReplyType type) {
        _reply = type.getId();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BR_BUY_PRODUCT.writeId(packet);

        packet.putInt(_reply);
    }

    public enum ExBrProductReplyType implements IIdentifiable {
        SUCCESS(1),
        LACK_OF_POINT(-1),
        INVALID_PRODUCT(-2),
        USER_CANCEL(-3),
        INVENTROY_OVERFLOW(-4),
        CLOSED_PRODUCT(-5),
        SERVER_ERROR(-6),
        BEFORE_SALE_DATE(-7),
        AFTER_SALE_DATE(-8),
        INVALID_USER(-9),
        INVALID_ITEM(-10),
        INVALID_USER_STATE(-11),
        NOT_DAY_OF_WEEK(-12),
        NOT_TIME_OF_DAY(-13),
        SOLD_OUT(-14);
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
