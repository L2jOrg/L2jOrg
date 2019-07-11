package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.items.L2Weapon;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CharInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.taskmanager.DecayTaskManager;

import java.util.concurrent.Future;

public class Decoy extends Creature {
    private final Player _owner;
    private int _totalLifeTime;
    private int _timeRemaining;
    private Future<?> _DecoyLifeTask;
    private Future<?> _HateSpam;

    public Decoy(L2NpcTemplate template, Player owner, int totalLifeTime) {
        super(template);
        setInstanceType(InstanceType.L2DecoyInstance);
        _owner = owner;
        setXYZInvisible(owner.getX(), owner.getY(), owner.getZ());
        setIsInvul(false);
        _totalLifeTime = totalLifeTime;
        _timeRemaining = _totalLifeTime;
        final int hateSpamSkillId = 5272;
        final int skilllevel = Math.min(getTemplate().getDisplayId() - 13070, SkillData.getInstance().getMaxLevel(hateSpamSkillId));
        _DecoyLifeTask = ThreadPoolManager.scheduleAtFixedRate(new DecoyLifetime(_owner, this), 1000, 1000);
        _HateSpam = ThreadPoolManager.scheduleAtFixedRate(new HateSpam(this, SkillData.getInstance().getSkill(hateSpamSkillId, skilllevel)), 2000, 5000);
    }

    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        if (_HateSpam != null) {
            _HateSpam.cancel(true);
            _HateSpam = null;
        }
        _totalLifeTime = 0;
        DecayTaskManager.getInstance().add(this);
        return true;
    }

    public void unSummon(Player owner) {
        if (_DecoyLifeTask != null) {
            _DecoyLifeTask.cancel(true);
            _DecoyLifeTask = null;
        }
        if (_HateSpam != null) {
            _HateSpam.cancel(true);
            _HateSpam = null;
        }

        if (isSpawned() && !isDead()) {
            ZoneManager.getInstance().getRegion(this).removeFromZones(this);
            decayMe();
        }
    }

    public void decTimeRemaining(int value) {
        _timeRemaining -= value;
    }

    public int getTimeRemaining() {
        return _timeRemaining;
    }

    public int getTotalLifeTime() {
        return _totalLifeTime;
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        sendPacket(new CharInfo(this, false));
    }

    @Override
    public void updateAbnormalVisualEffects() {
        L2World.getInstance().forEachVisibleObject(this, Player.class, player ->
        {
            if (isVisibleFor(player)) {
                player.sendPacket(new CharInfo(this, isInvisible() && player.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS)));
            }
        });
    }

    public void stopDecay() {
        DecayTaskManager.getInstance().cancel(this);
    }

    @Override
    public void onDecay() {
        deleteMe(_owner);
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return _owner.isAutoAttackable(attacker);
    }

    @Override
    public Item getActiveWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getActiveWeaponItem() {
        return null;
    }

    @Override
    public Item getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public final int getId() {
        return getTemplate().getId();
    }

    @Override
    public int getLevel() {
        return getTemplate().getLevel();
    }

    public void deleteMe(Player owner) {
        decayMe();
    }

    public final Player getOwner() {
        return _owner;
    }

    @Override
    public Player getActingPlayer() {
        return _owner;
    }

    @Override
    public L2NpcTemplate getTemplate() {
        return (L2NpcTemplate) super.getTemplate();
    }

    @Override
    public void sendInfo(Player activeChar) {
        activeChar.sendPacket(new CharInfo(this, isInvisible() && activeChar.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS)));
    }

    @Override
    public void sendPacket(ServerPacket... packets) {
        if (_owner != null) {
            _owner.sendPacket(packets);
        }
    }

    @Override
    public void sendPacket(SystemMessageId id) {
        if (_owner != null) {
            _owner.sendPacket(id);
        }
    }

    static class DecoyLifetime implements Runnable {
        private final Player _activeChar;

        private final Decoy _Decoy;

        DecoyLifetime(Player activeChar, Decoy Decoy) {
            _activeChar = activeChar;
            _Decoy = Decoy;
        }

        @Override
        public void run() {
            try {
                _Decoy.decTimeRemaining(1000);
                final double newTimeRemaining = _Decoy.getTimeRemaining();
                if (newTimeRemaining < 0) {
                    _Decoy.unSummon(_activeChar);
                }
            } catch (Exception e) {
                LOGGER.error("Decoy Error: ", e);
            }
        }
    }

    private static class HateSpam implements Runnable {
        private final Decoy _activeChar;
        private final Skill _skill;

        HateSpam(Decoy activeChar, Skill Hate) {
            _activeChar = activeChar;
            _skill = Hate;
        }

        @Override
        public void run() {
            try {
                _activeChar.setTarget(_activeChar);
                _activeChar.doCast(_skill);
            } catch (Throwable e) {
                LOGGER.error("Decoy Error: ", e);
            }
        }
    }
}
