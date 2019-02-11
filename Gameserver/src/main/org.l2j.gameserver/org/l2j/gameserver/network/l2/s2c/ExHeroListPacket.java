package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.StatsSet;

import java.nio.ByteBuffer;
import java.util.Collection;

import static org.l2j.commons.util.Util.STRING_EMPTY;

/**
 * Format: (ch) d [SdSdSdd]
 * d: size
 * [
 * S: hero name
 * d: hero class ID
 * S: hero clan name
 * d: hero clan crest id
 * S: hero ally name
 * d: hero Ally id
 * d: count
 * ]
 */
public class ExHeroListPacket extends L2GameServerPacket {
    private Collection<StatsSet> _heroList;

    public ExHeroListPacket() {
        _heroList = Hero.getInstance().getHeroes().values();
    }

    @Override
    protected final void writeImpl(GameClient client, ByteBuffer buffer) {
        buffer.putInt(_heroList.size());
        for (StatsSet hero : _heroList) {
            writeString(hero.getString(Hero.CHAR_NAME), buffer);
            buffer.putInt(hero.getInteger(Hero.CLASS_ID));
            writeString(hero.getString(Hero.CLAN_NAME, STRING_EMPTY), buffer);
            buffer.putInt(hero.getInteger(Hero.CLAN_CREST, 0));
            writeString(hero.getString(Hero.ALLY_NAME, STRING_EMPTY), buffer);
            buffer.putInt(hero.getInteger(Hero.ALLY_CREST, 0));
            buffer.putInt(hero.getInteger(Hero.COUNT));
            buffer.putInt(0);
        }
    }
}