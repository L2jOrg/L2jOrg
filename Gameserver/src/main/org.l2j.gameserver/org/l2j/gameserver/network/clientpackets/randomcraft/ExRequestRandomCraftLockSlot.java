package org.l2j.gameserver.network.clientpackets.randomcraft;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.PlayerRandomCraft;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftRandomInfo;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftRandomLockSlot;

public class ExRequestRandomCraftLockSlot extends ClientPacket {
    private static final int[] LOCK_PRICE =
            {
                    100,
                    500,
                    1000
            };

    private int _id;

    @Override
    protected void readImpl() throws Exception {
        _id = readInt();
    }

    @Override
    protected void runImpl() {
        if (!Config.ENABLE_RANDOM_CRAFT) {
            return;
        }

        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if ((_id >= 0) && (_id < 5)) {
            final PlayerRandomCraft rc = player.getRandomCraft();
            int lockedItemCount = rc.getLockedSlotCount();
            if (((rc.getRewards().size() - 1) >= _id) && (lockedItemCount < 3)) {
                int price = LOCK_PRICE[Math.min(lockedItemCount, 2)];
                Item lcoin = player.getInventory().getItemByItemId(91663);
                if ((lcoin != null) && (lcoin.getCount() >= price)) {
                    player.destroyItem("RandomCraft Lock Slot", lcoin, price, player, true);
                    rc.getRewards().get(_id).lock();
                    player.sendPacket(new ExCraftRandomLockSlot());
                    player.sendPacket(new ExCraftRandomInfo(player));
                }
            }
        }
    }
}
