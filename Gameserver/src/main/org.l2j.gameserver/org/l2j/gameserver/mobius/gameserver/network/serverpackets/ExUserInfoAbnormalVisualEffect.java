package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Set;

/**
 * @author Sdw
 */
public class ExUserInfoAbnormalVisualEffect implements IClientOutgoingPacket
{
    private final L2PcInstance _activeChar;

    public ExUserInfoAbnormalVisualEffect(L2PcInstance cha)
    {
        _activeChar = cha;
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.EX_USER_INFO_ABNORMAL_VISUAL_EFFECT.writeId(packet);

        packet.writeD(_activeChar.getObjectId());
        packet.writeD(_activeChar.getTransformationId());

        final Set<AbnormalVisualEffect> abnormalVisualEffects = _activeChar.getEffectList().getCurrentAbnormalVisualEffects();
        final boolean isInvisible = _activeChar.isInvisible();
        packet.writeD(abnormalVisualEffects.size() + (isInvisible ? 1 : 0));
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects)
        {
            packet.writeH(abnormalVisualEffect.getClientId());
        }
        if (isInvisible)
        {
            packet.writeH(AbnormalVisualEffect.STEALTH.getClientId());
        }
        return true;
    }
}
