package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
@StaticPacket
public class ExEnchantSkillResult extends IClientOutgoingPacket {
    public static final ExEnchantSkillResult STATIC_PACKET_TRUE = new ExEnchantSkillResult(true);
    public static final ExEnchantSkillResult STATIC_PACKET_FALSE = new ExEnchantSkillResult(false);

    private final boolean _enchanted;

    private ExEnchantSkillResult(boolean enchanted) {
        _enchanted = enchanted;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENCHANT_SKILL_RESULT.writeId(packet);

        packet.putInt(_enchanted ? 1 : 0);
    }
}
