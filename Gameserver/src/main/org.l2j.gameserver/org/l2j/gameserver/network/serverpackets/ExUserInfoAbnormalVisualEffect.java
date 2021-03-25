/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Set;

/**
 * @author Sdw
 */
public class ExUserInfoAbnormalVisualEffect extends ServerPacket {
    private final Player player;

    public ExUserInfoAbnormalVisualEffect(Player cha) {
        player = cha;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_USER_INFO_ABNORMAL_VISUAL_EFFECT, buffer );

        buffer.writeInt(player.getObjectId());
        buffer.writeInt(player.getTransformationId());

        final Set<AbnormalVisualEffect> abnormalVisualEffects = player.getEffectList().getCurrentAbnormalVisualEffects();
        final boolean isInvisible = player.isInvisible();
        buffer.writeInt(abnormalVisualEffects.size() + (isInvisible ? 1 : 0));
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            buffer.writeShort(abnormalVisualEffect.getClientId());
        }
        if (isInvisible) {
            buffer.writeShort(AbnormalVisualEffect.STEALTH.getClientId());
        }
    }

}
