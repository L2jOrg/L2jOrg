package l2s.gameserver.network.l2.components;

/**
 * @author VISTALL
 * @date 22:24/05.01.2011
 */
public enum SysString
{
    // Text: Passenger Boat Info
    PASSENGER_BOAT_INFO(801),
    // Text: Previous
    PREVIOUS(1037),
    // Text: Next
    NEXT(1038);

    private static final SysString[] VALUES = values();

    private final int _id;

    SysString(int i)
    {
        _id = i;
    }

    public int getId()
    {
        return _id;
    }

    public static SysString valueOf2(String id)
    {
        for(SysString m : VALUES)
            if(m.name().equals(id))
                return m;

        return null;
    }

    public static SysString valueOf(int id)
    {
        for(SysString m : VALUES)
            if(m.getId() == id)
                return m;

        return null;
    }
}