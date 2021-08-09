package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.model.actor.instance.Player;

public class AutoPotionTask implements Runnable
{
    private final Player _player;

    public AutoPotionTask(Player player)
    {
        _player = player;
    }

    @Override
    public void run()
    {
        if ((_player == null) || (!_player.isOnline()) || _player.isAlikeDead() || (!Config.AUTO_POTIONS_IN_OLYMPIAD && _player.isInOlympiadMode()))
        {
            return;
        }

        boolean success = false;
        if (Config.AUTO_HP_ENABLED)
        {
            final boolean restoreHP = ((_player.getStatus().getCurrentHp() / _player.getMaxHp()) * 100) < Config.AUTO_HP_PERCENTAGE;
            for (int itemId : Config.AUTO_HP_ITEM_IDS)
            {
                final Item hpPotion = _player.getInventory().getItemByItemId(itemId);
                if ((hpPotion != null) && (hpPotion.getCount() > 0))
                {
                    success = true;
                    if (restoreHP)
                    {
                        ItemHandler.getInstance().getHandler(hpPotion.getEtcItem()).useItem(_player, hpPotion, false);
                        _player.sendMessage("Auto potion: Restored HP.");
                        break;
                    }
                }
            }
        }
        if (Config.AUTO_CP_ENABLED)
        {
            final boolean restoreCP = ((_player.getStatus().getCurrentCp() / _player.getMaxCp()) * 100) < Config.AUTO_CP_PERCENTAGE;
            for (int itemId : Config.AUTO_CP_ITEM_IDS)
            {
                final Item cpPotion = _player.getInventory().getItemByItemId(itemId);
                if ((cpPotion != null) && (cpPotion.getCount() > 0))
                {
                    success = true;
                    if (restoreCP)
                    {
                        ItemHandler.getInstance().getHandler(cpPotion.getEtcItem()).useItem(_player, cpPotion, false);
                        _player.sendMessage("Auto potion: Restored CP.");
                        break;
                    }
                }
            }
        }
        if (Config.AUTO_MP_ENABLED)
        {
            final boolean restoreMP = ((_player.getStatus().getCurrentMp() / _player.getMaxMp()) * 100) < Config.AUTO_MP_PERCENTAGE;
            for (int itemId : Config.AUTO_MP_ITEM_IDS)
            {
                final Item mpPotion = _player.getInventory().getItemByItemId(itemId);
                if ((mpPotion != null) && (mpPotion.getCount() > 0))
                {
                    success = true;
                    if (restoreMP)
                    {
                        ItemHandler.getInstance().getHandler(mpPotion.getEtcItem()).useItem(_player, mpPotion, false);
                        _player.sendMessage("Auto potion: Restored MP.");
                        break;
                    }
                }
            }
        }

        if (!success)
        {
            _player.sendMessage("Auto potion: You are out of potions!");
        }
    }
}