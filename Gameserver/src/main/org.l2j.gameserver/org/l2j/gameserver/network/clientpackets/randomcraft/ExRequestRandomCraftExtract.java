package org.l2j.gameserver.network.clientpackets.randomcraft;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.RandomCraftData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.RandomCraftRequest;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftExtract;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftInfo;

import java.util.HashMap;
import java.util.Map;

public class ExRequestRandomCraftExtract extends ClientPacket {
    private final Map<Integer, Long> _items = new HashMap<>();
    @Override
    protected void readImpl() throws Exception {
        final int size = readInt();
        for (int i = 0; i < size; i++)
        {
            final int objId = readInt();
            final long count = readInt();
            _items.put(objId, count);
        }
    }

    @Override
    protected void runImpl() {
        if (!Config.ENABLE_RANDOM_CRAFT)
        {
            return;
        }

        final Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }

        if (player.hasItemRequest() || player.hasRequest(RandomCraftRequest.class))
        {
            return;
        }
        player.addRequest(new RandomCraftRequest(player));

        int points = 0;
        int fee = 0;
        Map<Integer, Long> toDestroy = new HashMap<>();
        for (Map.Entry<Integer, Long> e : _items.entrySet())
        {
            final int objId = e.getKey();
            long count = e.getValue();
            if (count < 1)
            {
                player.removeRequest(RandomCraftRequest.class);
                return;
            }
            final Item item = player.getInventory().getItemByObjectId(objId);
            if (item != null)
            {
                count = Math.min(item.getCount(), count);
                toDestroy.put(objId, count);
                points += RandomCraftData.getInstance().getPoints(item.getId()) * count;
                fee += RandomCraftData.getInstance().getFee(item.getId()) * count;
            }
        }

        if (player.reduceAdena("RandomCraft Extract", fee, player, true))
        {
            for (Map.Entry<Integer, Long> e : toDestroy.entrySet())
            {
                player.destroyItem("RandomCraft Extract", e.getKey(), e.getValue(), player, true);
            }
            player.getRandomCraft().addCraftPoints(points);
        }

        player.sendPacket(new ExCraftInfo(player));
        player.sendPacket(new ExCraftExtract());
        player.removeRequest(RandomCraftRequest.class);
    }

}
