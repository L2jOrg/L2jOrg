package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.gameserver.data.xml.model.VipInfo;
import org.l2j.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;

import static java.util.Objects.nonNull;

public class VipData extends IGameXmlReader{

    private IntObjectMap<VipInfo> vipTiers = new HashIntObjectMap<>(8);

    private VipData() {
        load();
    }

    @Override
    public void load() {
        parseDatapackFile("data/vip.xml");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list",  list -> forEach(list, "vip", this::parseVipTier));
    }

    private void parseVipTier(Node vipNode) {
        var attributes = vipNode.getAttributes();
        var level = parseInteger(attributes, "level");
        var pointsRequired = parseInteger(attributes, "points_required");
        var pointsDepreciated = parseInteger(attributes, "points_depreciated");

        var vipInfo = new VipInfo(level, pointsRequired, pointsDepreciated);
        vipTiers.put(level, vipInfo);

        var bonusNode = vipNode.getFirstChild();
        if(nonNull(bonusNode)) {
            attributes = bonusNode.getAttributes();
            vipInfo.setXpSpBonus(parseFloat(attributes, "xp_sp"));
            vipInfo.setItemDropBonus(parseFloat(attributes, "item_drop"));
            vipInfo.setWorldChatBonus(parseInteger(attributes, "world_chat"));
            vipInfo.setDeathPenaltyReduction(parseFloat(attributes, "death_penalty_reduction"));
            vipInfo.setFishingXpBonus(parseFloat(attributes, "fishing_xp"));
            vipInfo.setPvEDamageBonus(parseFloat(attributes, "pve_damage"));
            vipInfo.setPvPDamageBonus(parseFloat(attributes, "pvp_damage"));
            vipInfo.setSilverCoinChance(parseFloat(attributes, "silver_coin_acquisition"));
            vipInfo.setRustyCoinChance(parseFloat(attributes, "rusty_coin_acquisition"));
            vipInfo.setReceiveDailyVIPBox(parseBoolean(attributes, "daily_vip_box"));
            vipInfo.setAllCombatAttributeBonus(parseInteger(attributes, "combat_attribute"));
        }

    }

    public static VipData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final VipData INSTANCE = new VipData();
    }
}
