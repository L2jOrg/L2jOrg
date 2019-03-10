package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.entity.Hero;

import java.nio.ByteBuffer;

/**
 * Format chS c (id) 0xD0 h (subid) 0x0C S the hero's words :)
 *
 * @author -Wooden-
 */
public final class RequestWriteHeroWords extends IClientIncomingPacket {
    private String _heroWords;

    @Override
    public void readImpl(ByteBuffer packet) {
        _heroWords = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if ((player == null) || !player.isHero()) {
            return;
        }

        if ((_heroWords == null) || (_heroWords.length() > 300)) {
            return;
        }

        Hero.getInstance().setHeroMessage(player, _heroWords);
    }
}