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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_HERO_LIST.writeId(packet);

        packet.putInt(_heroList.size());
        for (Integer heroId : _heroList.keySet()) {
            final StatsSet hero = _heroList.get(heroId);
            writeString(hero.getString(Olympiad.CHAR_NAME), packet);
            packet.putInt(hero.getInt(Olympiad.CLASS_ID));
            writeString(hero.getString(Hero.CLAN_NAME, ""), packet);
            packet.putInt(hero.getInt(Hero.CLAN_CREST, 0));
            writeString(hero.getString(Hero.ALLY_NAME, ""), packet);
            packet.putInt(hero.getInt(Hero.ALLY_CREST, 0));
            packet.putInt(hero.getInt(Hero.COUNT));
            packet.putInt(0x00);
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 9 + _heroList.size() * 26 + _heroList.values().stream().mapToInt(h -> h.getString(Olympiad.CHAR_NAME).length() * 2
                + h.getString(Hero.CLAN_NAME).length() * 2 + h.getString(Hero.ALLY_NAME).length() * 2).sum();
    }
}
