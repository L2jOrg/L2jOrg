package org.l2j.gameserver.network.l2.components;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;
import org.l2j.gameserver.network.l2.s2c.SayPacket2;

public class CustomChatMessage extends CustomMessage
{
    private final ChatType _type;

    public CustomChatMessage(String address, ChatType type)
    {
        super(address);
        _type = type;
    }

    @Override
    public L2GameServerPacket packet(Player player)
    {
        return new SayPacket2(0, _type, "", toString(player));
    }
}