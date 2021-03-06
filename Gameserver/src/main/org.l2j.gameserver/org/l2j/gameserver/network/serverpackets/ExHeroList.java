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
import org.l2j.gameserver.data.database.data.OlympiadHeroData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.List;

/**
 * @author -Wooden-, KenM, godson
 * @author JoeAlisson
 */
public class ExHeroList extends ServerPacket {

    private final List<OlympiadHeroData> heroes;

    public ExHeroList(List<OlympiadHeroData> heroes) {
        this.heroes = heroes;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_HERO_LIST, buffer );

        buffer.writeInt(heroes.size());

        for (var hero : heroes) {
            buffer.writeString(hero.getName());
            buffer.writeInt(hero.getClassId());
            buffer.writeString(hero.getClanName());
            buffer.writeInt(0); // clan crest not used in classic
            buffer.writeString(""); // ally name not used in classic
            buffer.writeInt(0); // ally crest not used in classic
            buffer.writeInt(hero.getHeroCount());
            buffer.writeInt(hero.getServer());
            buffer.writeByte(hero.isLegend());
        }
    }
}
