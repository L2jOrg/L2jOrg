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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.network.serverpackets.StopMove;
import org.l2j.gameserver.util.MathUtil;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

// While a tamed beast behaves a lot like a pet (ingame) and does have
// an owner, in all other aspects, it acts like a mob.
// In addition, it can be fed in order to increase its duration.
// This class handles the running tasks, AI, and feed of the mob.
// The (mostly optional) AI on feeding the spawn is handled by the datapack ai script
public final class TamedBeast extends FeedableBeast {
    private static final int MAX_DISTANCE_FROM_HOME = 30000;
    private static final int MAX_DISTANCE_FROM_OWNER = 2000;
    private static final int MAX_DURATION = 1200000; // 20 minutes
    private static final int DURATION_CHECK_INTERVAL = 60000; // 1 minute
    private static final int DURATION_INCREASE_INTERVAL = 20000; // 20 secs (gained upon feeding)
    private static final int BUFF_INTERVAL = 5000; // 5 seconds
    protected Player _owner;
    protected boolean _isFreyaBeast;
    private int _foodSkillId;
    private int _remainingTime = MAX_DURATION;
    private int _homeX;
    private int _homeY;
    private int _homeZ;
    private Future<?> _buffTask = null;
    private Future<?> _durationCheckTask = null;
    private Collection<Skill> _beastSkills = null;

    public TamedBeast(int npcTemplateId) {
        super(NpcData.getInstance().getTemplate(npcTemplateId));
        setInstanceType(InstanceType.L2TamedBeastInstance);
        setHome(this);
    }

    public TamedBeast(int npcTemplateId, Player owner, int foodSkillId, int x, int y, int z) {
        super(NpcData.getInstance().getTemplate(npcTemplateId));
        _isFreyaBeast = false;
        setInstanceType(InstanceType.L2TamedBeastInstance);
        setCurrentHp(getMaxHp());
        setCurrentMp(getMaxMp());
        setOwner(owner);
        setFoodType(foodSkillId);
        setHome(x, y, z);
        spawnMe(x, y, z);
    }

