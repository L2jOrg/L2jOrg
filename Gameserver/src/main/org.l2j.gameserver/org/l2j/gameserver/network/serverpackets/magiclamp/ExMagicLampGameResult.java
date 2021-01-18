package org.l2j.gameserver.network.serverpackets.magiclamp;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.MagicLampData;
import org.l2j.gameserver.enums.LampMode;
import org.l2j.gameserver.enums.LampType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.MagicLampDataHolder;
import org.l2j.gameserver.model.holders.MagicLampHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExMagicLampGameResult extends ServerPacket {
    private final Map<LampType, MagicLampHolder> _reward = new HashMap<>();

    public ExMagicLampGameResult(Player player, int count, byte mode){
        final LampMode type = LampMode.getByMode(mode);
        final int consume = calcConsume(type, count);
        final int have = player.getLampCount();
        if (have >= consume)
        {
            init(type, count);
            player.setLampCount(have - consume);
            _reward.values().forEach(lamp -> player.addExpAndSp(lamp.getExp(), lamp.getSp()));
            // update UI
            final int left = player.getLampCount();
            player.sendPacket(new ExMagicLampGameInfoUI(player, mode, left > consume ? count : left)); // check left count for update UI
            player.sendPacket(new ExMagicLampExpInfoUI(player));
        }

    }

    private void init(LampMode mode, int count)
    {
        for (int x = count; x > 0; x--)
        {
            final List<MagicLampDataHolder> available = MagicLampData.getInstance().getLamps().stream().filter(lamp -> (lamp.getMode() == mode) && chance(lamp.getChance())).collect(Collectors.toList());
            final MagicLampDataHolder random = getRandom(available);
            if (random != null)
            {
                _reward.computeIfAbsent(random.getType(), list -> new MagicLampHolder(random)).inc();
            }
        }
    }

    private boolean chance(double chance)
    {
        return (chance > 0) && ((chance >= 100) || (Rnd.get(100) <= chance));
    }

    private <E> E getRandom(List<E> list)
    {
        if (list.isEmpty())
        {
            return null;
        }
        if (list.size() == 1)
        {
            return list.get(0);
        }
        return list.get(Rnd.get(list.size()));
    }

    private int calcConsume(LampMode mode, int count)
    {
        switch (mode) {
            case NORMAL -> {
                return Config.MAGIC_LAMP_REWARD_COUNT * count;
            }
            case GREATER -> {
                return Config.MAGIC_LAMP_GREATER_REWARD_COUNT * count;
            }
            default -> {
                return 0;
            }
        }
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_MAGICLAMP_GAME_RESULT, buffer );
        buffer.writeInt(_reward.size()); // magicLampGameResult
        _reward.values().forEach(lamp ->
        {
            buffer.writeByte(lamp.getType().getGrade()); // cGradeNum
            buffer.writeInt(lamp.getCount()); // nRewardCount
            buffer.writeLong(lamp.getExp()); // nEXP
            buffer.writeLong(lamp.getSp()); // nSP
        });
    }
}

