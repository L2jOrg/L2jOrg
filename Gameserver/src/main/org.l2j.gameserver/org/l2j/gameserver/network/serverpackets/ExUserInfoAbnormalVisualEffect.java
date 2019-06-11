package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author Sdw
 */
public class ExUserInfoAbnormalVisualEffect extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;

    public ExUserInfoAbnormalVisualEffect(L2PcInstance cha) {
        _activeChar = cha;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_USER_INFO_ABNORMAL_VISUAL_EFFECT);

        writeInt(_activeChar.getObjectId());
        writeInt(_activeChar.getTransformationId());

        final Set<AbnormalVisualEffect> abnormalVisualEffects = _activeChar.getEffectList().getCurrentAbnormalVisualEffects();
        final boolean isInvisible = _activeChar.isInvisible();
        writeInt(abnormalVisualEffects.size() + (isInvisible ? 1 : 0));
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            writeShort((short) abnormalVisualEffect.getClientId());
        }
        if (isInvisible) {
            writeShort((short) AbnormalVisualEffect.STEALTH.getClientId());
        }
    }

}
