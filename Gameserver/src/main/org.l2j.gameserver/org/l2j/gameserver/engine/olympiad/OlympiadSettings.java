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
package org.l2j.gameserver.engine.olympiad;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.util.GameXmlReader;
import org.w3c.dom.Node;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
class OlympiadSettings {

    Duration matchDuration;
    LocalDate startDate;
    List<ItemHolder> winnerRewards = new ArrayList<>();
    List<ItemHolder> loserRewards = new ArrayList<>();
    List<ItemHolder> tieRewards = new ArrayList<>();
    List<ItemHolder> heroRewards = new ArrayList<>();
    List<Skill> heroSkills = new ArrayList<>();
    int[] availableArenas = Util.INT_ARRAY_EMPTY;

    boolean forceStartDate;
    int minParticipant;
    short initialPoints;
    short maxBattlesPerDay;
    short minBattlePoints;
    short maxBattlePoints;
    int heroReputation;
    short minLevel;
    byte minClassLevel;
    byte minBattlesWonToBeHero;
    byte saveCycleMinBattles;
    boolean enableLegend;
    boolean keepDance;

    private OlympiadSettings() {

    }

    static OlympiadSettings parse(GameXmlReader reader, Node configNode) {
        final var settings = new OlympiadSettings();

        final var olympiadConfig= configNode.getFirstChild();
        if(nonNull(olympiadConfig) && olympiadConfig.getNodeName().equals("olympiad-config")) {
            final var attr = olympiadConfig.getAttributes();

            settings.minParticipant = reader.parseInt(attr, "min-participant");
            settings.forceStartDate = reader.parseBoolean(attr, "force-start-date");

            String strDate = reader.parseString(attr, "start-date");
            settings.startDate = isNull(strDate) ? LocalDate.now() : LocalDate.parse(strDate);

            settings.availableArenas = reader.parseIntArray(attr, "available-arena-instances");
            settings.matchDuration = Duration.ofMinutes(reader.parseInt(attr, "match-duration"));
            settings.initialPoints = reader.parseShort(attr, "initial-points");
            settings.maxBattlesPerDay = reader.parseShort(attr, "max-battles-per-day");
            settings.minLevel = reader.parseShort(attr, "min-level");
            settings.minClassLevel = reader.parseByte(attr, "min-class-level");
            settings.minBattlesWonToBeHero = reader.parseByte(attr, "hero-min-battles-won");
            settings.saveCycleMinBattles = reader.parseByte(attr,"previous-info-min-battles");
            settings.enableLegend = reader.parseBoolean(attr, "enable-legend");
            settings.keepDance = reader.parseBoolean(attr, "keep-dance");

            parseRewards(reader, settings, olympiadConfig);
        }
        return settings;
    }

    private static void parseRewards(GameXmlReader reader, OlympiadSettings settings, Node olympiadConfig) {
        final var rewards = olympiadConfig.getFirstChild();

        final var attr = rewards.getAttributes();
        settings.heroReputation = reader.parseInt(attr, "hero-reputation");
        settings.minBattlePoints = reader.parseShort(attr, "min-olympiad-points");
        settings.maxBattlePoints = reader.parseShort(attr, "max-olympiad-points");

        for(var rewardNode = rewards.getFirstChild(); nonNull(rewardNode); rewardNode = rewardNode.getNextSibling()) {

            switch (rewardNode.getNodeName()) {
                case "winner" -> settings.winnerRewards.add(reader.parseItemHolder(rewardNode));
                case "loser" -> settings.loserRewards.add(reader.parseItemHolder(rewardNode));
                case "tie" -> settings.tieRewards.add(reader.parseItemHolder(rewardNode));
                case "hero" -> settings.heroRewards.add(reader.parseItemHolder(rewardNode));
                case "hero-skills" -> parseSkill(reader, settings, rewardNode);
            }
        }
    }

    private static void parseSkill(GameXmlReader reader, OlympiadSettings settings, Node rewardNode) {
        var skill = reader.parseSkillInfo(rewardNode);
        if(nonNull(skill)) {
            settings.heroSkills.add(skill);
        }
    }
}
