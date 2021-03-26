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
package org.l2j.gameserver.model;

import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.data.database.data.ItemData;
import org.l2j.gameserver.data.database.data.ItemVariationData;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.data.database.data.PlayerVariableData;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;
import static org.l2j.gameserver.enums.InventorySlot.RIGHT_HAND;

/**
 * Used to Store data sent to Client for Player.<br>
 * Selection screen.
 */
public class PlayerSelectInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerSelectInfo.class);

    private final EnumMap<InventorySlot, ItemData> paperdoll = new EnumMap<>(InventorySlot.class);
    private final PlayerVariableData _vars;
    private final PlayerData data;
    private final long banExpireTime;

    private VariationInstance _augmentation;
    private int isBlessed;

    public PlayerSelectInfo(PlayerData data) {
        this.data = data;
        restoreVisibleInventory();
        restoreAugmentation();
        restoreBlessed();

        _vars = getDAO(PlayerVariablesDAO.class).findById(data.getCharId());
        banExpireTime = PunishmentManager.getInstance().getPunishmentExpiration(data.getCharId(), PunishmentAffect.CHARACTER, PunishmentType.BAN);
    }

    private void restoreVisibleInventory() {
        for (ItemData itemData : getDAO(ItemDAO.class).findItemsByOwnerAndLoc(data.getCharId(), ItemLocation.PAPERDOLL)) {
            paperdoll.put(InventorySlot.fromId(itemData.getLocData()), itemData);
        }
    }

    private void restoreAugmentation() {
        var weapon = paperdoll.get(RIGHT_HAND);
        if (nonNull(weapon)) {
            ItemVariationData itemVariation = getDAO(ItemDAO.class).findItemVariationByItem(weapon.getObjectId());
            if(nonNull(itemVariation)) {
                try {
                    int mineralId = itemVariation.getMineralId();
                    int option1 = itemVariation.getOption1();
                    int option2 = itemVariation.getOption2();
                    if ((option1 != -1 ) && (option2 != -1)) {
                        _augmentation = new VariationInstance(weapon.getObjectId(), mineralId, option1, option2);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Could not restore augmentation info", e);
                }
            }
        }
    }
    private void restoreBlessed() {
        var weapon = paperdoll.get(RIGHT_HAND);
        if (nonNull(weapon)) {
            int isBlessedWeapon = getDAO(ItemDAO.class).getIsBlessedWeapon(weapon.getObjectId());
            try {
                isBlessed = isBlessedWeapon;
            } catch (Exception e) {
                LOGGER.warn("Could not restore blessed info", e);
            }
        }
    }

    public int getObjectId() {
        return data.getCharId();
    }

    public int getAccessLevel() {
        return data.getAccessLevel();
    }

    public long getLastAccess() {
        return data.getLastAccess();
    }

    public int getFace() {
        return _vars.getVisualFaceId();
    }

    public int getHairColor() {
        return _vars.getVisualHairColorId();
    }

    public int getHairStyle() {
        return _vars.getVisualHairId();
    }

    public String getName() {
        return data.getName();
    }

    public int getPaperdollItemId(InventorySlot slot) {
        return zeroIfNullOrElse(paperdoll.get(slot), ItemData::getItemId);
    }

    public int getEnchantEffect(InventorySlot slot) {
        return zeroIfNullOrElse(paperdoll.get(slot), ItemData::getEnchantLevel);
    }

    public int getAugmentationOption1() {
        return nonNull(_augmentation) ? _augmentation.getOption1Id() : 0;
    }

    public int getAugmentationOption2() {
        return nonNull(_augmentation) ? _augmentation.getOption2Id() : 0;
    }

    public boolean isHairAccessoryEnabled() {
        return _vars.isHairAccessoryEnabled();
    }

    public int getVitalityItemsUsed() {
        return _vars.getVitalityItemsUsed();
    }

    public PlayerData getData() {
        return data;
    }

    public int getRemainBanExpireTime() {
        var diff = banExpireTime - System.currentTimeMillis();
        return diff > 0 ? (int) (diff / 1000) : 0;
    }

    public void setDeleteTime(long deleteTime) {
        this.data.setDeleteTime(deleteTime);
    }

    public int getIsBlessed() {
        return isBlessed;
    }
}
