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
package org.l2j.gameserver.model.actor.instance;

import io.github.joealisson.primitive.HashIntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.database.dao.SummonDAO;
import org.l2j.gameserver.data.database.data.SummonSkillData;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.gameserver.data.sql.impl.SummonEffectsTable;
import org.l2j.gameserver.data.sql.impl.SummonEffectsTable.SummonEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.EffectScope;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SetSummonRemainTime;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.MathUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import static java.util.Objects.*;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author UnAfraid
 */
public class Servitor extends Summon implements Runnable {

    protected Future<?> _summonLifeTask;
    private float _expMultiplier = 0;
    private ItemHolder _itemConsume;
    private int _lifeTime;
    private int _lifeTimeRemaining;
    private int _consumeItemInterval;
    private int _consumeItemIntervalRemaining;
    private int _referenceSkill;

    public Servitor(NpcTemplate template, Player owner) {
        super(template, owner);
        setInstanceType(InstanceType.L2ServitorInstance);
        setShowSummonAnimation(true);
        running = true;
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        if ((_lifeTime > 0) && (_summonLifeTask == null)) {
            _summonLifeTask = ThreadPool.scheduleAtFixedRate(this, 0, 5000);
        }
    }

    @Override
    public final int getLevel() {
        return (getTemplate() != null ? getTemplate().getLevel() : 0);
    }

    @Override
    public int getSummonType() {
        return 1;
    }

    public float getExpMultiplier() {
        return _expMultiplier;
    }

    public void setExpMultiplier(float expMultiplier) {
        _expMultiplier = expMultiplier;
    }

    public void setItemConsume(ItemHolder item) {
        _itemConsume = item;
    }

    public void setItemConsumeInterval(int interval) {
        _consumeItemInterval = interval;
        _consumeItemIntervalRemaining = interval;
    }

    public int getLifeTime() {
        return _lifeTime;
    }

    public void setLifeTime(int lifeTime) {
        _lifeTime = lifeTime;
        _lifeTimeRemaining = lifeTime;
    }

    public int getLifeTimeRemaining() {
        return _lifeTimeRemaining;
    }

    public void setLifeTimeRemaining(int time) {
        _lifeTimeRemaining = time;
    }

    public int getReferenceSkill() {
        return _referenceSkill;
    }

