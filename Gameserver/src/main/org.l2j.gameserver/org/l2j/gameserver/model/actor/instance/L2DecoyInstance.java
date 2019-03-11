package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.items.L2Weapon;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CharInfo;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2j.gameserver.taskmanager.DecayTaskManager;

import java.util.concurrent.Future;
import java.util.logging.Level;

public class L2DecoyInstance extends L2Character {
    private final L2PcInstance _owner;
    private int _totalLifeTime;
    private int _timeRemaining;
    private Future<?> _DecoyLifeTask;
    private Future<?> _HateSpam;

    public L2DecoyInstance(L2NpcTemplate template, L2PcInstance owner, int totalLifeTime) {
        super(template);
        setInstanceType(InstanceType.L2DecoyInstance);
        _owner = owner;
        setXYZInvisible(owner.getX(), owner.getY(), owner.getZ());
        setIsInvul(false);
        _totalLifeTime = totalLifeTime;
        _timeRemaining = _totalLifeTime;
        final int skilllevel = getTemplate().getDisplayId() - 13070;
        _DecoyLifeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new DecoyLifetime(_owner, this), 1000, 1000);
        _HateSpam = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HateSpam(this, SkillData.getInstance().getSkill(5272, skilllevel)), 2000, 5000);
    }

    @Override
    public boolean doDie(L2Character killer) {
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

    public void unSummon(L2PcInstance owner) {
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
        L2World.getInstance().forEachVisibleObject(this, L2PcInstance.class, player ->
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
    public boolean isAutoAttackable(L2Character attacker) {
        return _owner.isAutoAttackable(attacker);
    }

    @Override
    public L2ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public L2Weapon getActiveWeaponItem() {
        return null;
    }

    @Override
    public L2ItemInstance getSecondaryWeaponInstance() {
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

    public void deleteMe(L2PcInstance owner) {
        decayMe();
    }

    public final L2PcInstance getOwner() {
        return _owner;
    }

    @Override
    public L2PcInstance getActingPlayer() {
        return _owner;
    }

    @Override
    public L2NpcTemplate getTemplate() {
        return (L2NpcTemplate) super.getTemplate();
    }

    @Override
    public void sendInfo(L2PcInstance activeChar) {
        activeChar.sendPacket(new CharInfo(this, isInvisible() && activeChar.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS)));
    }

    @Override
    public void sendPacket(IClientOutgoingPacket... packets) {
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
        private final L2PcInstance _activeChar;

        private final L2DecoyInstance _Decoy;

        DecoyLifetime(L2PcInstance activeChar, L2DecoyInstance Decoy) {
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
                LOGGER.log(Level.SEVERE, "Decoy Error: ", e);
            }
        }
    }

    private static class HateSpam implements Runnable {
        private final L2DecoyInstance _activeChar;
        private final Skill _skill;

        HateSpam(L2DecoyInstance activeChar, Skill Hate) {
            _activeChar = activeChar;
            _skill = Hate;
        }

        @Override
        public void run() {
            try {
                _activeChar.setTarget(_activeChar);
                _activeChar.doCast(_skill);
            } catch (Throwable e) {
                LOGGER.log(Level.SEVERE, "Decoy Error: ", e);
            }
        }
    }
}
