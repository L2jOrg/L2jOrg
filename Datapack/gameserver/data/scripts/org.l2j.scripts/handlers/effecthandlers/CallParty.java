package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.util.Objects.isNull;

/**
 * Call Party effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class CallParty extends AbstractEffect {
    private CallParty() {
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        final Party party = effector.getParty();

        if (isNull(party)) {
            return;
        }

        party.getMembers().stream()
                .filter(partyMember -> effector != partyMember && CallPc.checkSummonTargetStatus(partyMember, effector.getActingPlayer()))
                .forEach(partyMember -> partyMember.teleToLocation(effector, true));
    }

    public static class Factory implements SkillEffectFactory {
        private static final CallParty INSTANCE = new CallParty();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "CallParty";
        }
    }
}
