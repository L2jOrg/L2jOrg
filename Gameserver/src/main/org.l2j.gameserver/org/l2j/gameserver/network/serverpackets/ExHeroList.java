package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.olympiad.Olympiad;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author -Wooden-, KenM, godson
 */
public class ExHeroList extends IClientOutgoingPacket {
    private final Map<Integer, StatsSet> _heroList;

    public ExHeroList() {
        _heroList = Hero.getInstance().getHeroes();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_HERO_LIST);

        writeInt(_heroList.size());
        for (Integer heroId : _heroList.keySet()) {
            final StatsSet hero = _heroList.get(heroId);
            writeString(hero.getString(Olympiad.CHAR_NAME));
            writeInt(hero.getInt(Olympiad.CLASS_ID));
            writeString(hero.getString(Hero.CLAN_NAME, ""));
            writeInt(hero.getInt(Hero.CLAN_CREST, 0));
            writeString(hero.getString(Hero.ALLY_NAME, ""));
            writeInt(hero.getInt(Hero.ALLY_CREST, 0));
            writeInt(hero.getInt(Hero.COUNT));
            writeInt(0x00);
        }
    }


}
