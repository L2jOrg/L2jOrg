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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Set;

/**
 * @author Sdw
 */
public class NpcInfoAbnormalVisualEffect extends ServerPacket {
    private final Npc _npc;

    public NpcInfoAbnormalVisualEffect(Npc npc) {
        _npc = npc;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.NPC_INFO_ABNORMAL_VISUAL_EFFECT);

        writeInt(_npc.getObjectId());
        writeInt(_npc.getTransformationDisplayId());

        final Set<AbnormalVisualEffect> abnormalVisualEffects = _npc.getEffectList().getCurrentAbnormalVisualEffects();
        writeInt(abnormalVisualEffects.size());
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            writeShort((short) abnormalVisualEffect.getClientId());
        }
    }

}
