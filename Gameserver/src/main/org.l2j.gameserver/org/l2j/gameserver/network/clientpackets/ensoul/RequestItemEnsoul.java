/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.clientpackets.ensoul;

import org.l2j.gameserver.engine.item.*;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.ensoul.ExEnsoulResult;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class RequestItemEnsoul extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestItemEnsoul.class);
    private int itemObjectId;
    private EnsoulItemOption[] options;

    @Override
    public void readImpl() {
        itemObjectId = readInt();
        final int amount = readByte();
        if (amount > 0 && amount <= 2) {
            this.options = new EnsoulItemOption[amount];
            for (int i = 0; i < amount; i++) {
                final int type = readByte(); // 1 = normal ; 2 = special
                final int position = readByte();
                final int soulCrystalObjectId = readInt();
                final int soulCrystalOption = readInt();
                if (type == 1 || type == 2) {
                    this.options[i] = new EnsoulItemOption(type, position, soulCrystalObjectId, soulCrystalOption);
                }
            }
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        if (isNull(player) || isNull(options) || options.length == 0 || !canEnsoulItem(player)) {
            return;
        }

        final Item item = player.getInventory().getItemByObjectId(itemObjectId);
        if(!isValid(player, item)) {
            return;
        }

        boolean success = false;
        final InventoryUpdate iu = new InventoryUpdate();
        for (EnsoulItemOption itemOption : options) {
            final Item soulCrystal = player.getInventory().getItemByObjectId(itemOption.getSoulCrystalObjectId());
            if (soulCrystal == null) {
                player.sendPacket(SystemMessageId.THE_RUNE_DOES_NOT_FIT);
                continue;
            }

            final EnsoulStone stone = ItemEnsoulEngine.getInstance().getStone(soulCrystal.getId());
            if (isNull(stone)) {
                continue;
            }

            if (!stone.containsOption(itemOption.getSoulCrystalOption())) {
                LOGGER.warn("Player: {} attempting to ensoul item option that stone doesn't contains!", player);
                continue;
            }

            final EnsoulOption option = ItemEnsoulEngine.getInstance().getOption(itemOption.getSoulCrystalOption());
            if (isNull(option)) {
                LOGGER.warn("Player: {} attempting to ensoul item option that doesn't exists!", player);
                continue;
            }

            ItemHolder fee = calculateFee(item, itemOption);

            if (isNull(fee)) {
                LOGGER.warn("Player: {} attempting to ensoul item option {} that doesn't exists! (unknown fee)", player, option);
                continue;
            }

            final Item gemStones = player.getInventory().getItemByItemId(fee.getId());
            if (isNull(gemStones) || gemStones.getCount() < fee.getCount()) {
                continue;
            }

            if (player.destroyItem("EnsoulOption", soulCrystal, 1, player, true) &&
                    player.destroyItem("EnsoulOption", gemStones, fee.getCount(), player, true)) {
                item.addSpecialAbility(option, stone.type(), true);
                success = true;
            }

            iu.addModifiedItem(item);
        }
        player.sendInventoryUpdate(iu);
        if (item.isEquipped()) {
            item.applySpecialAbilities();
        }
        player.sendPacket(new ExEnsoulResult(success, item));
        item.updateDatabase(true);

    }

    private ItemHolder calculateFee(Item item, EnsoulItemOption itemOption) {
        ItemHolder fee = null;
        if (itemOption.getType() == EnsoulType.COMMON) {
            if(nonNull(item.getSpecialAbility())) {
                fee = ItemEnsoulEngine.getInstance().getReplaceEnsoulFee(item.getCrystalType(), itemOption.getType());
            } else {
                fee = ItemEnsoulEngine.getInstance().getEnsoulFee(item.getCrystalType(), itemOption.getType());
            }
        } else if (itemOption.getType() == EnsoulType.SPECIAL) {
            if(nonNull(item.getAdditionalSpecialAbility())) {
                fee = ItemEnsoulEngine.getInstance().getReplaceEnsoulFee(item.getCrystalType(), itemOption.getType());
            } else {
                fee = ItemEnsoulEngine.getInstance().getEnsoulFee(item.getCrystalType(), itemOption.getType());
            }
        }
        return fee;
    }

    private boolean isValid(Player player, Item item) {
        if (isNull(item)) {
            LOGGER.warn("Player: {} attempting to ensoul item without having it!", player);
            return false;
        } else if (!item.isEquipable()) {
            LOGGER.warn("Player: {} attempting to ensoul non equippable item: {}!", player, item);
            return false;
        } else if (!item.isWeapon()) {
            LOGGER.warn("Player: {} attempting to ensoul item that's not a weapon: {}!", player, item);
            return false;
        } else if (item.isHeroItem()) {
            LOGGER.warn("Player: {} attempting to ensoul hero item: {}!", player, item);
            return false;
        }
        return true;
    }

    private boolean canEnsoulItem(Player player) {
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHEN_PRIVATE_STORE_AND_WORKSHOP_ARE_OPENED);
            return false;
        } else if (player.hasAbnormalType(AbnormalType.FREEZING)) {
            player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_IN_FROZEN_STATE);
        } else if (player.isDead()) {
            player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_IF_THE_CHARACTER_IS_DEAD);
            return false;
        } else if (nonNull(player.getActiveTradeList()) || player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_DURING_EXCHANGE);
            return false;
        } else if (player.hasAbnormalType(AbnormalType.PARALYZE)) {
            player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_PETRIFIED);
            return false;
        } else if (player.isFishing()) {
            player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_DURING_FISHING);
            return false;
        } else if (player.isSitting()) {
            player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_SITTING);
            return false;
        } else if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player)) {
            player.sendPacket(SystemMessageId.RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_IN_COMBAT);
            return false;
        }
        return true;
    }

    static class EnsoulItemOption {
        private final EnsoulType _type;
        private final int _position;
        private final int _soulCrystalObjectId;
        private final int _soulCrystalOption;

        EnsoulItemOption(int type, int position, int soulCrystalObjectId, int soulCrystalOption) {
            _type = EnsoulType.from(type);
            _position = position;
            _soulCrystalObjectId = soulCrystalObjectId;
            _soulCrystalOption = soulCrystalOption;
        }

        public EnsoulType getType() {
            return _type;
        }

        public int getPosition() {
            return _position;
        }

        public int getSoulCrystalObjectId() {
            return _soulCrystalObjectId;
        }

        public int getSoulCrystalOption() {
            return _soulCrystalOption;
        }
    }
}
