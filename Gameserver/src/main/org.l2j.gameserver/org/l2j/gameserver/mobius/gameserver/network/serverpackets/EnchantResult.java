package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class EnchantResult extends IClientOutgoingPacket {
    public static int SUCCESS = 0;
    public static int FAIL = 1;
    public static int ERROR = 2;
    public static int BLESSED_FAIL = 3;
    public static int NO_CRYSTAL = 4;
    public static int SAFE_FAIL = 5;
    private final int _result;
    private final int _crystal;
    private final int _count;
    private final int _enchantLevel;
    private final int[] _enchantOptions;

    public EnchantResult(int result, int crystal, int count, int enchantLevel, int[] options) {
        _result = result;
        _crystal = crystal;
        _count = count;
        _enchantLevel = enchantLevel;
        _enchantOptions = options;
    }

    public EnchantResult(int result, int crystal, int count) {
        this(result, crystal, count, 0, L2ItemInstance.DEFAULT_ENCHANT_OPTIONS);
    }

    public EnchantResult(int result, L2ItemInstance item) {
        this(result, 0, 0, item.getEnchantLevel(), item.getEnchantOptions());
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ENCHANT_RESULT.writeId(packet);

        packet.putInt(_result);
        packet.putInt(_crystal);
        packet.putLong(_count);
        packet.putInt(_enchantLevel);
        for (int option : _enchantOptions) {
            packet.putShort((short) option);
        }
    }
}
