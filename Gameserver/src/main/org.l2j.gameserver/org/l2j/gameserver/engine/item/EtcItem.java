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
package org.l2j.gameserver.engine.item;

import org.l2j.gameserver.model.ExtractableProduct;
import org.l2j.gameserver.model.item.AutoUseType;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.model.item.type.EtcItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is dedicated to the management of EtcItem.
 *
 * @author JoeAlisson
 */
public final class EtcItem extends ItemTemplate {
    private String handler;
    private final EtcItemType type;
    private List<ExtractableProduct> extractableItems;
    private int maxExtractable;
    private boolean isInfinite;
    private boolean selfResurrection;
    private AutoUseType autoUseType;

    public EtcItem(int id, String name, EtcItemType type) {
        super(id, name);
        this.type = type;
        type1 = ItemTemplate.TYPE1_ITEM_QUESTITEM_ADENA;
    }

    public void fillType2() {
        if (isQuestItem()) {
            type2 = ItemTemplate.TYPE2_QUEST;
        } else {
            type2 = switch (getId()) {
                case CommonItem.ADENA, CommonItem.GOLD_COIN, CommonItem.SILVER_COIN, CommonItem.L2_COIN -> ItemTemplate.TYPE2_MONEY;
                default -> ItemTemplate.TYPE2_OTHER;
            };
        }
    }

    /**
     * @return the type of Etc Item.
     */
    @Override
    public EtcItemType getItemType() {
        return type;
    }

    /**
     * @return the ID of the Etc item after applying the mask.
     */
    @Override
    public int getItemMask() {
        return type.mask();
    }

    /**
     * @return the handler name, null if no handler for item.
     */
    public String getHandlerName() {
        return handler;
    }

    /**
     * @return the extractable items list.
     */
    public List<ExtractableProduct> getExtractableItems() {
        return extractableItems;
    }

    /**
     * @return the maximum count of extractable items
     */
    public int getMaxExtractable() {
        return maxExtractable;
    }

    /**
     * @return true if item is infinite
     */
    public boolean isInfinite() {
        return isInfinite;
    }

    public void addCapsuledItem(ExtractableProduct extractableProduct) {
        if (extractableItems == null) {
            extractableItems = new ArrayList<>();
        }
        extractableItems.add(extractableProduct);
    }

    public void setImmediateEffect(boolean immediateEffect) {
        this.immediateEffect = immediateEffect;
    }

    public void setExImmediateEffect(boolean exImmediateEffect) {
        this.exImmediateEffect = exImmediateEffect;
    }

    public void setQuestItem(boolean questItem) {
        this.questItem = questItem;
    }

    public void setInfinite(boolean infinite) {
        this.isInfinite = infinite;
    }

    public void setSelfResurrection(boolean selfResurrection) {
        this.selfResurrection = selfResurrection;
    }

    public boolean isSelfResurrection() {
        return selfResurrection;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public void setAction(ActionType action) {
        _defaultAction = action;
    }

    public void setAutoUseType(AutoUseType autoUseType) {
        this.autoUseType = autoUseType;
    }

    public boolean isAutoPotion() {
        return autoUseType == AutoUseType.HEALING;
    }

    public boolean isAutoSupply() {
        return autoUseType == AutoUseType.SUPPLY;
    }

    public void setExtractableMax(int maxExtractable) {
        this.maxExtractable = maxExtractable;
    }
}
