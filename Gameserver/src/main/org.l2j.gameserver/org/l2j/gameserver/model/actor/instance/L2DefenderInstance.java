package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.instancemanager.FortManager;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

public class L2DefenderInstance extends L2Attackable {
    private Castle _castle = null; // the castle which the instance should defend
    private Fort _fort = null; // the fortress which the instance should defend

    public L2DefenderInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2DefenderInstance);
    }

    @Override
    public void addDamage(L2Character attacker, int damage, Skill skill) {
        super.addDamage(attacker, damage, skill);
        L2World.getInstance().forEachVisibleObjectInRange(this, L2DefenderInstance.class, 500, defender ->
        {
            defender.addDamageHate(attacker, 0, 10);
        });
    }

    /**
     * Return True if a siege is in progress and the L2Character attacker isn't a Defender.
     *
     * @param attacker The L2Character that the L2SiegeGuardInstance try to attack
     */
    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        // Attackable during siege by all except defenders
        if (!attacker.isPlayable()) {
            return false;
        }

        final L2PcInstance player = attacker.getActingPlayer();

        // Check if siege is in progress
        if (((_fort != null) && _fort.getZone().isActive()) || ((_castle != null) && _castle.getZone().isActive())) {
            final int activeSiegeId = (_fort != null) ? _fort.getResidenceId() : _castle.getResidenceId();

            // Check if player is an enemy of this defender npc
            if ((player != null) && (((player.getSiegeState() == 2) && !player.isRegisteredOnThisSiegeField(activeSiegeId)) || ((player.getSiegeState() == 1)) || (player.getSiegeState() == 0))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    /**
     * This method forces guard to return to home location previously set
     */
    @Override
    public void returnHome() {
        if (getWalkSpeed() <= 0) {
            return;
        }
        if (getSpawn() == null) {
            return;
        }
        if (!isInsideRadius2D(getSpawn(), 40)) {
            setisReturningToSpawnPoint(true);
            clearAggroList();

            if (hasAI()) {
                getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, getSpawn().getLocation());
            }
        }
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        _fort = FortManager.getInstance().getFort(getX(), getY(), getZ());
        _castle = CastleManager.getInstance().getCastle(getX(), getY(), getZ());

        if ((_fort == null) && (_castle == null)) {
            LOGGER.warning("L2DefenderInstance spawned outside of Fortress or Castle zone!" + this);
        }
    }

    /**
     * Custom onAction behaviour. Note that super() is not called because guards need extra check to see if a player should interact or ATTACK them when clicked.
     */
    @Override
    public void onAction(L2PcInstance player, boolean interact) {
        if (!canTarget(player)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Check if the L2PcInstance already target the L2NpcInstance
        if (this != player.getTarget()) {
            // Set the target of the L2PcInstance player
            player.setTarget(this);
        } else if (interact) {
            if (isAutoAttackable(player) && !isAlikeDead()) {
                if (Math.abs(player.getZ() - getZ()) < 600) // this max heigth difference might need some tweaking
                {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
                }
            }
            if (!isAutoAttackable(player)) {
                if (!canInteract(player)) {
                    // Notify the L2PcInstance AI with AI_INTENTION_INTERACT
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
                }
            }
        }
        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public void useMagic(Skill skill) {
        if (!skill.isBad()) {
            L2Character target = this;
            double lowestHpValue = Double.MAX_VALUE;
            for (L2Character nearby : L2World.getInstance().getVisibleObjectsInRange(this, L2Character.class, skill.getCastRange())) {
                if ((nearby == null) || nearby.isDead() || !GeoEngine.getInstance().canSeeTarget(this, nearby)) {
                    continue;
                }
                if (nearby instanceof L2DefenderInstance) {
                    final double targetHp = nearby.getCurrentHp();
                    if (lowestHpValue > targetHp) {
                        target = nearby;
                        lowestHpValue = targetHp;
                    }
                } else if (nearby.isPlayer()) {
                    final L2PcInstance player = (L2PcInstance) nearby;
                    if ((player.getSiegeState() == 2) && !player.isRegisteredOnThisSiegeField(getScriptValue())) {
                        final double targetHp = nearby.getCurrentHp();
                        if (lowestHpValue > targetHp) {
                            target = nearby;
                            lowestHpValue = targetHp;
                        }
                    }
                }
            }
            setTarget(target);
        }
        super.useMagic(skill);
    }

    @Override
    public void addDamageHate(L2Character attacker, int damage, int aggro) {
        if (attacker == null) {
            return;
        }

        if (!(attacker instanceof L2DefenderInstance)) {
            if ((damage == 0) && (aggro <= 1) && (attacker.isPlayable())) {
                final L2PcInstance player = attacker.getActingPlayer();
                // Check if siege is in progress
                if (((_fort != null) && _fort.getZone().isActive()) || ((_castle != null) && _castle.getZone().isActive())) {
                    final int activeSiegeId = (_fort != null) ? _fort.getResidenceId() : _castle.getResidenceId();
                    if ((player != null) && (((player.getSiegeState() == 2) && player.isRegisteredOnThisSiegeField(activeSiegeId)) || ((player.getSiegeState() == 1)))) {
                        return;
                    }
                }
            }
            super.addDamageHate(attacker, damage, aggro);
        }
    }
}
