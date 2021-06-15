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
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.data.CommissionItemData;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.model.item.container.Attachment;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.commission.*;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionList.CommissionListReplyType;
import org.l2j.gameserver.world.zone.ZoneType;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author NosBit
 */
public final class CommissionManager {

    private static final int ITEMS_LIMIT_PER_REQUEST = 999;
    private static final int MAX_ITEMS_REGISTERED_PER_PLAYER = 10;
    private static final long MIN_REGISTRATION_AND_SALE_FEE = 1000;
    private static final double REGISTRATION_FEE_PER_DAY = 0.001;
    private static final double SALE_FEE_PER_DAY = 0.005;

    private final Map<Long, CommissionItem> _commissionItems = new ConcurrentSkipListMap<>();

    private CommissionManager() {
        for (var commissionItem : getDAO(ItemDAO.class).findCommissionItems()) {
            _commissionItems.put(commissionItem.getCommissionId(), commissionItem);
            if (commissionItem.getEndTime().isBefore(Instant.now())) {
                expireSale(commissionItem);
            } else {
                commissionItem.setSaleEndTask(ThreadPool.schedule(() -> expireSale(commissionItem), Duration.between(Instant.now(), commissionItem.getEndTime()).toMillis()));
            }
        }
    }

    /**
     * Checks if the player is allowed to interact with commission manager.
     *
     * @param player the player
     * @return {@code true} if the player is allowed to interact, {@code false} otherwise
     */
    public static boolean isPlayerAllowedToInteract(Player player) {
        return  player.isInsideZone(ZoneType.PEACE);
       /* final Npc npc = player.getLastFolkNPC();
        if (npc instanceof org.l2j.gameserver.model.actor.instance.CommissionManager) {
            return MathUtil.isInsideRadius3D(npc,player, INTERACTION_DISTANCE);
        }*/
    }

    /**
     * Shows the player the auctions filtered by filter.
     *
     * @param player the player
     * @param filter the filter
     */
    public void showAuctions(Player player, Predicate<ItemTemplate> filter) {
        //@formatter:off
        final List<CommissionItem> commissionItems = _commissionItems.values().stream()
                .filter(c -> filter.test(c.getItemInfo().getTemplate()))
                .limit(ITEMS_LIMIT_PER_REQUEST)
                .collect(Collectors.toList());
        //@formatter:on

        if (commissionItems.isEmpty()) {
            player.sendPacket(new ExResponseCommissionList(CommissionListReplyType.ITEM_DOES_NOT_EXIST));
            return;
        }

        int chunks = commissionItems.size() / ExResponseCommissionList.MAX_CHUNK_SIZE;
        if (commissionItems.size() > (chunks * ExResponseCommissionList.MAX_CHUNK_SIZE)) {
            chunks++;
        }

        for (int i = chunks - 1; i >= 0; i--) {
            player.sendPacket(new ExResponseCommissionList(CommissionListReplyType.AUCTIONS, commissionItems, i, i * ExResponseCommissionList.MAX_CHUNK_SIZE));
        }
    }

    /**
     * Shows the player his auctions.
     *
     * @param player the player
     */
    public void showPlayerAuctions(Player player) {
        //@formatter:off
        final List<CommissionItem> commissionItems = _commissionItems.values().stream()
                .filter(c -> c.getItemInstance().getOwnerId() == player.getObjectId())
                .limit(MAX_ITEMS_REGISTERED_PER_PLAYER)
                .collect(Collectors.toList());
        //@formatter:on

        if (!commissionItems.isEmpty()) {
            player.sendPacket(new ExResponseCommissionList(CommissionListReplyType.PLAYER_AUCTIONS, commissionItems));
        } else {
            player.sendPacket(new ExResponseCommissionList(CommissionListReplyType.PLAYER_AUCTIONS_EMPTY));
        }
    }

