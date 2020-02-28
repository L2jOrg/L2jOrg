package org.l2j.gameserver.model.items;

import org.l2j.gameserver.model.ExtractableProduct;
import org.l2j.gameserver.model.items.type.ActionType;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.model.items.type.EtcItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is dedicated to the management of EtcItem.
 *
 * @author JoeAlisson
 */
public final class EtcItem extends ItemTemplate {
    private String handler;
    private EtcItemType type;
    private List<ExtractableProduct> _extractableItems;
    private int _extractableCountMin;
    private int _extractableCountMax;
    private boolean isInfinite;
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
                case CommonItem.ADENA, CommonItem.ANCIENT_ADENA, CommonItem.RUSTY_COIN, CommonItem.SILVER_COIN -> ItemTemplate.TYPE2_MONEY;
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
        return _extractableItems;
    }

    /**
     * @return the minimum count of extractable items
     */
    public int getExtractableCountMin() {
        return _extractableCountMin;
    }

    /**
     * @return the maximum count of extractable items
     */
    public int getExtractableCountMax() {
        return _extractableCountMax;
    }

    /**
     * @return true if item is infinite
     */
    public boolean isInfinite() {
        return isInfinite;
    }

    public void addCapsuledItem(ExtractableProduct extractableProduct) {
        if (_extractableItems == null) {
            _extractableItems = new ArrayList<>();
        }
        _extractableItems.add(extractableProduct);
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
        allowSelfResurrection = selfResurrection;
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
}
