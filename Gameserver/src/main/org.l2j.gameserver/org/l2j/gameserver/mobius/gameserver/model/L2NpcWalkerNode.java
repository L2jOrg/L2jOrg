package org.l2j.gameserver.mobius.gameserver.model;

import org.l2j.gameserver.mobius.gameserver.network.NpcStringId;

/**
 * @author Rayan RPG, JIV
 * @since 927
 */
public class L2NpcWalkerNode extends Location
{
    private final String _chatString;
    private final NpcStringId _npcString;
    private final int _delay;
    private final boolean _runToLocation;

    public L2NpcWalkerNode(int moveX, int moveY, int moveZ, int delay, boolean runToLocation, NpcStringId npcString, String chatText)
    {
        super(moveX, moveY, moveZ);
        _delay = delay;
        _runToLocation = runToLocation;
        _npcString = npcString;
        _chatString = (chatText == null) ? "" : chatText;
    }

    public int getDelay()
    {
        return _delay;
    }

    public boolean runToLocation()
    {
        return _runToLocation;
    }

    public NpcStringId getNpcString()
    {
        return _npcString;
    }

    public String getChatText()
    {
        if (_npcString != null)
        {
            throw new IllegalStateException("npcString is defined for walker route!");
        }
        return _chatString;
    }
}
