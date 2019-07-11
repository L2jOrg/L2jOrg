package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Set;

/**
 * @author Sdw
 */
public class ExUserInfoAbnormalVisualEffect extends ServerPacket {
    private final Player _activeChar;

    public ExUserInfoAbnormalVisualEffect(Player cha) {
        _activeChar = cha;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_USER_INFO_ABNORMAL_VISUAL_EFFECT);

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
