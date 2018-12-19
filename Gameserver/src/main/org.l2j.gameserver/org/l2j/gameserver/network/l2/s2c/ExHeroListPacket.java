package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.templates.StatsSet;

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
        _heroList = Hero.getInstance().getHeroes().valueCollection();
    }

    @Override
    protected final void writeImpl() {
        writeInt(_heroList.size());
        for (StatsSet hero : _heroList) {
            writeString(hero.getString(Hero.CHAR_NAME));
            writeInt(hero.getInteger(Hero.CLASS_ID));
            writeString(hero.getString(Hero.CLAN_NAME, STRING_EMPTY));
            writeInt(hero.getInteger(Hero.CLAN_CREST, 0));
            writeString(hero.getString(Hero.ALLY_NAME, STRING_EMPTY));
            writeInt(hero.getInteger(Hero.ALLY_CREST, 0));
            writeInt(hero.getInteger(Hero.COUNT));
            writeInt(0);
        }
    }
}