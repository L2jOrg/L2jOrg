package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.items.L2Weapon;
import org.l2j.gameserver.network.serverpackets.ExNewSkillToLearnByLevelUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlayableStat extends CharStat {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PlayableStat.class);

    public PlayableStat(Playable activeChar) {
        super(activeChar);
    }

    public boolean addExp(long value) {
        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnPlayableExpChanged(getActiveChar(), getExp(), getExp() + value), getActiveChar(), TerminateReturn.class);
        if ((term != null) && term.terminate()) {
            return false;
        }

        if (((getExp() + value) < 0) || ((value > 0) && (getExp() == (getExpForLevel(getMaxLevel()) - 1)))) {
            return true;
        }

        if ((getExp() + value) >= getExpForLevel(getMaxLevel())) {
            value = getExpForLevel(getMaxLevel()) - 1 - getExp();
        }

        final int oldLevel = getLevel();
        setExp(getExp() + value);

        byte minimumLevel = 1;
        if (getActiveChar().isPet()) {
            // get minimum level from L2NpcTemplate
            minimumLevel = (byte) PetDataTable.getInstance().getPetMinLevel(((Pet) getActiveChar()).getTemplate().getId());
        }

        byte level = minimumLevel; // minimum level

        for (byte tmp = level; tmp <= getMaxLevel(); tmp++) {
            if (getExp() >= getExpForLevel(tmp)) {
                continue;
            }
            level = --tmp;
            break;
        }

        if ((level != getLevel()) && (level >= minimumLevel)) {
            addLevel((byte) (level - getLevel()));
        }

        if ((getLevel() > oldLevel) && getActiveChar().isPlayer()) {
            final Player activeChar = getActiveChar().getActingPlayer();
            if (SkillTreesData.getInstance().hasAvailableSkills(activeChar, activeChar.getClassId())) {
                getActiveChar().sendPacket(ExNewSkillToLearnByLevelUp.STATIC_PACKET);
            }
        }

        return true;
    }

    public boolean removeExp(long value) {
        if (((getExp() - value) < getExpForLevel(getLevel())) && (!Config.PLAYER_DELEVEL || (Config.PLAYER_DELEVEL && (getLevel() <= Config.DELEVEL_MINIMUM)))) {
            value = getExp() - getExpForLevel(getLevel());
        }

        if ((getExp() - value) < 0) {
            value = getExp() - 1;
        }

        setExp(getExp() - value);

        byte minimumLevel = 1;
        if (getActiveChar().isPet()) {
            // get minimum level from L2NpcTemplate
            minimumLevel = (byte) PetDataTable.getInstance().getPetMinLevel(((Pet) getActiveChar()).getTemplate().getId());
        }
        byte level = minimumLevel;

        for (byte tmp = level; tmp <= getMaxLevel(); tmp++) {
            if (getExp() >= getExpForLevel(tmp)) {
                continue;
            }
            level = --tmp;
            break;
        }
        if ((level != getLevel()) && (level >= minimumLevel)) {
            addLevel((byte) (level - getLevel()));
        }
        return true;
    }

    public boolean removeExpAndSp(long removeExp, long removeSp) {
        boolean expRemoved = false;
        boolean spRemoved = false;
        if (removeExp > 0) {
            expRemoved = removeExp(removeExp);
        }
        if (removeSp > 0) {
            spRemoved = removeSp(removeSp);
        }

        return expRemoved || spRemoved;
    }

    public boolean addLevel(byte value) {
        if ((getLevel() + value) > (getMaxLevel() - 1)) {
            if (getLevel() < (getMaxLevel() - 1)) {
                value = (byte) (getMaxLevel() - 1 - getLevel());
            } else {
                return false;
            }
        }

        final boolean levelIncreased = (getLevel() + value) > getLevel();
        value += getLevel();
        setLevel(value);

        // Sync up exp with current level
        if ((getExp() >= getExpForLevel(getLevel() + 1)) || (getExpForLevel(getLevel()) > getExp())) {
            setExp(getExpForLevel(getLevel()));
        }

        if (!levelIncreased && getActiveChar().isPlayer() && !getActiveChar().isGM() && Config.DECREASE_SKILL_LEVEL) {
            ((Player) getActiveChar()).checkPlayerSkills();
        }

        if (!levelIncreased) {
            return false;
        }

        getActiveChar().getStatus().setCurrentHp(getActiveChar().getStat().getMaxHp());
        getActiveChar().getStatus().setCurrentMp(getActiveChar().getStat().getMaxMp());

        return true;
    }

    public boolean addSp(long value) {
        if (value < 0) {
            LOGGER.warn("wrong usage");
            return false;
        }
        final long currentSp = getSp();
        if (currentSp >= Config.MAX_SP) {
            return false;
        }

        if (currentSp > (Config.MAX_SP - value)) {
            value = Config.MAX_SP - currentSp;
        }

        setSp(currentSp + value);
        return true;
    }

    public boolean removeSp(long value) {
        final long currentSp = getSp();
        if (currentSp < value) {
            value = currentSp;
        }
        setSp(getSp() - value);
        return true;
    }

    public long getExpForLevel(int level) {
        return ExperienceData.getInstance().getExpForLevel(level);
    }

    @Override
    public Playable getActiveChar() {
        return (Playable) super.getActiveChar();
    }

    public int getMaxLevel() {
        return ExperienceData.getInstance().getMaxLevel();
    }

    @Override
    public int getPhysicalAttackRadius() {
        final L2Weapon weapon = getActiveChar().getActiveWeaponItem();
        return weapon != null ? weapon.getBaseAttackRadius() : super.getPhysicalAttackRadius();
    }

    @Override
    public int getPhysicalAttackAngle() {
        final L2Weapon weapon = getActiveChar().getActiveWeaponItem();
        return weapon != null ? weapon.getBaseAttackAngle() : super.getPhysicalAttackAngle();
    }
}
