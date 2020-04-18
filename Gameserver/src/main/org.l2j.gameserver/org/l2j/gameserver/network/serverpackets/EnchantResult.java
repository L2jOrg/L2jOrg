package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class EnchantResult extends ServerPacket {
    public static int SUCCESS = 0;
    public static int FAIL = 1;
    public static int ERROR = 2;
    public static int BLESSED_FAIL = 3;
    public static int NO_CRYSTAL = 4;
    public static int SAFE_FAIL = 5;
    private final int result;
    private final int crystal;
    private final int count;
    private final int enchantLevel;
    private final int[] enchantOptions;

    public EnchantResult(int result, int crystal, int count, int enchantLevel, int[] options) {
        this.result = result;
        this.crystal = crystal;
        this.count = count;
        this.enchantLevel = enchantLevel;
        enchantOptions = options;
    }

    public EnchantResult(int result, int crystal, int count) {
        this(result, crystal, count, 0, Item.DEFAULT_ENCHANT_OPTIONS);
    }

    public EnchantResult(int result, Item item) {
        this(result, 0, 0, item.getEnchantLevel(), item.getEnchantOptions());
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.ENCHANT_RESULT);

        writeInt(result);
        writeInt(crystal);
        writeLong(count);

       for (int option : enchantOptions) {
            writeInt(option);
        }
        writeInt(enchantLevel);
    }

}