    /**
     * Registers an item for the given player.
     *
     * @param player         the player
     * @param itemObjectId   the item object id
     * @param itemCount      the item count
     * @param pricePerUnit   the price per unit
     * @param durationInDays the duration in days
     */
    public void registerItem(Player player, int itemObjectId, long itemCount, long pricePerUnit, byte durationInDays) {
        if (itemCount < 1) {
            player.sendPacket(SystemMessageId.THE_ITEM_HAS_FAILED_TO_BE_REGISTERED);
            player.sendPacket(ExResponseCommissionRegister.FAILED);
            return;
        }

        final long totalPrice = itemCount * pricePerUnit;
        if (totalPrice <= MIN_REGISTRATION_AND_SALE_FEE) {
            player.sendPacket(SystemMessageId.THE_ITEM_CANNOT_BE_REGISTERED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
            player.sendPacket(ExResponseCommissionRegister.FAILED);
            return;
        }

        Item itemInstance = player.getInventory().getItemByObjectId(itemObjectId);
        if ((itemInstance == null) || !itemInstance.isAvailable(player, false, false) || (itemInstance.getCount() < itemCount)) {
            player.sendPacket(SystemMessageId.THE_ITEM_HAS_FAILED_TO_BE_REGISTERED);
            player.sendPacket(ExResponseCommissionRegister.FAILED);
            return;
        }

        synchronized (this) {
            //@formatter:off
            final long playerRegisteredItems = _commissionItems.values().stream()
                    .filter(c -> c.getItemInstance().getOwnerId() == player.getObjectId())
                    .count();
            //@formatter:on

            if (playerRegisteredItems >= MAX_ITEMS_REGISTERED_PER_PLAYER) {
                player.sendPacket(SystemMessageId.THE_ITEM_HAS_FAILED_TO_BE_REGISTERED);
                player.sendPacket(ExResponseCommissionRegister.FAILED);
                return;
            }

            final long registrationFee = (long) Math.max(MIN_REGISTRATION_AND_SALE_FEE, (totalPrice * REGISTRATION_FEE_PER_DAY) * durationInDays);
            if (!player.getInventory().reduceAdena("Commission Registration Fee", registrationFee, player, null)) {
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_TO_REGISTER_THE_ITEM);
                player.sendPacket(ExResponseCommissionRegister.FAILED);
                return;
            }

            itemInstance = player.getInventory().detachItem("Commission Registration", itemInstance, itemCount, ItemLocation.COMMISSION, player, null);
            if (itemInstance == null) {
                player.getInventory().addAdena("Commission error refund", registrationFee, player, null);
                player.sendPacket(SystemMessageId.THE_ITEM_HAS_FAILED_TO_BE_REGISTERED);
                player.sendPacket(ExResponseCommissionRegister.FAILED);
                return;
            }

            CommissionItemData data = CommissionItemData.of(itemInstance.getObjectId(), pricePerUnit, durationInDays );
            getDAO(ItemDAO.class).save(data);

            final CommissionItem commissionItem = new CommissionItem(data, itemInstance);
            final ScheduledFuture<?> saleEndTask = ThreadPool.schedule(() -> expireSale(commissionItem), Duration.between(Instant.now(), commissionItem.getEndTime()).toMillis());
            commissionItem.setSaleEndTask(saleEndTask);
            _commissionItems.put(commissionItem.getCommissionId(), commissionItem);

            player.getLastCommissionInfos().put(itemInstance.getId(), new ExResponseCommissionInfo(itemInstance.getId(), pricePerUnit, itemCount, (byte) ((durationInDays - 1) / 2)));
            player.sendPacket(SystemMessageId.THE_ITEM_HAS_BEEN_SUCCESSFULLY_REGISTERED);
            player.sendPacket(ExResponseCommissionRegister.SUCCEED);
        }
    }

