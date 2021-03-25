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
package org.l2j.gameserver.network.serverpackets.olympiad;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.database.data.OlympiadHeroData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;
import java.util.Objects;

import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * @author JoeAlisson
 */
public class ExOlympiadHeroesInfo extends ServerPacket {

    private static final OlympiadHeroData DEFAULT_LEGEND = new OlympiadHeroData();
    private final OlympiadHeroData legend;
    private final List<OlympiadHeroData> heroes;

    public ExOlympiadHeroesInfo(OlympiadHeroData legend, List<OlympiadHeroData> heroes) {
        this.legend = Objects.requireNonNullElse(legend, DEFAULT_LEGEND);
        this.heroes = heroes;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_OLYMPIAD_HERO_AND_LEGEND_INFO, buffer );

        buffer.writeShort(1024);
        writeHeroInfo(buffer, legend, legend.getLegendCount());

        buffer.writeInt(heroes.size());
        for (var hero : heroes) {
            writeHeroInfo(buffer, hero, hero.getHeroCount());
        }
    }

    private void writeHeroInfo(WritableBuffer buffer, OlympiadHeroData hero, int count) {
        buffer.writeSizedString(hero.getName());
        buffer.writeSizedString(hero.getClanName());
        buffer.writeInt(hero.getServer());
        buffer.writeInt(hero.getRace());
        buffer.writeInt(hero.getSex());
        buffer.writeInt(hero.getClassId());
        buffer.writeInt(hero.getLevel());

        buffer.writeInt(count);
        buffer.writeInt(hero.getBattlesWon());
        buffer.writeInt(hero.getBattlesLost());
        buffer.writeInt(hero.getPoints());
        buffer.writeInt(hero.getClanLevel());
    }
}
