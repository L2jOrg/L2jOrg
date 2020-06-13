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
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.DamageInfo.NpcDamage;
import org.l2j.gameserver.model.DamageInfo.PlayerDamage;
import org.l2j.gameserver.model.item.instance.Item;
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
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_DIE_INFO);

        writeShort(drop.size());
        drop.forEach(this::writeDrop);

        writeShort(damages.size());
        damages.forEach(this::writeDamage);
    }

    private void writeDrop(Item item) {
        writeInt(item.getId());
        writeInt(item.getEnchantLevel());
        writeInt((int) item.getCount());
    }

    private void writeDamage(DamageInfo damageInfo) {
        writeShort(damageInfo.attackerType());

        if(damageInfo instanceof PlayerDamage playerDamage) {
            writePlayerDamage(playerDamage);
        } else if(damageInfo instanceof NpcDamage npcDamage) {
            writeNpcDamage(npcDamage);
        } else {
            writeOthersDamage(damageInfo);
        }

        writeDouble(damageInfo.damage());
        writeShort(damageInfo.damageType());
    }

    private void writeNpcDamage(NpcDamage npcDamage) {
        writeInt(npcDamage.attackerId());
        writeShort(0);
        writeInt(npcDamage.skillId());
    }

    private void writePlayerDamage(PlayerDamage playerDamage) {
        writeString(playerDamage.attackerName());
        writeString(playerDamage.clanName());
        writeInt(playerDamage.skillId());
    }

    private void writeOthersDamage(DamageInfo damageInfo) {
        writeInt(0);
        writeInt(0);
    }
}

