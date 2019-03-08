package org.l2j.gameserver.mobius.gameserver.model.cubic;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2CubicTemplate;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExUserInfoCubic;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.MagicSkillUse;

import java.util.Comparator;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

/**
 * @author UnAfraid
 */
public class CubicInstance
{
    private final L2PcInstance _owner;
    private final L2PcInstance _caster;
    private final L2CubicTemplate _template;
    private ScheduledFuture<?> _skillUseTask;
    private ScheduledFuture<?> _expireTask;

    public CubicInstance(L2PcInstance owner, L2PcInstance caster, L2CubicTemplate template)
    {
        _owner = owner;
        _caster = caster == null ? owner : caster;
        _template = template;
        activate();
    }

    private void activate()
    {
        _skillUseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(this::readyToUseSkill, 0, _template.getDelay() * 1000);
        _expireTask = ThreadPoolManager.getInstance().schedule(this::deactivate, _template.getDuration() * 1000);
    }

    public void deactivate()
    {
        if ((_skillUseTask != null) && !_skillUseTask.isDone())
        {
            _skillUseTask.cancel(true);
        }
        _skillUseTask = null;

        if ((_expireTask != null) && !_expireTask.isDone())
        {
            _expireTask.cancel(true);
        }
        _expireTask = null;
        _owner.getCubics().remove(_template.getId());
        _owner.sendPacket(new ExUserInfoCubic(_owner));
        _owner.broadcastCharInfo();
    }

    private void readyToUseSkill()
    {
        switch (_template.getTargetType())
        {
            case TARGET:
            {
                actionToCurrentTarget();
                break;
            }
            case BY_SKILL:
            {
                actionToTargetBySkill();
                break;
            }
            case HEAL:
            {
                actionHeal();
                break;
            }
            case MASTER:
            {
                actionToMaster();
                break;
            }
        }
    }

    private CubicSkill chooseSkill()
    {
        final double random = Rnd.nextDouble() * 100;
        double commulativeChance = 0;
        for (CubicSkill cubicSkill : _template.getSkills())
        {
            if ((commulativeChance += cubicSkill.getTriggerRate()) > random)
            {
                return cubicSkill;
            }
        }

        return null;
    }

    private void actionToCurrentTarget()
    {
        final CubicSkill skill = chooseSkill();
        final L2Object target = _owner.getTarget();
        if ((skill != null) && (target != null))
        {
            tryToUseSkill(target, skill);
        }
    }

    private void actionToTargetBySkill()
    {
        final CubicSkill skill = chooseSkill();
        if (skill != null)
        {
            switch (skill.getTargetType())
            {
                case TARGET:
                {
                    final L2Object target = _owner.getTarget();
                    if (target != null)
                    {
                        tryToUseSkill(target, skill);
                    }
                    break;
                }
                case HEAL:
                {
                    actionHeal();
                    break;
                }
                case MASTER:
                {
                    tryToUseSkill(_owner, skill);
                    break;
                }
            }
        }
    }

    private void actionHeal()
    {
        final double random = Rnd.nextDouble() * 100;
        double commulativeChance = 0;
        for (CubicSkill cubicSkill : _template.getSkills())
        {
            if ((commulativeChance += cubicSkill.getTriggerRate()) > random)
            {
                final Skill skill = cubicSkill.getSkill();
                if ((skill != null) && (Rnd.get(100) < cubicSkill.getSuccessRate()))
                {
                    final L2Party party = _owner.getParty();
                    Stream<L2Character> stream;
                    if (party != null)
                    {
                        stream = L2World.getInstance().getVisibleObjectsInRange(_owner, L2Character.class, Config.ALT_PARTY_RANGE, c -> (c.getParty() == party) && _template.validateConditions(this, _owner, c) && cubicSkill.validateConditions(this, _owner, c)).stream();
                    }
                    else
                    {
                        stream = _owner.getServitorsAndPets().stream().filter(summon -> _template.validateConditions(this, _owner, summon) && cubicSkill.validateConditions(this, _owner, summon)).map(L2Character.class::cast);

                    }

                    if (_template.validateConditions(this, _owner, _owner) && cubicSkill.validateConditions(this, _owner, _owner))
                    {
                        stream = Stream.concat(stream, Stream.of(_owner));
                    }

                    final L2Character target = stream.sorted(Comparator.comparingInt(L2Character::getCurrentHpPercent)).findFirst().orElse(null);
                    if (target != null)
                    {
                        activateCubicSkill(skill, target);
                        break;
                    }
                }
            }
        }
    }

    private void actionToMaster()
    {
        final CubicSkill skill = chooseSkill();
        if (skill != null)
        {
            tryToUseSkill(_owner, skill);
        }
    }

    private void tryToUseSkill(L2Object target, CubicSkill cubicSkill)
    {
        final Skill skill = cubicSkill.getSkill();
        if ((_template.getTargetType() != CubicTargetType.MASTER) && !((_template.getTargetType() == CubicTargetType.BY_SKILL) && (cubicSkill.getTargetType() == CubicTargetType.MASTER)))
        {
            target = skill.getTarget(_owner, target, false, false, false);
        }

        if (target != null)
        {
            if (target.isDoor() && !cubicSkill.canUseOnStaticObjects())
            {
                return;
            }

            if (_template.validateConditions(this, _owner, target) && cubicSkill.validateConditions(this, _owner, target))
            {
                if (Rnd.get(100) < cubicSkill.getSuccessRate())
                {
                    activateCubicSkill(skill, target);
                }
            }
        }
    }

    private void activateCubicSkill(Skill skill, L2Object target)
    {
        if (!_owner.hasSkillReuse(skill.getReuseHashCode()))
        {
            _caster.broadcastPacket(new MagicSkillUse(_owner, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), skill.getReuseDelay()));
            skill.activateSkill(this, target);

            _owner.addTimeStamp(skill, skill.getReuseDelay());
        }
    }

    /**
     * @return the {@link L2Character} that owns this cubic
     */
    public L2Character getOwner()
    {
        return _owner;
    }

    /**
     * @return the {@link L2Character} that casted this cubic
     */
    public L2Character getCaster()
    {
        return _caster;
    }

    /**
     * @return {@code true} if cubic is casted from someone else but the owner, {@code false}
     */
    public boolean isGivenByOther()
    {
        return _caster != _owner;
    }

    /**
     * @return the {@link L2CubicTemplate} of this cubic
     */
    public L2CubicTemplate getTemplate()
    {
        return _template;
    }
}
