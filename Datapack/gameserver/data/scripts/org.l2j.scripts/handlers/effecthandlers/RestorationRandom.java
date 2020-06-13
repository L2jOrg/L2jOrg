/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.ExtractableProductItem;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.holders.RestorationItemHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * Restoration Random effect implementation.<br>
 * This effect is present in item skills that "extract" new items upon usage.<br>
 * This effect has been unhardcoded in order to work on targets as well.
 * @author Zoey76, Mobius
 * @author JoeAlisson
 */
public final class RestorationRandom extends AbstractEffect {

    private final List<ExtractableProductItem> products;

    private RestorationRandom(StatsSet params) {
        if(params.contains("id")) {
            var item = new RestorationItemHolder(params.getInt("id"), params.getInt("count"), 0, 0);
            products = List.of(new ExtractableProductItem(List.of(item), params.getInt("chance")));
        } else {
            products = new ArrayList<>();
            params.getSet().forEach((key, value) -> {
                if(key.startsWith("item")) {
                    var set = (StatsSet) value;
                    var item = new RestorationItemHolder(set.getInt("id"), set.getInt("count"), 0, 0);
                    products.add(new ExtractableProductItem(List.of(item), set.getInt("chance")));
                }
            });
        }
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        final double rndNum = 100 * Rnd.nextDouble();
        double chance;
        double chanceFrom = 0;
        final List<RestorationItemHolder> creationList = new ArrayList<>();

        // Explanation for future changes:
        // You get one chance for the current skill, then you can fall into
        // one of the "areas" like in a roulette.
        // Example: for an item like Id1,A1,30;Id2,A2,50;Id3,A3,20;
        // #---#-----#--#
        // 0--30----80-100
        // If you get chance equal 45% you fall into the second zone 30-80.
        // Meaning you get the second production list.
        // Calculate extraction
        for (ExtractableProductItem expi : products) {
            chance = expi.getChance();
            if ((rndNum >= chanceFrom) && (rndNum <= (chance + chanceFrom))) {
                creationList.addAll(expi.getItems());
                break;
            }
            chanceFrom += chance;
        }

        final Player player = effected.getActingPlayer();
        if (creationList.isEmpty()) {
            player.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
            return;
        }

        final Map<Item, Long> extractedItems = new HashMap<>();
        for (RestorationItemHolder createdItem : creationList) {
            if ((createdItem.getId() <= 0) || (createdItem.getCount() <= 0)) {
                continue;
            }

            long itemCount = (long) (createdItem.getCount() * Config.RATE_EXTRACTABLE);
            final Item newItem = player.addItem("Extract", createdItem.getId(), itemCount, effector, false);

            if (nonNull(newItem) && createdItem.getMaxEnchant() > 0) {
                newItem.setEnchantLevel(Rnd.get(createdItem.getMinEnchant(), createdItem.getMaxEnchant()));
            }

            if (nonNull(extractedItems.get(newItem))){
                extractedItems.put(newItem, extractedItems.get(newItem) + itemCount);
            } else {
                extractedItems.put(newItem, itemCount);
            }
        }

        if (!extractedItems.isEmpty()) {
            final InventoryUpdate playerIU = new InventoryUpdate();
            for (Entry<Item, Long> entry : extractedItems.entrySet()) {
                if (entry.getKey().isStackable()) {
                    playerIU.addModifiedItem(entry.getKey());
                } else {
                    for (var itemInstance : player.getInventory().getItemsByItemId(entry.getKey().getId())) {
                        playerIU.addModifiedItem(itemInstance);
                    }
                }
                sendMessage(player, entry.getKey(), entry.getValue());
            }
            player.sendPacket(playerIU);
        }
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.EXTRACT_ITEM;
    }

    private void sendMessage(Player player, Item item, Long count) {
        final SystemMessage sm;
        if (count > 1) {
            sm = getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S2_S1).addItemName(item).addLong(count);
        } else if (item.getEnchantLevel() > 0) {
            sm = getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_A_S1_S2).addInt(item.getEnchantLevel()).addItemName(item);
        } else {
            sm = getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1).addItemName(item);
        }
        player.sendPacket(sm);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new RestorationRandom(data);
        }

        @Override
        public String effectName() {
            return "random-restoration";
        }
    }
}
