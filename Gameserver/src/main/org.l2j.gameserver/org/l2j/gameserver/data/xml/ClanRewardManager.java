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
package org.l2j.gameserver.data.xml;

import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.enums.ClanRewardType.ARENA;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class ClanRewardManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanRewardManager.class);

    private final Map<ClanRewardType, List<ClanRewardBonus>> clanRewards = new EnumMap<>(ClanRewardType.class);
    private int minRaidBonus;

    private ClanRewardManager() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/clan-reward.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/clan-reward.xml");
        clanRewards.forEach((type, rewards) -> LOGGER.info("Loaded {} rewards for {}",  nonNull(rewards) ? rewards.size() : 0, type));
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc.getFirstChild(), XmlReader::isNode, listNode -> {
            switch (listNode.getNodeName()) {
                case "members-online" -> parseMembersOnline(listNode);
                case "hunting-bonus" -> parseHuntingBonus(listNode);
                case "raid-bonus" -> parseRaidBonus(listNode);
            }
        });
    }

    private void parseRaidBonus(Node node) {
        forEach(node, "raid", raidNode -> {
            final var progress = parseInt(raidNode.getAttributes(), "progress");
            if(minRaidBonus == 0 || progress < minRaidBonus) {
                minRaidBonus = progress;
            }
            final var bonus = new ClanRewardBonus(ARENA, progress , progress);

            forEach(raidNode, "skill", skillNode -> {
                final NamedNodeMap skillAttr = skillNode.getAttributes();
                bonus.setSkillReward(new SkillHolder(parseInt(skillAttr, "id"), parseInt(skillAttr, "level")));
            });

            clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList<>()).add(bonus);
        });
    }

    private void parseMembersOnline(Node node) {
        forEach(node, "players", memberNode -> {
            final var attrs = memberNode.getAttributes();
            final var bonus = new ClanRewardBonus(ClanRewardType.MEMBERS_ONLINE, parseInt(attrs, "level"), parseInt(attrs, "size"));

            forEach(memberNode, "skill", skillNode -> {
                final NamedNodeMap skillAttr = skillNode.getAttributes();
                bonus.setSkillReward(new SkillHolder(parseInt(skillAttr, "id"), parseInt(skillAttr, "level")));
            });

            clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList<>()).add(bonus);
        });
    }

    private void parseHuntingBonus(Node node) {
        forEach(node, "hunting", hunting -> {
            final var attrs = hunting.getAttributes();
            final var bonus = new ClanRewardBonus(ClanRewardType.HUNTING_MONSTERS, parseInt(attrs, "level"), parseInt(attrs, "points"));

            forEach(hunting, "item", itemsNode -> {
                final NamedNodeMap itemsAttr = itemsNode.getAttributes();
                bonus.setItemReward(new ItemHolder(parseInt(itemsAttr, "id"), parselong(itemsAttr, "count")));
            });

            clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList<>()).add(bonus);
        });
    }

    public List<ClanRewardBonus> getClanRewardBonuses(ClanRewardType type) {
        return clanRewards.get(type);
    }

    public ClanRewardBonus getHighestReward(ClanRewardType type) {
        return clanRewards.getOrDefault(type, Collections.emptyList()).stream().max(Comparator.comparingInt(ClanRewardBonus::getLevel)).orElse(null);
    }

    public void forEachReward(ClanRewardType type, Consumer<ClanRewardBonus> action) {
        clanRewards.getOrDefault(type, Collections.emptyList()).forEach(action);
    }

    public void checkArenaProgress(Clan clan) {
        final var arenaRewards = clanRewards.get(ARENA);
        final var anyReward = arenaRewards.get(0);
        clan.removeSkill(anyReward.getSkillReward().getSkillId());

        final var progress = clan.getArenaProgress();
        if(progress >= minRaidBonus) {
            final var reward = arenaRewards.stream().filter(r -> r.getLevel() < progress).max(Comparator.comparingInt(ClanRewardBonus::getLevel)).orElse(null);
            if (nonNull(reward)) {
                clan.addNewSkill(reward.getSkillReward().getSkill());
            }
        }
    }

    public static void init() {
        getInstance().load();
    }

    public static ClanRewardManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ClanRewardManager INSTANCE = new ClanRewardManager();
    }
}