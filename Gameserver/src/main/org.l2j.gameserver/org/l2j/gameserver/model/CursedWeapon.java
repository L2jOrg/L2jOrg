package org.l2j.gameserver.model;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.Party.MessageType;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;


public class CursedWeapon implements INamable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CursedWeapon.class);

    // _name is the name of the cursed weapon associated with its ID.
    private final String _name;
    // _itemId is the Item ID of the cursed weapon.
    private final int _itemId;
    // _skillId is the skills ID.
    private final int _skillId;
    private final int _skillMaxLevel;
    protected Player _player = null;
    protected int transformationId = 0;
    long _endTime = 0;
    private int _dropRate;
    private int _duration;
    private int _durationLost;
    private int _disapearChance;
    private int _stageKills;
    // this should be false unless if the cursed weapon is dropped, in that case it would be true.
    private boolean _isDropped = false;
    // this sets the cursed weapon status to true only if a player has the cursed weapon, otherwise this should be false.
    private boolean _isActivated = false;
    private ScheduledFuture<?> _removeTask;
    private int _nbKills = 0;
    private int _playerId = 0;
    private Item _item = null;
    private int _playerReputation = 0;
    private int _playerPkKills = 0;

    public CursedWeapon(int itemId, int skillId, String name) {
        _name = name;
        _itemId = itemId;
        _skillId = skillId;
        _skillMaxLevel = SkillEngine.getInstance().getMaxLevel(_skillId);
    }

    public void endOfLife() {
        if (_isActivated) {
            if ((_player != null) && _player.isOnline()) {
                // Remove from player
                LOGGER.info(_name + " being removed online.");

                _player.abortAttack();

                _player.setReputation(_playerReputation);
                _player.setPkKills(_playerPkKills);
                _player.setCursedWeaponEquippedId(0);
                removeSkill();

                // Remove
                _player.getInventory().unEquipItemInBodySlot(BodyPart.TWO_HAND);
                _player.storeMe();

                // Destroy
                final Item removedItem = _player.getInventory().destroyItemByItemId("", _itemId, 1, _player, null);
                if (!Config.FORCE_INVENTORY_UPDATE) {
                    final InventoryUpdate iu = new InventoryUpdate();
                    if (removedItem.getCount() == 0) {
                        iu.addRemovedItem(removedItem);
                    } else {
                        iu.addModifiedItem(removedItem);
                    }

                    _player.sendInventoryUpdate(iu);
                } else {
                    _player.sendItemList();
                }

                _player.broadcastUserInfo();
            } else {
                // Remove from Db
                LOGGER.info(_name + " being removed offline.");

                try (Connection con = DatabaseFactory.getInstance().getConnection();
                     PreparedStatement del = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?");
                     PreparedStatement ps = con.prepareStatement("UPDATE characters SET reputation=?, pkkills=? WHERE charId=?")) {
                    // Delete the item
                    del.setInt(1, _playerId);
                    del.setInt(2, _itemId);
                    if (del.executeUpdate() != 1) {
                        LOGGER.warn("Error while deleting itemId " + _itemId + " from userId " + _playerId);
                    }

                    // Restore the reputation
                    ps.setInt(1, _playerReputation);
                    ps.setInt(2, _playerPkKills);
                    ps.setInt(3, _playerId);
                    if (ps.executeUpdate() != 1) {
                        LOGGER.warn("Error while updating karma & pkkills for userId " + _playerId);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Could not delete : " + e.getMessage(), e);
                }
            }
        } else {
            // either this cursed weapon is in the inventory of someone who has another cursed weapon equipped,
            // OR this cursed weapon is on the ground.
            if ((_player != null) && (_player.getInventory().getItemByItemId(_itemId) != null)) {
                // Destroy
                final Item removedItem = _player.getInventory().destroyItemByItemId("", _itemId, 1, _player, null);
                if (!Config.FORCE_INVENTORY_UPDATE) {
                    final InventoryUpdate iu = new InventoryUpdate();
                    if (removedItem.getCount() == 0) {
                        iu.addRemovedItem(removedItem);
                    } else {
                        iu.addModifiedItem(removedItem);
                    }

                    _player.sendInventoryUpdate(iu);
                } else {
                    _player.sendItemList();
                }

                _player.broadcastUserInfo();
            }
            // is dropped on the ground
            else if (_item != null) {
                _item.decayMe();
                LOGGER.info(_name + " item has been removed from World.");
            }
        }

        // Delete infos from table if any
        CursedWeaponsManager.removeFromDb(_itemId);

        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_DISAPPEARED);
        sm.addItemName(_itemId);
        CursedWeaponsManager.announce(sm);

        // Reset state
        cancelTask();
        _isActivated = false;
        _isDropped = false;
        _endTime = 0;
        _player = null;
        _playerId = 0;
        _playerReputation = 0;
        _playerPkKills = 0;
        _item = null;
        _nbKills = 0;
    }

    private void cancelTask() {
        if (_removeTask != null) {
            _removeTask.cancel(true);
            _removeTask = null;
        }
    }

    private void dropIt(Attackable attackable, Player player) {
        dropIt(attackable, player, null, true);
    }

    private void dropIt(Attackable attackable, Player player, Creature killer, boolean fromMonster) {
        _isActivated = false;

        if (fromMonster) {
            _item = attackable.dropItem(player, _itemId, 1);
            _item.setDropTime(0); // Prevent item from being removed by ItemsAutoDestroy

            // RedSky and Earthquake
            final ExRedSky packet = new ExRedSky(10);
            final Earthquake eq = new Earthquake(player.getX(), player.getY(), player.getZ(), 14, 3);
            Broadcast.toAllOnlinePlayers(packet);
            Broadcast.toAllOnlinePlayers(eq);
        } else {
            _item = _player.getInventory().getItemByItemId(_itemId);
            _player.dropItem("DieDrop", _item, killer, true);
            _player.setReputation(_playerReputation);
            _player.setPkKills(_playerPkKills);
            _player.setCursedWeaponEquippedId(0);
            removeSkill();
            _player.abortAttack();
            // Item item = _player.getInventory().getItemByItemId(_itemId);
            // _player.getInventory().dropItem("DieDrop", item, _player, null);
            // _player.getInventory().getItemByItemId(_itemId).dropMe(_player, _player.getX(), _player.getY(), _player.getZ());
        }
        _isDropped = true;
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HAS_DROPPED_IN_S1_THE_QUANTITY_OF_ADENA_STACKED_IN_THE_S2_TREASURE_CHEST_IS_S3_IN_FIXED_QUANTITY_AND_S4_IN_VARIABLE_QUANTITY_THE_OWNER_AT_23_59_WILL_FINALLY_OWN_THIS_ADENA);
        if (player != null) {
            sm.addZoneName(player.getX(), player.getY(), player.getZ()); // Region Name
        } else if (_player != null) {
            sm.addZoneName(_player.getX(), _player.getY(), _player.getZ()); // Region Name
        } else {
            sm.addZoneName(killer.getX(), killer.getY(), killer.getZ()); // Region Name
        }
        sm.addItemName(_itemId);
        sm.addInt(0); // TODO adena fix count
        sm.addInt(0); // adena variable count
        CursedWeaponsManager.announce(sm); // in the Hot Spring region
    }

    public void cursedOnLogin() {
        doTransform();
        giveSkill();

        final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.S2_S_OWNER_HAS_LOGGED_INTO_S1_THE_QUANTITY_OF_ADENA_STACKED_IN_THE_S2_TREASURE_CHEST_IS_S3_IN_FIXED_QUANTITY_AND_S4_IN_VARIABLE_QUANTITY_THE_OWNER_AT_23_59_WILL_FINALLY_OWN_THIS_ADENA);
        msg.addZoneName(_player.getX(), _player.getY(), _player.getZ());
        msg.addItemName(_player.getCursedWeaponEquippedId());
        msg.addInt(0); // adena fix count
        msg.addInt(0); // adena variable count
        CursedWeaponsManager.announce(msg);

        final CursedWeapon cw = CursedWeaponsManager.getInstance().getCursedWeapon(_player.getCursedWeaponEquippedId());
        final SystemMessage msg2 = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_S2_MINUTE_S_OF_USAGE_TIME_REMAINING);
        final int timeLeft = (int) (cw.getTimeLeft() / 60000);
        msg2.addItemName(_player.getCursedWeaponEquippedId());
        msg2.addInt(timeLeft);
        _player.sendPacket(msg2);
    }

    /**
     * Yesod:<br>
     * Rebind the passive skill belonging to the CursedWeapon. Invoke this method if the weapon owner switches to a subclass.
     */
    public void giveSkill() {
        int level = 1 + (_nbKills / _stageKills);
        if (level > _skillMaxLevel) {
            level = _skillMaxLevel;
        }

        final Skill skill = SkillEngine.getInstance().getSkill(_skillId, level);
        _player.addSkill(skill, false);

        // Void Burst, Void Flow
        _player.addTransformSkill(CommonSkill.VOID_BURST.getSkill());
        _player.addTransformSkill(CommonSkill.VOID_FLOW.getSkill());
        _player.sendSkillList();
    }

    public void doTransform() {
        if (_itemId == 8689) {
            transformationId = 302;
        } else if (_itemId == 8190) {
            transformationId = 301;
        }

        if (_player.isTransformed()) {
            _player.stopTransformation(true);

            ThreadPool.schedule(() -> _player.transform(transformationId, true), 500);
        } else {
            _player.transform(transformationId, true);
        }
    }

    public void removeSkill() {
        _player.removeSkill(_skillId);
        _player.untransform();
        _player.sendSkillList();
    }

    public void reActivate() {
        _isActivated = true;
        if ((_endTime - System.currentTimeMillis()) <= 0) {
            endOfLife();
        } else {
            _removeTask = ThreadPool.scheduleAtFixedRate(new RemoveTask(), _durationLost * 12000, _durationLost * 12000);
        }

    }

    public boolean checkDrop(Attackable attackable, Player player) {
        if (Rnd.get(100000) < _dropRate) {
            // Drop the item
            dropIt(attackable, player);

            // Start the Life Task
            _endTime = System.currentTimeMillis() + (_duration * 60000);
            _removeTask = ThreadPool.scheduleAtFixedRate(new RemoveTask(), _durationLost * 12000, _durationLost * 12000);

            return true;
        }

        return false;
    }

    public void activate(Player player, Item item) {
        // If the player is mounted, attempt to unmount first.
        // Only allow picking up the cursed weapon if unmounting is successful.
        if (player.isMounted() && !player.dismount()) {
            // TODO: Verify the following system message, may still be custom.
            player.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
            player.dropItem("InvDrop", item, null, true);
            return;
        }

        _isActivated = true;

        // Player holding it data
        _player = player;
        _playerId = _player.getObjectId();
        _playerReputation = _player.getReputation();
        _playerPkKills = _player.getPkKills();
        saveData();

        // Change player stats
        _player.setCursedWeaponEquippedId(_itemId);
        _player.setReputation(-9999999);
        _player.setPkKills(0);
        if (_player.isInParty()) {
            _player.getParty().removePartyMember(_player, MessageType.EXPELLED);
        }

        // Disable All Skills
        // Do Transform
        doTransform();
        // Add skill
        giveSkill();

        // Equip with the weapon
        _item = item;
        // Item[] items =
        _player.getInventory().equipItem(_item);
        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
        sm.addItemName(_item);
        _player.sendPacket(sm);

        // Fully heal player
        _player.setCurrentHpMp(_player.getMaxHp(), _player.getMaxMp());
        _player.setCurrentCp(_player.getMaxCp());

        // Refresh inventory
        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate iu = new InventoryUpdate();
            iu.addItem(_item);
            _player.sendInventoryUpdate(iu);
        } else {
            _player.sendItemList();
        }

        // Refresh player stats
        _player.broadcastUserInfo();

        final SocialAction atk = new SocialAction(_player.getObjectId(), 17);

        _player.broadcastPacket(atk);

        sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S_OWNER_HAS_APPEARED_IN_S1_THE_QUANTITY_OF_ADENA_STACKED_IN_THE_S2_TREASURE_CHEST_IS_S3_IN_FIXED_QUANTITY_AND_S4_IN_VARIABLE_QUANTITY_THE_OWNER_AT_23_59_WILL_FINALLY_OWN_THIS_ADENA);
        sm.addZoneName(_player.getX(), _player.getY(), _player.getZ()); // Region Name
        sm.addItemName(_item);
        sm.addInt(0); // TODO adena count
        sm.addInt(0); // adena variable count
        CursedWeaponsManager.announce(sm);
    }

    public void saveData() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement del = con.prepareStatement("DELETE FROM cursed_weapons WHERE itemId = ?");
             PreparedStatement ps = con.prepareStatement("INSERT INTO cursed_weapons (itemId, charId, playerReputation, playerPkKills, nbKills, endTime) VALUES (?, ?, ?, ?, ?, ?)")) {
            // Delete previous datas
            del.setInt(1, _itemId);
            del.executeUpdate();

            if (_isActivated) {
                ps.setInt(1, _itemId);
                ps.setInt(2, _playerId);
                ps.setInt(3, _playerReputation);
                ps.setInt(4, _playerPkKills);
                ps.setInt(5, _nbKills);
                ps.setLong(6, _endTime);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("CursedWeapon: Failed to save data.", e);
        }
    }

    public void dropIt(Creature killer) {
        if (Rnd.get(100) <= _disapearChance) {
            // Remove it
            endOfLife();
        } else {
            // Unequip & Drop
            dropIt(null, null, killer, false);
            // Reset player stats
            _player.setReputation(_playerReputation);
            _player.setPkKills(_playerPkKills);
            _player.setCursedWeaponEquippedId(0);
            removeSkill();

            _player.abortAttack();

            _player.broadcastUserInfo();
        }
    }

    public void increaseKills() {
        _nbKills++;

        if ((_player != null) && _player.isOnline()) {
            _player.setPkKills(_nbKills);
            _player.sendPacket(new UserInfo(_player));

            if (((_nbKills % _stageKills) == 0) && (_nbKills <= (_stageKills * (_skillMaxLevel - 1)))) {
                giveSkill();
            }
        }
        // Reduce time-to-live
        _endTime -= _durationLost * 60000;
        saveData();
    }

    public void setDisapearChance(int disapearChance) {
        _disapearChance = disapearChance;
    }

    public void setDropRate(int dropRate) {
        _dropRate = dropRate;
    }

    public void setDurationLost(int durationLost) {
        _durationLost = durationLost;
    }

    public void setItem(Item item) {
        _item = item;
    }

    public boolean isActivated() {
        return _isActivated;
    }

    public void setActivated(boolean isActivated) {
        _isActivated = isActivated;
    }

    public boolean isDropped() {
        return _isDropped;
    }

    public void setDropped(boolean isDropped) {
        _isDropped = isDropped;
    }

    public long getEndTime() {
        return _endTime;
    }

    public void setEndTime(long endTime) {
        _endTime = endTime;
    }

    @Override
    public String getName() {
        return _name;
    }

    public int getItemId() {
        return _itemId;
    }

    public int getSkillId() {
        return _skillId;
    }

    public int getPlayerId() {
        return _playerId;
    }

    public void setPlayerId(int playerId) {
        _playerId = playerId;
    }

    public Player getPlayer() {
        return _player;
    }

    public void setPlayer(Player player) {
        _player = player;
    }

    public int getPlayerReputation() {
        return _playerReputation;
    }

    public void setPlayerReputation(int playerReputation) {
        _playerReputation = playerReputation;
    }

    public int getPlayerPkKills() {
        return _playerPkKills;
    }

    public void setPlayerPkKills(int playerPkKills) {
        _playerPkKills = playerPkKills;
    }

    public int getNbKills() {
        return _nbKills;
    }

    public void setNbKills(int nbKills) {
        _nbKills = nbKills;
    }

    public int getStageKills() {
        return _stageKills;
    }

    public void setStageKills(int stageKills) {
        _stageKills = stageKills;
    }

    public boolean isActive() {
        return _isActivated || _isDropped;
    }

    public int getLevel() {
        if (_nbKills > (_stageKills * _skillMaxLevel)) {
            return _skillMaxLevel;
        }
        return (_nbKills / _stageKills);
    }

    public long getTimeLeft() {
        return _endTime - System.currentTimeMillis();
    }

    public void goTo(Player player) {
        if (player == null) {
            return;
        }

        if (_isActivated && (_player != null)) {
            // Go to player holding the weapon
            player.teleToLocation(_player.getLocation(), true);
        } else if (_isDropped && (_item != null)) {
            // Go to item on the ground
            player.teleToLocation(_item.getLocation(), true);
        } else {
            player.sendMessage(_name + " isn't in the World.");
        }
    }

    public Location getWorldPosition() {
        if (_isActivated && (_player != null)) {
            return _player.getLocation();
        }

        if (_isDropped && (_item != null)) {
            return _item.getLocation();
        }

        return null;
    }

    public long getDuration() {
        return _duration;
    }

    public void setDuration(int duration) {
        _duration = duration;
    }

    private class RemoveTask implements Runnable {
        protected RemoveTask() {
        }

        @Override
        public void run() {
            if (System.currentTimeMillis() >= _endTime) {
                endOfLife();
            }
        }
    }
}
