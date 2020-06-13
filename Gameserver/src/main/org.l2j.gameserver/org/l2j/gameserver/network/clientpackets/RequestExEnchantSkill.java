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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author -Wooden-
 */
public final class RequestExEnchantSkill extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestExEnchantSkill.class);
    private static final Logger LOGGER_ENCHANT = LoggerFactory.getLogger("enchant.skills");

    private SkillEnchantType _type;
    private int _skillId;
    private int _skillLvl;
    private int _skillSubLvl;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        final int type = readInt();
        if ((type < 0) || (type >= SkillEnchantType.values().length)) {
            LOGGER.warn("Client: " + client + " send incorrect type " + type + " on packet: " + getClass().getSimpleName());
            throw new InvalidDataPacketException();
        }

        _type = SkillEnchantType.values()[type];
        _skillId = readInt();
        _skillLvl = readShort();
        _skillSubLvl = readShort();
    }

    @Override
    public void runImpl() {
        /*if ((_skillId <= 0) || (_skillLvl <= 0) || (_skillSubLvl < 0)) {
            return;
        }

        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!player.isAllowedToEnchantSkills()) {
            return;
        }

        if (player.isSellingBuffs()) {
            return;
        }

        if (player.isInOlympiadMode()) {
            return;
        }

        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            return;
        }

        Skill skill = player.getKnownSkill(_skillId);
        if (skill == null) {
            return;
        }

        if (!skill.isEnchantable()) {
            return;
        }

        if (skill.getLevel() != _skillLvl) {
            return;
        }

        if (skill.getSubLevel() > 0) {
            if (_type == SkillEnchantType.CHANGE) {
                final int group1 = (_skillSubLvl % 1000);
                final int group2 = (skill.getSubLevel() % 1000);
                if (group1 != group2) {
                    LOGGER.warn(getClass().getSimpleName() + ": Client: " + client + " send incorrect sub level group: " + group1 + " expected: " + group2);
                    return;
                }
            } else if ((skill.getSubLevel() + 1) != _skillSubLvl) {
                LOGGER.warn(getClass().getSimpleName() + ": Client: " + client + " send incorrect sub level: " + _skillSubLvl + " expected: " + skill.getSubLevel() + 1);
                return;
            }
        }

        final EnchantSkillHolder enchantSkillHolder = EnchantSkillGroupsData.getInstance().getEnchantSkillHolder(_skillSubLvl % 1000);

        // Verify if player has all the ingredients
        for (ItemHolder holder : enchantSkillHolder.getRequiredItems(_type)) {
            if (player.getInventory().getInventoryItemCount(holder.getId(), 0) < holder.getCount()) {
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
                return;
            }
        }

        // Consume all ingredients
        for (ItemHolder holder : enchantSkillHolder.getRequiredItems(_type)) {
            if (!player.destroyItemByItemId("Skill enchanting", holder.getId(), holder.getCount(), player, true)) {
                return;
            }
        }

        if (player.getSp() < enchantSkillHolder.getSp(_type)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
            return;
        }

        player.getStats().removeExpAndSp(0, enchantSkillHolder.getSp(_type), false);

        switch (_type) {
            case BLESSED:
            case NORMAL:
            case IMMORTAL: {
                if (Rnd.get(100) <= enchantSkillHolder.getChance(_type)) {
                    final Skill enchantedSkill = SkillEngine.getInstance().getSkill(_skillId, _skillLvl, _skillSubLvl);
                    if (Config.LOG_SKILL_ENCHANTS) {
                        LOGGER.info("Success, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantedSkill.getLevel() + " " + enchantedSkill.getSubLevel() + " - " + enchantedSkill.getName() + " (" + enchantedSkill.getId() + "), " + enchantSkillHolder.getChance(_type));
                    }
                    player.addSkill(enchantedSkill, true);

                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.SKILL_ENCHANT_WAS_SUCCESSFUL_S1_HAS_BEEN_ENCHANTED);
                    sm.addSkillName(_skillId);
                    player.sendPacket(sm);

                    player.sendPacket(ExEnchantSkillResult.STATIC_PACKET_TRUE);
                } else {
                    final int newSubLevel = ((skill.getSubLevel() > 0) && (enchantSkillHolder.getEnchantFailLevel() > 0)) ? ((skill.getSubLevel() - (skill.getSubLevel() % 1000)) + enchantSkillHolder.getEnchantFailLevel()) : 0;
                    final Skill enchantedSkill = SkillEngine.getInstance().getSkill(_skillId, _skillLvl, _type == SkillEnchantType.NORMAL ? newSubLevel : skill.getSubLevel());
                    if (_type == SkillEnchantType.NORMAL) {
                        player.addSkill(enchantedSkill, true);
                        player.sendPacket(SystemMessageId.SKILL_ENCHANT_FAILED_THE_SKILL_WILL_BE_INITIALIZED);
                    } else if (_type == SkillEnchantType.BLESSED) {
                        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_ENCHANT_FAILED_CURRENT_LEVEL_OF_ENCHANT_SKILL_S1_WILL_REMAIN_UNCHANGED).addSkillName(skill));
                    }
                    player.sendPacket(ExEnchantSkillResult.STATIC_PACKET_FALSE);

                    if (Config.LOG_SKILL_ENCHANTS) {
                        LOGGER.info("Failed, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantedSkill.getLevel() + " " + enchantedSkill.getSubLevel() + " - " + enchantedSkill.getName() + " (" + enchantedSkill.getId() + "), " + enchantSkillHolder.getChance(_type));
                    }
                }
                break;
            }
            case CHANGE: {
                if (Rnd.get(100) <= enchantSkillHolder.getChance(_type)) {
                    final Skill enchantedSkill = SkillEngine.getInstance().getSkill(_skillId, _skillLvl, _skillSubLvl);
                    if (Config.LOG_SKILL_ENCHANTS) {
                        LOGGER_ENCHANT.info("Success, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantedSkill.getLevel() + " " + enchantedSkill.getSubLevel() + " - " + enchantedSkill.getName() + " (" + enchantedSkill.getId() + "), " + enchantSkillHolder.getChance(_type));
                    }
                    player.addSkill(enchantedSkill, true);

                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ENCHANT_SKILL_ROUTE_CHANGE_WAS_SUCCESSFUL_LV_OF_ENCHANT_SKILL_S1_WILL_REMAIN);
                    sm.addSkillName(_skillId);
                    player.sendPacket(sm);

                    player.sendPacket(ExEnchantSkillResult.STATIC_PACKET_TRUE);
                } else {
                    final Skill enchantedSkill = SkillEngine.getInstance().getSkill(_skillId, _skillLvl, enchantSkillHolder.getEnchantFailLevel());
                    player.addSkill(enchantedSkill, true);
                    player.sendPacket(SystemMessageId.SKILL_ENCHANT_FAILED_THE_SKILL_WILL_BE_INITIALIZED);
                    player.sendPacket(ExEnchantSkillResult.STATIC_PACKET_FALSE);

                    if (Config.LOG_SKILL_ENCHANTS) {
                        LOGGER.info("Failed, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantedSkill.getLevel() + " " + enchantedSkill.getSubLevel() + " - " + enchantedSkill.getName() + " (" + enchantedSkill.getId() + "), " + enchantSkillHolder.getChance(_type));
                    }
                }
                break;
            }
        }

        player.broadcastUserInfo();
        player.sendSkillList();

        skill = player.getKnownSkill(_skillId);
        player.sendPacket(new ExEnchantSkillInfo(skill.getId(), skill.getLevel(), skill.getSubLevel(), skill.getSubLevel()));
        player.sendPacket(new ExEnchantSkillInfoDetail(_type, skill.getId(), skill.getLevel(), Math.min(skill.getSubLevel() + 1, EnchantSkillGroupsData.MAX_ENCHANT_LEVEL), player));
        player.updateShortCuts(skill.getId(), skill.getLevel(), skill.getSubLevel());*/
    }
}
