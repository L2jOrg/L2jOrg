package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JIV
 */
@StaticPacket
public class ExEnchantSkillResult extends ServerPacket {
    public static final ExEnchantSkillResult STATIC_PACKET_TRUE = new ExEnchantSkillResult(true);
    public static final ExEnchantSkillResult STATIC_PACKET_FALSE = new ExEnchantSkillResult(false);

    private final boolean _enchanted;

    private ExEnchantSkillResult(boolean enchanted) {
        _enchanted = enchanted;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ENCHANT_SKILL_RESULT);

        writeInt(_enchanted ? 1 : 0);
    }

}
