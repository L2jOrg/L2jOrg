package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author Sdw
 */
public class NpcInfoAbnormalVisualEffect extends IClientOutgoingPacket {
    private final L2Npc _npc;

    public NpcInfoAbnormalVisualEffect(L2Npc npc) {
        _npc = npc;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.NPC_INFO_ABNORMAL_VISUAL_EFFECT);

        writeInt(_npc.getObjectId());
        writeInt(_npc.getTransformationDisplayId());

        final Set<AbnormalVisualEffect> abnormalVisualEffects = _npc.getEffectList().getCurrentAbnormalVisualEffects();
        writeInt(abnormalVisualEffects.size());
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            writeShort((short) abnormalVisualEffect.getClientId());
        }
    }

}
