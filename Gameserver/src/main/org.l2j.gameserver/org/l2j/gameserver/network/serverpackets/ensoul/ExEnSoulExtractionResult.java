package org.l2j.gameserver.network.serverpackets.ensoul;

import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExEnSoulExtractionResult extends ServerPacket {
    private final boolean _success;
    private final Item _item;

    public ExEnSoulExtractionResult(boolean success, Item item) {
        _success = success;
        _item = item;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ENSOUL_EXTRACTION_RESULT);
        writeByte((byte) (_success ? 1 : 0));
        if (_success) {
            writeByte((byte) _item.getSpecialAbilities().size());
            for (EnsoulOption option : _item.getSpecialAbilities()) {
                writeInt(option.getId());
            }
            writeByte((byte) _item.getAdditionalSpecialAbilities().size());
            for (EnsoulOption option : _item.getAdditionalSpecialAbilities()) {
                writeInt(option.getId());
            }
        }
    }

}