    public void setReferenceSkill(int skillId) {
        _referenceSkill = skillId;
    }

    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }

        if (_summonLifeTask != null) {
            _summonLifeTask.cancel(false);
        }
        return true;

    }

    /**
     * Servitors' skills automatically change their level based on the servitor's level.<br>
     * Until level 70, the servitor gets 1 lv of skill per 10 levels.<br>
     * After that, it is 1 skill level per 5 servitor levels.<br>
     * If the resulting skill level doesn't exist use the max that does exist!
     */
    @Override
    public void doCast(Skill skill) {
        final int petLevel = getLevel();
        int skillLevel = petLevel / 10;
        if (petLevel >= 70) {
            skillLevel += (petLevel - 65) / 10;
        }

        // Adjust the level for servitors less than level 1.
        if (skillLevel < 1) {
            skillLevel = 1;
        }

        final Skill skillToCast = SkillEngine.getInstance().getSkill(skill.getId(), skillLevel);

        super.doCast(requireNonNullElse(skillToCast, skill));
    }

    @Override
    public void setRestoreSummon(boolean val) {
        _restoreSummon = val;
    }

    @Override
    public final boolean stopSkillEffects(boolean removed, int skillId) {
        boolean stopped = super.stopSkillEffects(removed, skillId);
        final var servitorEffects = SummonEffectsTable.getInstance().getServitorEffects(getOwner());
        final Collection<SummonEffect> effects = servitorEffects.get(_referenceSkill);
        if (!isNullOrEmpty(effects)) {
            for (SummonEffect effect : effects) {
                final Skill skill = effect.getSkill();
                if (nonNull(skill) && (skill.getId() == skillId)) {
                    effects.remove(effect);
                    stopped = true;
                }
            }
        }
        return stopped;
    }

    @Override
    public void storeMe() {
        if (_referenceSkill == 0) {
            return;
        }

        if (CharacterSettings.restoreSummonOnReconnect()) {
            if (isDead())
            {
                PlayerSummonTable.getInstance().removeServitor(getOwner(), getObjectId());
            }
            else
            {
                PlayerSummonTable.getInstance().saveSummon(this);
            }
        }
    }

    @Override
    public void storeEffect(boolean storeEffects) {
        if ((getOwner() == null) || getOwner().isInOlympiadMode()) {
            return;
        }

        SummonEffectsTable.getInstance().getServitorEffects(getOwner()).getOrDefault(getReferenceSkill(), Collections.emptyList()).clear();

        getDAO(SummonDAO.class).deleteSkillsSave(getOwner().getObjectId(), _referenceSkill);

        if(storeEffects) {
            int buffIndex = 0;

            final List<SummonSkillData> storedEffects = new ArrayList<>(getEffectList().getEffects().size());
            for (BuffInfo info : getEffectList().getEffects()) {
                final Skill skill = info.getSkill();

                if (skill.isDeleteAbnormalOnLeave() || skill.isToggle() || skill.getAbnormalType() == AbnormalType.LIFE_FORCE_OTHERS) {
                    continue;
                }

                if (skill.isDance() && !CharacterSettings.storeDances()) {
                    continue;
                }

                storedEffects.add(SummonSkillData.of(getOwner().getObjectId(), _referenceSkill, skill.getId(), skill.getLevel(), info.getTime(), ++buffIndex));

                var effects = SummonEffectsTable.getInstance().getServitorEffectsOwner();
                effects.computeIfAbsent(getOwner().getObjectId(), id -> new HashIntMap<>())
                        .computeIfAbsent(getReferenceSkill(), referenceSkill -> ConcurrentHashMap.newKeySet())
                        .add(new SummonEffect(skill, info.getTime()));
            }

            getDAO(SummonDAO.class).save(storedEffects);
        }
    }

    @Override
    public void restoreEffects() {
        if (getOwner().isInOlympiadMode()) {
            return;
        }
        if (!SummonEffectsTable.getInstance().getServitorEffects(getOwner()).containsKey(_referenceSkill)) {
            for (SummonSkillData data : getDAO(SummonDAO.class).findSummonSkills(getOwner().getObjectId(), _referenceSkill)) {
                final var skill = SkillEngine.getInstance().getSkill(data.getSkillId(), data.getSkillLevel());
                if(isNull(skill)) {
                    continue;
                }

                if(skill.hasEffects(EffectScope.GENERAL)) {
                    var effects = SummonEffectsTable.getInstance().getServitorEffectsOwner();
                    effects.computeIfAbsent(getOwner().getObjectId(), id -> new HashIntMap<>())
                            .computeIfAbsent(_referenceSkill, s -> ConcurrentHashMap.newKeySet())
                            .add(new SummonEffect(skill, data.getRemainingTime()));
                }
            }
        }

        getDAO(SummonDAO.class).deleteSkillsSave(getOwner().getObjectId(), _referenceSkill);

        if (SummonEffectsTable.getInstance().getServitorEffects(getOwner()).containsKey(_referenceSkill)) {
            for (SummonEffect se : SummonEffectsTable.getInstance().getServitorEffects(getOwner()).get(_referenceSkill)) {
                if (nonNull(se)) {
                    se.getSkill().applyEffects(this, this, false, se.getEffectCurTime());
                }
            }
        }
    }

    @Override
    public void unSummon(Player owner) {
        if (_summonLifeTask != null) {
            _summonLifeTask.cancel(false);
        }

        super.unSummon(owner);

        if (!_restoreSummon) {
            PlayerSummonTable.getInstance().removeServitor(owner, getObjectId());
        }
    }

    @Override
    public boolean destroyItem(String process, int objectId, long count, WorldObject reference, boolean sendMessage) {
        return getOwner().destroyItem(process, objectId, count, reference, sendMessage);
    }

    @Override
    public boolean destroyItemByItemId(String process, int itemId, long count, WorldObject reference, boolean sendMessage) {
        return getOwner().destroyItemByItemId(process, itemId, count, reference, sendMessage);
    }

    @Override
    public AttributeType getAttackElement() {
        if (getOwner() != null) {
            return getOwner().getAttackElement();
        }
        return super.getAttackElement();
    }

    @Override
    public int getAttackElementValue(AttributeType attackAttribute) {
        if (getOwner() != null) {
            return (getOwner().getAttackElementValue(attackAttribute));
        }
        return super.getAttackElementValue(attackAttribute);
    }

    @Override
    public int getDefenseElementValue(AttributeType defenseAttribute) {
        if (getOwner() != null) {
            return (getOwner().getDefenseElementValue(defenseAttribute));
        }
        return super.getDefenseElementValue(defenseAttribute);
    }

    @Override
    public boolean isServitor() {
        return true;
    }

    @Override
    public void run() {
        final int usedTime = 5000;
        _lifeTimeRemaining -= usedTime;

        if (isDead() || !isSpawned()) {
            if (_summonLifeTask != null) {
                _summonLifeTask.cancel(false);
            }
            return;
        }

        // check if the summon's lifetime has ran out
        if (_lifeTimeRemaining < 0) {
            sendPacket(SystemMessageId.YOUR_SERVITOR_PASSED_AWAY);
            unSummon(getOwner());
            return;
        }

        if (_consumeItemInterval > 0) {
            _consumeItemIntervalRemaining -= usedTime;

            // check if it is time to consume another item
            if ((_consumeItemIntervalRemaining <= 0) && (_itemConsume.getCount() > 0) && (_itemConsume.getId() > 0) && !isDead()) {
                if (destroyItemByItemId("Consume", _itemConsume.getId(), _itemConsume.getCount(), this, false)) {
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.A_SUMMONED_MONSTER_USES_S1);
                    msg.addItemName(_itemConsume.getId());
                    sendPacket(msg);

                    // Reset
                    _consumeItemIntervalRemaining = _consumeItemInterval;
                } else {
                    sendPacket(SystemMessageId.SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITOR_S_STAY_THE_SERVITOR_HAS_DISAPPEARED);
                    unSummon(getOwner());
                }
            }
        }

        sendPacket(new SetSummonRemainTime(_lifeTime, _lifeTimeRemaining));

        // Using same task to check if owner is in visible range
        if (!MathUtil.isInsideRadius3D(this, getOwner(),  2000)) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getOwner());
        }
    }

    @Override
    public void doPickupItem(WorldObject object) {
    }
}
