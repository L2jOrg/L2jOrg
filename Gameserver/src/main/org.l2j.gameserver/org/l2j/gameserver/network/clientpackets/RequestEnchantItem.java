package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.EnchantItemData;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.enchant.EnchantResultType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.EnchantResult;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.item.ExItemAnnounce;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.network.serverpackets.item.ItemAnnounceType.ENHANCEMENT;

public final class RequestEnchantItem extends ClientPacket {

    private static final Logger LOGGER_ENCHANT = LoggerFactory.getLogger("enchant.items");

    private int objectId;
    private int supportId;

    @Override
    public void readImpl() {
        objectId = readInt();
        supportId = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        final var request = player.getRequest(EnchantItemRequest.class);
        if (isNull(request) || request.isProcessing()) {
            return;
        }

        request.setEnchantingItem(objectId);
        request.setProcessing(true);

        if (!player.isOnline() || client.isDetached()) {
            player.removeRequest(request.getClass());
            return;
        }

        if (player.isProcessingTransaction() || player.isInStoreMode()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            player.removeRequest(request.getClass());
            return;
        }

        final Item item = request.getEnchantingItem();
        final Item scroll = request.getEnchantingScroll();
        if (isNull(item) || isNull(scroll)) {
            player.removeRequest(request.getClass());
            return;
        }

        final var scrollTemplate = EnchantItemData.getInstance().getEnchantScroll(scroll);
        if (isNull(scrollTemplate)) {
            return;
        }

        // first validation check - also over enchant check
        if (!scrollTemplate.isValid(item) || (Config.DISABLE_OVER_ENCHANTING && (item.getEnchantLevel() == scrollTemplate.getMaxEnchantLevel()))) {
            client.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            player.removeRequest(request.getClass());
            client.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
            return;
        }

        // fast auto-enchant cheat check
        if (request.getTimestamp() == 0 || (System.currentTimeMillis() - request.getTimestamp()) < 2000 ) {
            GameUtils.handleIllegalPlayerAction(player, player + " use auto enchant program");
            player.removeRequest(request.getClass());
            client.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
            return;
        }

        // attempting to destroy scroll
        if (player.getInventory().destroyItem("Enchant", scroll.getObjectId(), 1, player, item) == null) {
            client.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            GameUtils.handleIllegalPlayerAction(player, player + " tried to enchant with a scroll he doesn't have");
            player.removeRequest(request.getClass());
            client.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
            return;
        }

        final InventoryUpdate iu = new InventoryUpdate();
        synchronized (item) {
            // last validation check
            if ((item.getOwnerId() != player.getObjectId()) || (!item.isEnchantable())) {
                client.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
                player.removeRequest(request.getClass());
                client.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
                return;
            }

            final EnchantResultType resultType = scrollTemplate.calculateSuccess(player, item);
            switch (resultType) {
                case ERROR: {
                    client.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
                    player.removeRequest(request.getClass());
                    client.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));
                    break;
                }
                case SUCCESS: {
                    final ItemTemplate it = item.getTemplate();
                    // Increase enchant level only if scroll's base template has chance, some armors can success over +20 but they shouldn't have increased.
                    if (scrollTemplate.getChance(player, item) > 0) {
                        if (scrollTemplate.isGiant()) {
                            item.setEnchantLevel(Math.min(item.getEnchantLevel() + 1 + Rnd.get(3), scrollTemplate.getMaxEnchantLevel()));
                        } else {
                            item.setEnchantLevel(item.getEnchantLevel() + 1);
                        }
                        item.updateDatabase();
                    }
                    client.sendPacket(new EnchantResult(EnchantResult.SUCCESS, item));

                    if (Config.LOG_ITEM_ENCHANTS) {
                        LOGGER_ENCHANT.info("Success, {} Account {} IP: {}, Enchant {} {}, with scroll {}", player,  player.getAccountName(), player.getIPAddress(), item.getEnchantLevel(), item, scroll);
                    }

                    final int minEnchantAnnounce = item.isArmor() ? getSettings(CharacterSettings.class).minimumEnchantAnnounceArmor() : getSettings(CharacterSettings.class).minimumEnchantAnnounceWeapon();
                    if (minEnchantAnnounce > 0 && item.getEnchantLevel() >= minEnchantAnnounce) {

                        Broadcast.toAllOnlinePlayers(new ExItemAnnounce(ENHANCEMENT, player, item),
                                        getSystemMessage(SystemMessageId.C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3).addPcName(player).addInt(item.getEnchantLevel()).addItemName(item));

                        doIfNonNull(CommonSkill.FIREWORK.getSkill(), skill ->
                                player.broadcastPacket(new MagicSkillUse(player, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay())));

                    }

                    if (item.isEquipped()) {
                        if (item.isArmor()) {
                            it.forEachSkill(ItemSkillType.ON_ENCHANT, holder ->
                            {
                                // add skills bestowed from +4 armor
                                if (item.getEnchantLevel() >= holder.getValue()) {
                                    player.addSkill(holder.getSkill(), false);
                                    player.sendSkillList();
                                }
                            });
                        }
                        player.broadcastUserInfo(); // update user info
                    }
                    break;
                }
                case FAILURE: {
                    if (scrollTemplate.isSafe()) {
                        // safe enchant - remain old value
                        client.sendPacket(SystemMessageId.ENCHANT_FAILED_THE_ENCHANT_SKILL_FOR_THE_CORRESPONDING_ITEM_WILL_BE_EXACTLY_RETAINED);
                        client.sendPacket(new EnchantResult(EnchantResult.SAFE_FAIL, item));

                        if (Config.LOG_ITEM_ENCHANTS) {
                            if (item.getEnchantLevel() > 0) {
                                LOGGER_ENCHANT.info("Safe Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + item.getEnchantLevel() + " " + item.getName() + "(" + item.getCount() + ") [" + item.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
                            } else {
                                LOGGER_ENCHANT.info("Safe Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", " + item.getName() + "(" + item.getCount() + ") [" + item.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
                            }
                        }
                    } else {
                        // unequip item on enchant failure to avoid item skills stack
                        if (item.isEquipped()) {
                            if (item.getEnchantLevel() > 0) {
                                final SystemMessage sm = getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
                                sm.addInt(item.getEnchantLevel());
                                sm.addItemName(item);
                                client.sendPacket(sm);
                            } else {
                                final SystemMessage sm = getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
                                sm.addItemName(item);
                                client.sendPacket(sm);
                            }

                            var unequiped = player.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(item.getLocationSlot()));
                            for (Item itm : unequiped) {
                                iu.addModifiedItem(itm);
                            }

                            player.sendInventoryUpdate(iu);
                            player.broadcastUserInfo();
                        }

                        if (scrollTemplate.isBlessed()) {
                            // blessed enchant - clear enchant value
                            client.sendPacket(SystemMessageId.THE_BLESSED_ENCHANT_FAILED_THE_ENCHANT_VALUE_OF_THE_ITEM_BECAME_0);

                            item.setEnchantLevel(0);
                            item.updateDatabase();
                            client.sendPacket(new EnchantResult(EnchantResult.BLESSED_FAIL, 0, 0));

                            if (Config.LOG_ITEM_ENCHANTS) {
                                if (item.getEnchantLevel() > 0) {
                                    LOGGER_ENCHANT.info("Blessed Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + item.getEnchantLevel() + " " + item.getName() + "(" + item.getCount() + ") [" + item.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
                                } else  {
                                    LOGGER_ENCHANT.info("Blessed Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", " + item.getName() + "(" + item.getCount() + ") [" + item.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
                                }
                            }
                        } else {
                            // enchant failed, destroy item
                            final int crystalId = item.getTemplate().getCrystalItemId();
                            int count = item.getCrystalCount() - ((item.getTemplate().getCrystalCount() + 1) / 2);
                            if (count < 1) {
                                count = 1;
                            }

                            if (player.getInventory().destroyItem("Enchant", item, player, null) == null) {
                                // unable to destroy item, cheater ?
                                GameUtils.handleIllegalPlayerAction(player, "Unable to delete item on enchant failure from player " + player.getName() + ", possible cheater !");
                                player.removeRequest(request.getClass());
                                client.sendPacket(new EnchantResult(EnchantResult.ERROR, 0, 0));

                                if (Config.LOG_ITEM_ENCHANTS) {
                                    if (item.getEnchantLevel() > 0) {
                                        LOGGER_ENCHANT.info("Unable to destroy, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + item.getEnchantLevel() + " " + item.getName() + "(" + item.getCount() + ") [" + item.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
                                    } else  {
                                        LOGGER_ENCHANT.info("Unable to destroy, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", " + item.getName() + "(" + item.getCount() + ") [" + item.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
                                    }
                                }
                                return;
                            }

                            World.getInstance().removeObject(item);
                            Item crystals = null;
                            if (crystalId != 0) {
                                crystals = player.getInventory().addItem("Enchant", crystalId, count, player, item);

                                final SystemMessage sm = getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
                                sm.addItemName(crystals);
                                sm.addLong(count);
                                client.sendPacket(sm);
                            }

                            if (!Config.FORCE_INVENTORY_UPDATE) {
                                if (crystals != null) {
                                    iu.addItem(crystals);
                                }
                            }

                            if (crystalId == 0) {
                                client.sendPacket(new EnchantResult(EnchantResult.NO_CRYSTAL, 0, 0));
                            } else {
                                client.sendPacket(new EnchantResult(EnchantResult.FAIL, crystalId, count));
                            }

                            if (Config.LOG_ITEM_ENCHANTS) {
                                if (item.getEnchantLevel() > 0) {
                                    LOGGER_ENCHANT.info("Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + item.getEnchantLevel() + " " + item.getName() + "(" + item.getCount() + ") [" + item.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
                                } else {
                                    LOGGER_ENCHANT.info("Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", " + item.getName() + "(" + item.getCount() + ") [" + item.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
                                }
                            }
                        }
                    }
                    break;
                }
            }

            player.sendItemList();

            request.setProcessing(false);
            player.broadcastUserInfo(UserInfoType.ENCHANTLEVEL);
        }
    }
}
