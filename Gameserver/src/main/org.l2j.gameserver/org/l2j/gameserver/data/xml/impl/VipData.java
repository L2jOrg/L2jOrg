package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.gameserver.data.xml.model.VipInfo;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipInfo;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

public class VipData extends IGameXmlReader{

    private static final byte VIP_MAX_TIER = 7;
    private IntObjectMap<VipInfo> vipTiers = new HashIntObjectMap<>(8);

    private VipData() {
        load();

        var listeners = Containers.Players();
        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) (event) -> {
            final var player = event.getActiveChar();
            if(player.getVipTier() > 0 && !checkVipTierExpiration(player)) {
                player.sendPacket(new ReceiveVipInfo());
            }
        }, this));
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/vip.xsd");
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
        var level = parseByte(attributes, "tier");
        var pointsRequired = parseLong(attributes, "points_required");
        var pointsDepreciated = parseLong(attributes, "points_depreciated");

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

    public byte getVipTier(L2PcInstance player) {
        return getVipInfo(player).getTier();
    }

    public byte getVipTier(long points) {
        return getVipInfo(points).getTier();
    }


    private VipInfo getVipInfo(L2PcInstance player) {
        var points =  player.getVipPoints();
        return getVipInfo(points);
    }

    private VipInfo getVipInfo(long points) {
        for (byte i = 0; i < vipTiers.size(); i++) {
            if(points < vipTiers.get(i).getPointsRequired()) {
                return vipTiers.get(i - 1);
            }
        }
        return vipTiers.get(VIP_MAX_TIER);
    }

    public long getPointsDepreciatedOnLevel(byte vipTier) {
        return vipTiers.get(vipTier).getPointsDepreciated();
    }

    public long getPointsToLevel(int level) {
        if(vipTiers.containsKey(level)) {
            return vipTiers.get(level).getPointsRequired();
        }
        return 0;
    }

    public float getXPAndSPBonus(L2PcInstance player) {
        return getVipInfo(player).getXpSpBonus() - 1;
    }

    public float getItemDropBonus(L2PcInstance player) {
        return getVipInfo(player).getItemDropBonus();
    }

    public float getDeathPenaltyReduction(L2PcInstance player) {
        return getVipInfo(player).getDeathPenaltyReduction();
    }

    public int getWorldChatBonus(L2PcInstance player) {
        return getVipInfo(player).getWorldChatBonus();
    }

    public double getPvPDamageBonus(L2PcInstance player) {
        return getVipInfo(player).getPvPDamageBonus();
    }

    public double getPvEDamageBonus(L2PcInstance player) {
        return getVipInfo(player).getPvEDamageBonus();
    }

    public float getFishingXPBonus(L2PcInstance player) {
        return getVipInfo(player).getPhishingXpBonus();
    }

    public double getCombatAttributesBonus(L2PcInstance player) {
        return getVipInfo(player).getAllCombatAttributeBonus();
    }

    public float getSilverCoinDropChance(L2PcInstance player) {
        return getVipInfo(player).getSilverCoinChance();
    }

    public float getRustyCoinDropChance(L2PcInstance player) {
        return getVipInfo(player).getRustyCoinChance();
    }

    public boolean checkVipTierExpiration(L2PcInstance player) {
        var now = Instant.now();
        if(now.isAfter(Instant.ofEpochMilli(player.getVipTierExpiration()))) {
            player.updateVipPoints(-getPointsDepreciatedOnLevel(player.getVipTier()));
            player.setVipTierExpiration(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());
            return true;
        }
        return false;
    }

    public static VipData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final VipData INSTANCE = new VipData();
    }
}
