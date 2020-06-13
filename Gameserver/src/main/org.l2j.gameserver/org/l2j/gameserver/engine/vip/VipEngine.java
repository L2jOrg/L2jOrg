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
package org.l2j.gameserver.engine.vip;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.network.serverpackets.ExBRNewIconCashBtnWnd;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipInfo;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author JoeAlisson
 */
public final class VipEngine extends GameXmlReader {

    private static final byte VIP_MAX_TIER = 10;
    private IntMap<VipInfo> vipTiers = new HashIntMap<>(11);

    private VipEngine() {
        var listeners = Listeners.players();

        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) (event) -> {
            final var player = event.getPlayer();
            if(player.getVipTier() > 0) {
                manageTier(player);
            } else {
                player.sendPacket(new ReceiveVipInfo());
                player.sendPacket(ExBRNewIconCashBtnWnd.NOT_SHOW);
            }
        }, this));
    }

    public void manageTier(Player player) {
        if(!checkVipTierExpiration(player)) {
            player.sendPacket(new ReceiveVipInfo());
        }

        if(player.getVipTier() > 1) {
            var oldSkillId = vipTiers.get(player.getVipTier() - 1).getSkill();
            if(oldSkillId > 0) {
                var oldSkill = SkillEngine.getInstance().getSkill(oldSkillId, 1);
                if(nonNull(oldSkill)) {
                    player.removeSkill(oldSkill);
                }
            }
        }

        var skillId = vipTiers.get(player.getVipTier()).getSkill();
        if(skillId > 0) {
            var skill = SkillEngine.getInstance().getSkill(skillId, 1);
            if(nonNull(skill)) {
                player.addSkill(skill);
            }
        }
        if(PrimeShopData.getInstance().canReceiveVipGift(player)) {
            player.sendPacket(ExBRNewIconCashBtnWnd.SHOW);
        } else {
            player.sendPacket(ExBRNewIconCashBtnWnd.NOT_SHOW);
        }
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/vip.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/vip.xml");
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list",  list -> forEach(list, "vip", this::parseVipTier));
    }

    private void parseVipTier(Node vipNode) {
        var attributes = vipNode.getAttributes();
        var level = parseByte(attributes, "tier");
        var pointsRequired = parseLong(attributes, "points-required");
        var pointsDepreciated = parseLong(attributes, "points-depreciated");

        var vipInfo = new VipInfo(level, pointsRequired, pointsDepreciated);
        vipTiers.put(level, vipInfo);

        var bonusNode = vipNode.getFirstChild();
        if(nonNull(bonusNode)) {
            attributes = bonusNode.getAttributes();
            vipInfo.setSilverCoinChance(parseFloat(attributes, "silver-coin-acquisition"));
            vipInfo.setRustyCoinChance(parseFloat(attributes, "rusty-coin-acquisition"));
            vipInfo.setSkill(parseInteger(attributes, "skill"));
        }
    }

    public byte getVipTier(Player player) {
        return getVipInfo(player).getTier();
    }

    public byte getVipTier(long points) {
        return getVipInfo(points).getTier();
    }

    private VipInfo getVipInfo(Player player) {
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

    public float getSilverCoinDropChance(Player player) {
        return getVipInfo(player).getSilverCoinChance();
    }

    public float getRustyCoinDropChance(Player player) {
        return getVipInfo(player).getRustyCoinChance();
    }

    public boolean checkVipTierExpiration(Player player) {
        var now = Instant.now();
        if(now.isAfter(Instant.ofEpochMilli(player.getVipTierExpiration()))) {
            player.updateVipPoints(-getPointsDepreciatedOnLevel(player.getVipTier()));
            player.setVipTierExpiration(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());
            return true;
        }
        return false;
    }

    public static void init() {
        getInstance().load();
    }

    public static VipEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final VipEngine INSTANCE = new VipEngine();
    }
}