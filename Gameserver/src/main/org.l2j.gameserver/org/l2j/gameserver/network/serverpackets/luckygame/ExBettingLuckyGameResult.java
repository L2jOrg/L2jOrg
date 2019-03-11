package org.l2j.gameserver.network.serverpackets.luckygame;

import org.l2j.gameserver.enums.LuckyGameItemType;
import org.l2j.gameserver.enums.LuckyGameResultType;
import org.l2j.gameserver.enums.LuckyGameType;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Sdw
 */
public class ExBettingLuckyGameResult extends IClientOutgoingPacket {
    public static final ExBettingLuckyGameResult NORMAL_INVALID_ITEM_COUNT = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_ITEM_COUNT, LuckyGameType.NORMAL);
    public static final ExBettingLuckyGameResult LUXURY_INVALID_ITEM_COUNT = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_ITEM_COUNT, LuckyGameType.LUXURY);
    public static final ExBettingLuckyGameResult NORMAL_INVALID_CAPACITY = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_CAPACITY, LuckyGameType.NORMAL);
    public static final ExBettingLuckyGameResult LUXURY_INVALID_CAPACITY = new ExBettingLuckyGameResult(LuckyGameResultType.INVALID_CAPACITY, LuckyGameType.LUXURY);

    private final LuckyGameResultType _result;
    private final LuckyGameType _type;
    private final EnumMap<LuckyGameItemType, List<ItemHolder>> _rewards;
    private final int _ticketCount;
    private final int _size;

    public ExBettingLuckyGameResult(LuckyGameResultType result, LuckyGameType type) {
        _result = result;
        _type = type;
        _rewards = new EnumMap<>(LuckyGameItemType.class);
        _ticketCount = 0;
        _size = 0;
    }

    public ExBettingLuckyGameResult(LuckyGameResultType result, LuckyGameType type, EnumMap<LuckyGameItemType, List<ItemHolder>> rewards, int ticketCount) {
        _result = result;
        _type = type;
        _rewards = rewards;
        _ticketCount = ticketCount;
        _size = (int) rewards.values().stream().mapToLong(i -> i.stream().count()).sum();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BETTING_LUCKY_GAME_RESULT.writeId(packet);
        packet.putInt(_result.getClientId());
        packet.putInt(_type.ordinal());
        packet.putInt(_ticketCount);
        packet.putInt(_size);
        for (Entry<LuckyGameItemType, List<ItemHolder>> reward : _rewards.entrySet()) {
            for (ItemHolder item : reward.getValue()) {
                packet.putInt(reward.getKey().getClientId());
                packet.putInt(item.getId());
                packet.putInt((int) item.getCount());
            }
        }
    }
}
