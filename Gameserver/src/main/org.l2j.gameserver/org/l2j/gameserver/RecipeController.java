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
package org.l2j.gameserver;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.StatType;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.WorldTimeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.gameserver.model.DamageInfo.DamageType;


public class RecipeController {
    private static final Map<Integer, RecipeItemMaker> _activeMakers = new ConcurrentHashMap<>();

    private RecipeController() { }

    public void requestBookOpen(Player player, boolean isDwarvenCraft) {
        // Check if player is trying to alter recipe book while engaged in manufacturing.
        if (!_activeMakers.containsKey(player.getObjectId())) {
            final RecipeBookItemList response = new RecipeBookItemList(isDwarvenCraft, player.getMaxMp());
            response.addRecipes(isDwarvenCraft ? player.getDwarvenRecipeBook() : player.getCommonRecipeBook());
            player.sendPacket(response);
            return;
        }
        player.sendPacket(SystemMessageId.YOU_MAY_NOT_ALTER_YOUR_RECIPE_BOOK_WHILE_ENGAGED_IN_MANUFACTURING);
    }

    public void requestMakeItemAbort(Player player) {
        _activeMakers.remove(player.getObjectId()); // TODO: anything else here?
    }

    public void requestManufactureItem(Player manufacturer, int recipeListId, Player player) {
        final RecipeList recipeList = RecipeData.getInstance().getValidRecipeList(player, recipeListId);
        if (recipeList == null) {
            return;
        }

        final List<RecipeList> dwarfRecipes = Arrays.asList(manufacturer.getDwarvenRecipeBook());
        final List<RecipeList> commonRecipes = Arrays.asList(manufacturer.getCommonRecipeBook());

        if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList)) {
            GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.");
            return;
        }

        // Check if manufacturer is under manufacturing store or private store.
        if (Config.ALT_GAME_CREATION && _activeMakers.containsKey(manufacturer.getObjectId())) {
            player.sendPacket(SystemMessageId.PLEASE_CLOSE_THE_SETUP_WINDOW_FOR_YOUR_PRIVATE_WORKSHOP_OR_PRIVATE_STORE_AND_TRY_AGAIN);
            return;
        }

        final RecipeItemMaker maker = new RecipeItemMaker(manufacturer, recipeList, player);
        if (maker._isValid) {
            if (Config.ALT_GAME_CREATION) {
                _activeMakers.put(manufacturer.getObjectId(), maker);
                ThreadPool.schedule(maker, 100);
            } else {
                maker.run();
            }
        }
    }

    public void requestMakeItem(Player player, int recipeListId) {
        // Check if player is trying to operate a private store or private workshop while engaged in combat.
        if (player.isInCombat() || player.isInDuel()) {
            player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            return;
        }

        final RecipeList recipeList = RecipeData.getInstance().getValidRecipeList(player, recipeListId);
        if (recipeList == null) {
            return;
        }

        final List<RecipeList> dwarfRecipes = Arrays.asList(player.getDwarvenRecipeBook());
        final List<RecipeList> commonRecipes = Arrays.asList(player.getCommonRecipeBook());

        if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList)) {
            GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false recipe id.");
            return;
        }

        // Check if player is busy (possible if alt game creation is enabled)
        if (Config.ALT_GAME_CREATION && _activeMakers.containsKey(player.getObjectId())) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1);
            sm.addItemName(recipeList.getItemId());
            sm.addString("You are busy creating.");
            player.sendPacket(sm);
            return;
        }

        final RecipeItemMaker maker = new RecipeItemMaker(player, recipeList, player);
        if (maker._isValid) {
            if (Config.ALT_GAME_CREATION) {
                _activeMakers.put(player.getObjectId(), maker);
                ThreadPool.schedule(maker, 100);
            } else {
                maker.run();
            }
        }
    }

    private static class RecipeItemMaker implements Runnable {
        private static final Logger LOGGER = LoggerFactory.getLogger(RecipeItemMaker.class);

        protected final RecipeList _recipeList;
        protected final Player _player; // "crafter"
        protected final Player _target; // "customer"
        protected final Skill _skill;
        protected final int _skillId;
        protected final int _skillLevel;
        protected boolean _isValid;
        protected List<TempItem> _items = null;
        protected int _creationPasses = 1;
        protected int _itemGrab;
        protected long _exp = -1;
        protected long _sp = -1;
        protected long _price;
        protected int _totalItems;
        protected int _delay;
        public RecipeItemMaker(Player pPlayer, RecipeList pRecipeList, Player pTarget) {
            _player = pPlayer;
            _target = pTarget;
            _recipeList = pRecipeList;

            _isValid = false;
            _skillId = _recipeList.isDwarvenRecipe() ? CommonSkill.CREATE_DWARVEN.getId() : CommonSkill.CREATE_COMMON.getId();
            _skillLevel = _player.getSkillLevel(_skillId);
            _skill = _player.getKnownSkill(_skillId);

            _player.setIsCrafting(true);

            if (_player.isAlikeDead()) {
                _player.sendPacket(ActionFailed.STATIC_PACKET);
                abort();
                return;
            }

            if (_target.isAlikeDead()) {
                _target.sendPacket(ActionFailed.STATIC_PACKET);
                abort();
                return;
            }

            if (_target.isProcessingTransaction()) {
                _target.sendPacket(ActionFailed.STATIC_PACKET);
                abort();
                return;
            }

            if (_player.isProcessingTransaction()) {
                _player.sendPacket(ActionFailed.STATIC_PACKET);
                abort();
                return;
            }

            // validate recipe list
            if (_recipeList.getRecipes().length == 0) {
                _player.sendPacket(ActionFailed.STATIC_PACKET);
                abort();
                return;
            }

            // validate skill level
            if (_recipeList.getLevel() > _skillLevel) {
                _player.sendPacket(ActionFailed.STATIC_PACKET);
                abort();
                return;
            }

            // check that customer can afford to pay for creation services
            if (_player != _target) {
                final ManufactureItem item = _player.getManufactureItems().get(_recipeList.getId());
                if (item != null) {
                    _price = item.getCost();
                    if (_target.getAdena() < _price) // check price
                    {
                        _target.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                        abort();
                        return;
                    }
                }
            }

            // make temporary items
            _items = listItems(false);
            if (_items == null) {
                abort();
                return;
            }

            for (TempItem i : _items) {
                _totalItems += i.getQuantity();
            }

            // initial statUse checks
            if (!calculateStatUse(false, false)) {
                abort();
                return;
            }

            // initial AltStatChange checks
            if (Config.ALT_GAME_CREATION) {
                calculateAltStatChange();
            }

            updateMakeInfo(true);
            updateCurMp();
            updateCurLoad();

            _player.setIsCrafting(false);
            _isValid = true;
        }

        @Override
        public void run() {
            if (!Config.IS_CRAFTING_ENABLED) {
                _target.sendMessage("Item creation is currently disabled.");
                abort();
                return;
            }

            if ((_player == null) || (_target == null)) {
                LOGGER.warn("player or target == null (disconnected?), aborting" + _target + _player);
                abort();
                return;
            }

            if (!_player.isOnline() || !_target.isOnline()) {
                LOGGER.warn("player or target is not online, aborting " + _target + _player);
                abort();
                return;
            }

            if (Config.ALT_GAME_CREATION && !_activeMakers.containsKey(_player.getObjectId())) {
                if (_target != _player) {
                    _target.sendMessage("Manufacture aborted");
                    _player.sendMessage("Manufacture aborted");
                } else {
                    _player.sendMessage("Item creation aborted");
                }

                abort();
                return;
            }

            if (Config.ALT_GAME_CREATION && !_items.isEmpty()) {
                if (!calculateStatUse(true, true)) {
                    return; // check stat use
                }
                updateCurMp(); // update craft window mp bar

                grabSomeItems(); // grab (equip) some more items with a nice msg to player

                // if still not empty, schedule another pass
                if (!_items.isEmpty()) {
                    _delay = (int) (Config.ALT_GAME_CREATION_SPEED * _player.getStats().getReuseTime(_skill) * WorldTimeController.TICKS_PER_SECOND * WorldTimeController.MILLIS_IN_TICK);

                    // FIXME: please fix this packet to show crafting animation (somebody)
                    final MagicSkillUse msk = new MagicSkillUse(_player, _skillId, _skillLevel, _delay, 0);
                    _player.broadcastPacket(msk);

                    _player.sendPacket(new SetupGauge(_player.getObjectId(), 0, _delay));
                    ThreadPool.schedule(this, 100 + _delay);
                } else {
                    // for alt mode, sleep delay msec before finishing
                    _player.sendPacket(new SetupGauge(_player.getObjectId(), 0, _delay));

                    try {
                        Thread.sleep(_delay);
                    } catch (InterruptedException e) {
                    } finally {
                        finishCrafting();
                    }
                }
            } // for old craft mode just finish
            else {
                finishCrafting();
            }
        }

        private void finishCrafting() {
            if (!Config.ALT_GAME_CREATION) {
                calculateStatUse(false, true);
            }

            // first take adena for manufacture
            if ((_target != _player) && (_price > 0)) // customer must pay for services
            {
                // attempt to pay for item
                final Item adenatransfer = _target.transferItem("PayManufacture", _target.getInventory().getAdenaInstance().getObjectId(), _price, _player.getInventory(), _player);

                if (adenatransfer == null) {
                    _target.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                    abort();
                    return;
                }
            }
            _items = listItems(true); // this line actually takes materials from inventory
            if (_items == null) {
                // handle possible cheaters here
                // (they click craft then try to get rid of items in order to get free craft)
            } else if (Rnd.get(100) < getCraftChanceRate()) {
                rewardPlayer(); // and immediately puts created item in its place
                updateMakeInfo(true);
            } else {
                if (_target != _player) {
                    SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_FAILED_TO_CREATE_S2_FOR_C1_AT_THE_PRICE_OF_S3_ADENA);
                    msg.addString(_target.getName());
                    msg.addItemName(_recipeList.getItemId());
                    msg.addLong(_price);
                    _player.sendPacket(msg);

                    msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_FAILED_TO_CREATE_S2_AT_THE_PRICE_OF_S3_ADENA);
                    msg.addString(_player.getName());
                    msg.addItemName(_recipeList.getItemId());
                    msg.addLong(_price);
                    _target.sendPacket(msg);
                } else {
                    _target.sendPacket(SystemMessageId.YOU_FAILED_AT_MIXING_THE_ITEM);
                }
                updateMakeInfo(false);
            }
            // update load and mana bar of craft window
            updateCurMp();
            _activeMakers.remove(_player.getObjectId());
            _player.setIsCrafting(false);
            _target.sendItemList();
        }

        private double getCraftChanceRate() {
            return _player.getStats().getValue(Stat.CRAFT_RATE_MASTER, _recipeList.getSuccessRate());
        }

        private double getCraftCriticalRate() {
            return _player.getStats().getValue(Stat.CRAFT_RATE_CRITICAL, Config.BASE_CRITICAL_CRAFT_RATE);
        }

        private void updateMakeInfo(boolean success) {
            if (_target == _player) {
                _target.sendPacket(new RecipeItemMakeInfo(_recipeList.getId(), _target, success));
            } else {
                _target.sendPacket(new RecipeShopItemInfo(_player, _recipeList.getId()));
            }
        }

        private void updateCurLoad() {
            _target.sendPacket(new ExUserInfoInvenWeight(_target));
        }

        private void updateCurMp() {
            final StatusUpdate su = new StatusUpdate(_target);
            su.addUpdate(StatusUpdateType.CUR_MP, (int) _target.getCurrentMp());
            _target.sendPacket(su);
        }

        private void grabSomeItems() {
            int grabItems = _itemGrab;
            while ((grabItems > 0) && !_items.isEmpty()) {
                final TempItem item = _items.get(0);

                int count = item.getQuantity();
                if (count >= grabItems) {
                    count = grabItems;
                }

                item.setQuantity(item.getQuantity() - count);
                if (item.getQuantity() <= 0) {
                    _items.remove(0);
                } else {
                    _items.set(0, item);
                }

                grabItems -= count;

                if (_target == _player) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPPED_S1_S2); // you equipped ...
                    sm.addLong(count);
                    sm.addItemName(item.getItemId());
                    _player.sendPacket(sm);
                } else {
                    _target.sendMessage("Manufacturer " + _player.getName() + " used " + count + " " + item.getItemName());
                }
            }
        }

        // AltStatChange parameters make their effect here

        private void calculateAltStatChange() {
            _itemGrab = _skillLevel;

            for (RecipeStat altStatChange : _recipeList.getAltStatChange()) {
                if (altStatChange.getType() == StatType.XP) {
                    _exp = altStatChange.getValue();
                } else if (altStatChange.getType() == StatType.SP) {
                    _sp = altStatChange.getValue();
                } else if (altStatChange.getType() == StatType.GIM) {
                    _itemGrab *= altStatChange.getValue();
                }
            }
            // determine number of creation passes needed
            _creationPasses = (_totalItems / _itemGrab) + ((_totalItems % _itemGrab) != 0 ? 1 : 0);
            if (_creationPasses < 1) {
                _creationPasses = 1;
            }
        }
        // StatUse

        private boolean calculateStatUse(boolean isWait, boolean isReduce) {
            boolean ret = true;
            for (RecipeStat statUse : _recipeList.getStatUse()) {
                final double modifiedValue = statUse.getValue() / _creationPasses;
                if (statUse.getType() == StatType.HP) {
                    // we do not want to kill the player, so its CurrentHP must be greater than the reduce value
                    if (_player.getCurrentHp() <= modifiedValue) {
                        // rest (wait for HP)
                        if (Config.ALT_GAME_CREATION && isWait) {
                            _player.sendPacket(new SetupGauge(_player.getObjectId(), 0, _delay));
                            ThreadPool.schedule(this, 100 + _delay);
                        } else {
                            _target.sendPacket(SystemMessageId.NOT_ENOUGH_HP);
                            abort();
                        }
                        ret = false;
                    } else if (isReduce) {
                        _player.reduceCurrentHp(modifiedValue, _player, _skill, DamageType.OTHER);
                    }
                } else if (statUse.getType() == StatType.MP) {
                    if (_player.getCurrentMp() < modifiedValue) {
                        // rest (wait for MP)
                        if (Config.ALT_GAME_CREATION && isWait) {
                            _player.sendPacket(new SetupGauge(_player.getObjectId(), 0, _delay));
                            ThreadPool.schedule(this, 100 + _delay);
                        } else {
                            _target.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
                            abort();
                        }
                        ret = false;
                    } else if (isReduce) {
                        _player.reduceCurrentMp(modifiedValue);
                    }
                } else {
                    // there is an unknown StatUse value
                    _target.sendMessage("Recipe error!!!, please tell this to your GM.");
                    ret = false;
                    abort();
                }
            }
            return ret;
        }
        private List<TempItem> listItems(boolean remove) {
            final Recipe[] recipes = _recipeList.getRecipes();
            final Inventory inv = _target.getInventory();
            final List<TempItem> materials = new ArrayList<>();
            SystemMessage sm;

            for (Recipe recipe : recipes) {
                if (recipe.getQuantity() > 0) {
                    final Item item = inv.getItemByItemId(recipe.getItemId());
                    final long itemQuantityAmount = item == null ? 0 : item.getCount();

                    // check materials
                    if (itemQuantityAmount < recipe.getQuantity()) {
                        sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_NEED_S2_MORE_S1_S);
                        sm.addItemName(recipe.getItemId());
                        sm.addLong(recipe.getQuantity() - itemQuantityAmount);
                        _target.sendPacket(sm);

                        abort();
                        return null;
                    }

                    // make new temporary object, just for counting purposes
                    materials.add(new TempItem(item, recipe.getQuantity()));
                }
            }

            if (remove) {
                for (TempItem tmp : materials) {
                    inv.destroyItemByItemId("Manufacture", tmp.getItemId(), tmp.getQuantity(), _target, _player);

                    if (tmp.getQuantity() > 1) {
                        sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                        sm.addItemName(tmp.getItemId());
                        sm.addLong(tmp.getQuantity());
                        _target.sendPacket(sm);
                    } else {
                        sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                        sm.addItemName(tmp.getItemId());
                        _target.sendPacket(sm);
                    }
                }
            }
            return materials;
        }

        private void abort() {
            updateMakeInfo(false);
            _player.setIsCrafting(false);
            _activeMakers.remove(_player.getObjectId());
        }

        private void rewardPlayer() {
            final int rareProdId = _recipeList.getRareItemId();
            int itemId = _recipeList.getItemId();
            int itemCount = _recipeList.getCount();

            final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);

            // TODO: This test should be moved after code below if applicable on rare production items (@Pearlbear)
            if (Rnd.get(100) < getCraftCriticalRate()) {
                itemCount *= 2;
                // TODO: Send message for critical craft or not ? (@Pearlbear)
            }

            // check that the current recipe has a rare production or not
            if ((rareProdId != -1) && ((rareProdId == itemId) || Config.CRAFT_MASTERWORK)) {
                if (Rnd.get(100) < _recipeList.getRarity()) {
                    itemId = rareProdId;
                    itemCount = _recipeList.getRareCount();
                }
            }

            _target.getInventory().addItem("Manufacture", itemId, itemCount, _target, _player);

            // inform customer of earned item
            SystemMessage sm = null;
            if (_target != _player) {
                // inform manufacturer of earned profit
                if (itemCount == 1) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HAS_BEEN_CREATED_FOR_C1_AFTER_THE_PAYMENT_OF_S3_ADENA_WAS_RECEIVED);
                    sm.addString(_target.getName());
                    sm.addItemName(itemId);
                    sm.addLong(_price);
                    _player.sendPacket(sm);

                    sm = SystemMessage.getSystemMessage(SystemMessageId.C1_CREATED_S2_AFTER_RECEIVING_S3_ADENA);
                    sm.addString(_player.getName());
                    sm.addItemName(itemId);
                    sm.addLong(_price);
                    _target.sendPacket(sm);
                } else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S3_S2_S_HAVE_BEEN_CREATED_FOR_C1_AT_THE_PRICE_OF_S4_ADENA);
                    sm.addString(_target.getName());
                    sm.addInt(itemCount);
                    sm.addItemName(itemId);
                    sm.addLong(_price);
                    _player.sendPacket(sm);

                    sm = SystemMessage.getSystemMessage(SystemMessageId.C1_CREATED_S3_S2_S_AT_THE_PRICE_OF_S4_ADENA);
                    sm.addString(_player.getName());
                    sm.addInt(itemCount);
                    sm.addItemName(itemId);
                    sm.addLong(_price);
                    _target.sendPacket(sm);
                }
            }

            if (itemCount > 1) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
                sm.addItemName(itemId);
                sm.addLong(itemCount);
                _target.sendPacket(sm);
            } else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
                sm.addItemName(itemId);
                _target.sendPacket(sm);
            }

            if (Config.ALT_GAME_CREATION) {
                final int recipeLevel = _recipeList.getLevel();
                if (_exp < 0) {
                    _exp = template.getReferencePrice() * itemCount;
                    _exp /= recipeLevel;
                }
                if (_sp < 0) {
                    _sp = _exp / 10;
                }
                if (itemId == rareProdId) {
                    _exp *= Config.ALT_GAME_CREATION_RARE_XPSP_RATE;
                    _sp *= Config.ALT_GAME_CREATION_RARE_XPSP_RATE;
                }

                if (_exp < 0) {
                    _exp = 0;
                }
                if (_sp < 0) {
                    _sp = 0;
                }

                for (int i = _skillLevel; i > recipeLevel; i--) {
                    _exp /= 4;
                    _sp /= 4;
                }

                // Added multiplication of Creation speed with XP/SP gain slower crafting -> more XP,
                // faster crafting -> less XP you can use ALT_GAME_CREATION_XP_RATE/SP to modify XP/SP gained (default = 1)
                _player.addExpAndSp((int) _player.getStats().getValue(Stat.EXPSP_RATE, _exp * Config.ALT_GAME_CREATION_XP_RATE * Config.ALT_GAME_CREATION_SPEED), (int) _player.getStats().getValue(Stat.EXPSP_RATE, _sp * Config.ALT_GAME_CREATION_SP_RATE * Config.ALT_GAME_CREATION_SPEED));
            }
            updateMakeInfo(true); // success
        }

    }

    public static RecipeController getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final RecipeController INSTANCE = new RecipeController();
    }
}
