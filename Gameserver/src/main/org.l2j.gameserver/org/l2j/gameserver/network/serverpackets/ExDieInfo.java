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
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.DamageInfo.NpcDamage;
import org.l2j.gameserver.model.DamageInfo.PlayerDamage;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
public class ExDieInfo extends ServerPacket {

    private final Collection<DamageInfo> damages;
    private final Collection<Item> drop;

    public ExDieInfo(Collection<DamageInfo> damages, Collection<Item> drop) {
        this.damages = damages;
        this.drop = drop;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_DIE_INFO, buffer );

        buffer.writeShort(drop.size());
        for (Item item : drop) {
            writeDrop(item, buffer);
        }

        buffer.writeShort(damages.size());
        for (DamageInfo damage : damages) {
            writeDamage(damage, buffer);
        }
    }

    private void writeDrop(Item item, WritableBuffer buffer) {
        buffer.writeInt(item.getId());
        buffer.writeInt(item.getEnchantLevel());
        buffer.writeInt((int) item.getCount());
    }

    private void writeDamage(DamageInfo damageInfo, WritableBuffer buffer) {
        buffer.writeShort(damageInfo.attackerType());

        if(damageInfo instanceof PlayerDamage playerDamage) {
            writePlayerDamage(playerDamage, buffer);
        } else if(damageInfo instanceof NpcDamage npcDamage) {
            writeNpcDamage(npcDamage, buffer);
        } else {
            writeOthersDamage(damageInfo, buffer);
        }

        buffer.writeDouble(damageInfo.damage());
        buffer.writeShort(damageInfo.damageType());
    }

    private void writeNpcDamage(NpcDamage npcDamage, WritableBuffer buffer) {
        buffer.writeInt(npcDamage.attackerId());
        buffer.writeShort(0);
        buffer.writeInt(npcDamage.skillId());
    }

    private void writePlayerDamage(PlayerDamage playerDamage, WritableBuffer buffer) {
        buffer.writeString(playerDamage.attackerName());
        buffer.writeString(playerDamage.clanName());
        buffer.writeInt(playerDamage.skillId());
    }

    private void writeOthersDamage(DamageInfo damageInfo, WritableBuffer buffer) {
        buffer.writeInt(0);
        buffer.writeInt(0);
    }
}