    public TamedBeast(int npcTemplateId, Player owner, int food, int x, int y, int z, boolean isFreyaBeast) {
        super(NpcData.getInstance().getTemplate(npcTemplateId));
        _isFreyaBeast = isFreyaBeast;
        setInstanceType(InstanceType.L2TamedBeastInstance);
        setCurrentHp(getMaxHp());
        setCurrentMp(getMaxMp());
        setFoodType(food);
        setHome(x, y, z);
        spawnMe(x, y, z);
        setOwner(owner);
        if (isFreyaBeast) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, _owner);
        }
    }

    public void onReceiveFood() {
        // Eating food extends the duration by 20secs, to a max of 20minutes
        _remainingTime += DURATION_INCREASE_INTERVAL;
        if (_remainingTime > MAX_DURATION) {
            _remainingTime = MAX_DURATION;
        }
    }

    public Location getHome() {
        return new Location(_homeX, _homeY, _homeZ);
    }

    public void setHome(Creature c) {
        setHome(c.getX(), c.getY(), c.getZ());
    }

    public void setHome(int x, int y, int z) {
        _homeX = x;
        _homeY = y;
        _homeZ = z;
    }

    public int getRemainingTime() {
        return _remainingTime;
    }

    public void setRemainingTime(int duration) {
        _remainingTime = duration;
    }

    public int getFoodType() {
        return _foodSkillId;
    }

    public void setFoodType(int foodItemId) {
        if (foodItemId > 0) {
            _foodSkillId = foodItemId;

            // start the duration checks
            // start the buff tasks
            if (_durationCheckTask != null) {
                _durationCheckTask.cancel(true);
            }
            _durationCheckTask = ThreadPool.scheduleAtFixedRate(new CheckDuration(this), DURATION_CHECK_INTERVAL, DURATION_CHECK_INTERVAL);
        }
    }

    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }

        getAI().stopFollow();
        if (_buffTask != null) {
            _buffTask.cancel(true);
        }
        if (_durationCheckTask != null) {
            _durationCheckTask.cancel(true);
        }

        // clean up variables
        if ((_owner != null) && (_owner.getTrainedBeasts() != null)) {
            _owner.getTrainedBeasts().remove(this);
        }
        _buffTask = null;
        _durationCheckTask = null;
        _owner = null;
        _foodSkillId = 0;
        _remainingTime = 0;
        return true;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return !_isFreyaBeast;
    }

    public boolean isFreyaBeast() {
        return _isFreyaBeast;
    }

    public void addBeastSkill(Skill skill) {
        if (_beastSkills == null) {
            _beastSkills = ConcurrentHashMap.newKeySet();
        }
        _beastSkills.add(skill);
    }

    public void castBeastSkills() {
        if ((_owner == null) || (_beastSkills == null)) {
            return;
        }
        int delay = 100;
        for (Skill skill : _beastSkills) {
            ThreadPool.schedule(new buffCast(skill), delay);
            delay += (100 + skill.getHitTime());
        }
        ThreadPool.schedule(new buffCast(null), delay);
    }

    public Player getOwner() {
        return _owner;
    }

    public void setOwner(Player owner) {
        if (owner != null) {
            _owner = owner;
            setTitle(owner.getName());
            // broadcast the new title
            setShowSummonAnimation(true);
            broadcastPacket(new NpcInfo(this));

            owner.addTrainedBeast(this);

            // always and automatically follow the owner.
            getAI().startFollow(_owner, 100);

            if (!_isFreyaBeast) {
                // instead of calculating this value each time, let's get this now and pass it on
                int totalBuffsAvailable = 0;
                for (Skill skill : getTemplate().getSkills().values()) {
                    // if the skill is a buff, check if the owner has it already [ owner.getEffect(L2Skill skill) ]
                    if (skill.isContinuous() && !skill.isDebuff()) {
                        totalBuffsAvailable++;
                    }
                }

                // start the buff tasks
                if (_buffTask != null) {
                    _buffTask.cancel(true);
                }
                _buffTask = ThreadPool.scheduleAtFixedRate(new CheckOwnerBuffs(this, totalBuffsAvailable), BUFF_INTERVAL, BUFF_INTERVAL);
            }
        } else {
            deleteMe(); // despawn if no owner
        }
    }

    public boolean isTooFarFromHome() {
        return !MathUtil.isInsideRadius3D(this, _homeX, _homeY, _homeZ, MAX_DISTANCE_FROM_HOME);
    }

    @Override
    public boolean deleteMe() {
        if (_buffTask != null) {
            _buffTask.cancel(true);
        }
        _durationCheckTask.cancel(true);
        stopHpMpRegeneration();

        // clean up variables
        if ((_owner != null) && (_owner.getTrainedBeasts() != null)) {
            _owner.getTrainedBeasts().remove(this);
        }
        setTarget(null);
        _buffTask = null;
        _durationCheckTask = null;
        _owner = null;
        _foodSkillId = 0;
        _remainingTime = 0;

        // remove the spawn
        return super.deleteMe();
    }

    // notification triggered by the owner when the owner is attacked.
    // tamed mobs will heal/recharge or debuff the enemy according to their skills
    public void onOwnerGotAttacked(Creature attacker) {
        // check if the owner is no longer around...if so, despawn
        if ((_owner == null) || !_owner.isOnline()) {
            deleteMe();
            return;
        }
        // if the owner is too far away, stop anything else and immediately run towards the owner.
        if (!MathUtil.isInsideRadius3D(_owner, this, MAX_DISTANCE_FROM_OWNER)) {
            getAI().startFollow(_owner);
            return;
        }
        // if the owner is dead, do nothing...
        if (_owner.isDead() || _isFreyaBeast) {
            return;
        }

        // if the tamed beast is currently in the middle of casting, let it complete its skill...
        if (isCastingNow(SkillCaster::isAnyNormalType)) {
            return;
        }

        final float HPRatio = ((float) _owner.getCurrentHp()) / _owner.getMaxHp();

        // if the owner has a lot of HP, then debuff the enemy with a random debuff among the available skills
        // use of more than one debuff at this moment is acceptable
        if (HPRatio >= 0.8) {
            for (Skill skill : getTemplate().getSkills().values()) {
                // if the skill is a debuff, check if the attacker has it already [ attacker.getEffect(L2Skill skill) ]
                if (skill.isDebuff() && (Rnd.get(3) < 1) && ((attacker != null) && attacker.isAffectedBySkill(skill.getId()))) {
                    sitCastAndFollow(skill, attacker);
                }
            }
        }
        // for HP levels between 80% and 50%, do not react to attack events (so that MP can regenerate a bit)
        // for lower HP ranges, heal or recharge the owner with 1 skill use per attack.
        else if (HPRatio < 0.5) {
            int chance = 1;
            if (HPRatio < 0.25) {
                chance = 2;
            }

            // if the owner has a lot of HP, then debuff the enemy with a random debuff among the available skills
            for (Skill skill : getTemplate().getSkills().values()) {
                // if the skill is a buff, check if the owner has it already [ owner.getEffect(L2Skill skill) ]
                if ((Rnd.get(5) < chance) && skill.hasAnyEffectType(EffectType.CPHEAL, EffectType.HEAL, EffectType.MANAHEAL_BY_LEVEL, EffectType.MANAHEAL_PERCENT)) {
                    sitCastAndFollow(skill, _owner);
                }
            }
        }
    }

    /**
     * Prepare and cast a skill:<br>
     * First smoothly prepare the beast for casting, by abandoning other actions.<br>
     * Next, call super.doCast(skill) in order to actually cast the spell.<br>
     * Finally, return to auto-following the owner.
     *
     * @param skill
     * @param target
     */
    protected void sitCastAndFollow(Skill skill, Creature target) {
        stopMove(null);
        broadcastPacket(new StopMove(this));
        getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);

        setTarget(target);
        doCast(skill);
        getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, _owner);
    }

    @Override
    public void onAction(Player player, boolean interact) {
        if ((player == null) || !canTarget(player)) {
            return;
        }

        // Check if the Player already target the Folk
        if (this != player.getTarget()) {
            // Set the target of the Player player
            player.setTarget(this);
        } else if (interact) {
            if (isAutoAttackable(player) && (Math.abs(player.getZ() - getZ()) < 100)) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
            } else {
                // Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
                player.sendPacket(ActionFailed.STATIC_PACKET);
            }
        }
    }

    private static class CheckDuration implements Runnable {
        private final TamedBeast _tamedBeast;

        CheckDuration(TamedBeast tamedBeast) {
            _tamedBeast = tamedBeast;
        }

        @Override
        public void run() {
            final int foodTypeSkillId = _tamedBeast.getFoodType();
            final Player owner = _tamedBeast.getOwner();

            Item item = null;
            if (_tamedBeast._isFreyaBeast) {
                item = owner.getInventory().getItemByItemId(foodTypeSkillId);
                if ((item != null) && (item.getCount() >= 1)) {
                    owner.destroyItem("BeastMob", item, 1, _tamedBeast, true);
                    _tamedBeast.broadcastPacket(new SocialAction(_tamedBeast.getObjectId(), 3));
                } else {
                    _tamedBeast.deleteMe();
                }
            } else {
                _tamedBeast.setRemainingTime(_tamedBeast.getRemainingTime() - DURATION_CHECK_INTERVAL);
                // I tried to avoid this as much as possible...but it seems I can't avoid hardcoding
                // ids further, except by carrying an additional variable just for these two lines...
                // Find which food item needs to be consumed.
                if (foodTypeSkillId == 2188) {
                    item = owner.getInventory().getItemByItemId(6643);
                } else if (foodTypeSkillId == 2189) {
                    item = owner.getInventory().getItemByItemId(6644);
                }

                // if the owner has enough food, call the item handler (use the food and triffer all necessary actions)
                if ((item != null) && (item.getCount() >= 1)) {
                    final WorldObject oldTarget = owner.getTarget();
                    owner.setTarget(_tamedBeast);

                    // emulate a call to the owner using food, but bypass all checks for range, etc
                    // this also causes a call to the AI tasks handling feeding, which may call onReceiveFood as required.
                    SkillCaster.triggerCast(owner, _tamedBeast, SkillEngine.getInstance().getSkill(foodTypeSkillId, 1));
                    owner.setTarget(oldTarget);
                } else {
                    // if the owner has no food, the beast immediately despawns, except when it was only
                    // newly spawned. Newly spawned beasts can last up to 5 minutes
                    if (_tamedBeast.getRemainingTime() < (MAX_DURATION - 300000)) {
                        _tamedBeast.setRemainingTime(-1);
                    }
                }
                // There are too many conflicting reports about whether distance from home should be taken into consideration. Disabled for now.
                // if (_tamedBeast.isTooFarFromHome())
                // _tamedBeast.setRemainingTime(-1);

                if (_tamedBeast.getRemainingTime() <= 0) {
                    _tamedBeast.deleteMe();
                }
            }
        }
    }

    private class buffCast implements Runnable {
        private final Skill _skill;

        public buffCast(Skill skill) {
            _skill = skill;
        }

        @Override
        public void run() {
            if (_skill == null) {
                getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, _owner);
            } else {
                sitCastAndFollow(_skill, _owner);
            }
        }
    }

    private class CheckOwnerBuffs implements Runnable {
        private final TamedBeast _tamedBeast;
        private final int _numBuffs;

        CheckOwnerBuffs(TamedBeast tamedBeast, int numBuffs) {
            _tamedBeast = tamedBeast;
            _numBuffs = numBuffs;
        }

        @Override
        public void run() {
            final Player owner = _tamedBeast.getOwner();

            // check if the owner is no longer around...if so, despawn
            if ((owner == null) || !owner.isOnline()) {
                deleteMe();
                return;
            }
            // if the owner is too far away, stop anything else and immediately run towards the owner.
            if (!MathUtil.isInsideRadius3D(TamedBeast.this, owner, MAX_DISTANCE_FROM_OWNER)) {
                getAI().startFollow(owner);
                return;
            }
            // if the owner is dead, do nothing...
            if (owner.isDead()) {
                return;
            }
            // if the tamed beast is currently casting a spell, do not interfere (do not attempt to cast anything new yet).
            if (isCastingNow(SkillCaster::isAnyNormalType)) {
                return;
            }

            int totalBuffsOnOwner = 0;
            int i = 0;
            final int rand = Rnd.get(_numBuffs);
            Skill buffToGive = null;

            // get this npc's skills: getSkills()
            for (Skill skill : _tamedBeast.getTemplate().getSkills().values()) {
                // if the skill is a buff, check if the owner has it already [ owner.getEffect(L2Skill skill) ]
                if (skill.isContinuous() && !skill.isDebuff()) {
                    if (i++ == rand) {
                        buffToGive = skill;
                    }
                    if (owner.isAffectedBySkill(skill.getId())) {
                        totalBuffsOnOwner++;
                    }
                }
            }
            // if the owner has less than 60% of this beast's available buff, cast a random buff
            if (((_numBuffs * 2) / 3) > totalBuffsOnOwner) {
                _tamedBeast.sitCastAndFollow(buffToGive, owner);
            }
            getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, _tamedBeast.getOwner());
        }
    }
}