    /**
     * Deletes an item and returns it to the player.
     *
     * @param player       the player
     * @param commissionId the commission id
     */
    public void deleteItem(Player player, long commissionId) {
        final CommissionItem commissionItem = getCommissionItem(commissionId);
        if (commissionItem == null) {
            player.sendPacket(SystemMessageId.CANCELLATION_OF_SALE_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
            player.sendPacket(ExResponseCommissionDelete.FAILED);
            return;
        }

        if (commissionItem.getItemInstance().getOwnerId() != player.getObjectId()) {
            player.sendPacket(ExResponseCommissionDelete.FAILED);
            return;
        }

        if (!player.isInventoryUnder80() || (player.getWeightPenalty() >= 3)) {
            player.sendPacket(SystemMessageId.IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASE_CANCELLATION_IS_NOT_POSSIBLE);
            player.sendPacket(SystemMessageId.CANCELLATION_OF_SALE_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
            player.sendPacket(ExResponseCommissionDelete.FAILED);
            return;
        }

        if ((_commissionItems.remove(commissionId) == null) || !commissionItem.getSaleEndTask().cancel(false)) {
            player.sendPacket(SystemMessageId.CANCELLATION_OF_SALE_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
            player.sendPacket(ExResponseCommissionDelete.FAILED);
            return;
        }

        if (deleteItemFromDB(commissionId)) {
            player.getInventory().addItem("Commission Cancellation", commissionItem.getItemInstance(), player, null);
            player.sendPacket(SystemMessageId.CANCELLATION_OF_SALE_FOR_THE_ITEM_IS_SUCCESSFUL);
            player.sendPacket(ExResponseCommissionDelete.SUCCEED);
        } else {
            player.sendPacket(SystemMessageId.CANCELLATION_OF_SALE_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
            player.sendPacket(ExResponseCommissionDelete.FAILED);
        }
    }

    /**
     * Buys the item for the given player.
     *
     * @param player       the player
     * @param commissionId the commission id
     */
    public void buyItem(Player player, long commissionId) {
        final CommissionItem commissionItem = getCommissionItem(commissionId);
        if (commissionItem == null) {
            player.sendPacket(SystemMessageId.ITEM_PURCHASE_HAS_FAILED);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
            return;
        }

        final Item itemInstance = commissionItem.getItemInstance();
        if (itemInstance.getOwnerId() == player.getObjectId()) {
            player.sendPacket(SystemMessageId.ITEM_PURCHASE_HAS_FAILED);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
            return;
        }

        if (!player.isInventoryUnder80() || (player.getWeightPenalty() >= 3)) {
            player.sendPacket(SystemMessageId.IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASE_CANCELLATION_IS_NOT_POSSIBLE);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
            return;
        }

        final long totalPrice = itemInstance.getCount() * commissionItem.getPricePerUnit();
        if (!player.getInventory().reduceAdena("Commission Registration Fee", totalPrice, player, null)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
            return;
        }

        if ((_commissionItems.remove(commissionId) == null) || !commissionItem.getSaleEndTask().cancel(false)) {
            player.getInventory().addAdena("Commission error refund", totalPrice, player, null);
            player.sendPacket(SystemMessageId.ITEM_PURCHASE_HAS_FAILED);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
            return;
        }

        if (deleteItemFromDB(commissionId)) {
            final long saleFee = (long) Math.max(MIN_REGISTRATION_AND_SALE_FEE, (totalPrice * SALE_FEE_PER_DAY) * commissionItem.getDurationInDays());
            final var mail = MailData.of(itemInstance.getOwnerId(), itemInstance, MailType.COMMISSION_ITEM_SOLD);

            final Attachment attachement = new Attachment(mail.getSender(), mail.getId());
            attachement.addItem("Commission Item Sold", CommonItem.ADENA, totalPrice - saleFee, player, null);
            mail.attach(attachement);
            MailEngine.getInstance().sendMail(mail);

            player.sendPacket(new ExResponseCommissionBuyItem(commissionItem));
            player.getInventory().addItem("Commission Buy Item", commissionItem.getItemInstance(), player, null);
        } else {
            player.getInventory().addAdena("Commission error refund", totalPrice, player, null);
            player.sendPacket(ExResponseCommissionBuyItem.FAILED);
        }
    }

    /**
     * Deletes a commission item from database.
     *
     * @param commissionId the commission item
     * @return {@code true} if the item was deleted successfully, {@code false} otherwise
     */
    private boolean deleteItemFromDB(long commissionId) {
        return getDAO(ItemDAO.class).deleteCommission(commissionId);
    }

    /**
     * Expires the sale of a commission item and sends the item back to the player.
     *
     * @param commissionItem the comission item
     */
    private void expireSale(CommissionItem commissionItem) {
        if ((_commissionItems.remove(commissionItem.getCommissionId()) != null) && deleteItemFromDB(commissionItem.getCommissionId())) {
            final var mail = MailData.of(commissionItem.getItemInstance().getOwnerId(), commissionItem.getItemInstance(), MailType.COMMISSION_ITEM_RETURNED);
            MailEngine.getInstance().sendMail(mail);
        }
    }

    /**
     * Gets the commission item.
     *
     * @param commissionId the commission id to get
     * @return the commission item if it exists, {@code null} otherwise
     */
    public CommissionItem getCommissionItem(long commissionId) {
        return _commissionItems.get(commissionId);
    }

    /**
     * @param objectId
     * @return {@code true} if player with the objectId has commission items, {@code false} otherwise
     */
    public boolean hasCommissionItems(int objectId) {
        return _commissionItems.values().stream().anyMatch(item -> item.getItemInstance().getObjectId() == objectId);
    }

    public static CommissionManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CommissionManager INSTANCE = new CommissionManager();
    }
}
