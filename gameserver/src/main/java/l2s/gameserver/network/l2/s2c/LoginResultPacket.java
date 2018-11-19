package l2s.gameserver.network.l2.s2c;

public class LoginResultPacket extends L2GameServerPacket
{
    public static L2GameServerPacket SUCCESS = new LoginResultPacket(0xFFFFFFFF, 0);
    public static L2GameServerPacket SYSTEM_ERROR_LOGIN_LATER = new LoginResultPacket(0, 1);
    public static L2GameServerPacket PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT = new LoginResultPacket(0, 2);
    public static L2GameServerPacket PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT2 = new LoginResultPacket(0, 3);
    public static L2GameServerPacket ACCESS_FAILED_TRY_LATER = new LoginResultPacket(0, 4);
    public static L2GameServerPacket INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT = new LoginResultPacket(0, 5);
    public static L2GameServerPacket ACCESS_FAILED_TRY_LATER2 = new LoginResultPacket(0, 6);
    public static L2GameServerPacket ACOUNT_ALREADY_IN_USE = new LoginResultPacket(0, 7);
    public static L2GameServerPacket ACCESS_FAILED_TRY_LATER3 = new LoginResultPacket(0, 8);
    public static L2GameServerPacket ACCESS_FAILED_TRY_LATER4 = new LoginResultPacket(0, 9);
    public static L2GameServerPacket ACCESS_FAILED_TRY_LATER5 = new LoginResultPacket(0, 10);

    private final int _reason1;
    private final int _reason2;

    public LoginResultPacket(int reason1, int reason2)
    {
        _reason1 = reason1;
        _reason2 = reason2;
    }

    @Override
    protected final void writeImpl()
    {
        writeD(_reason1);
        writeD(_reason2);
    }
}