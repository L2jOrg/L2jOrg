package handlers;

import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.handler.SkillConditionHandler;

import handlers.skillconditionhandlers.*;

import java.util.ServiceLoader;

/**
 * @author NosBit
 */
public class SkillConditionMasterHandler {
    public static void main(String[] args) {
        var handlers = SkillConditionHandler.getInstance();

        ServiceLoader.load(SkillConditionFactory.class).forEach(f -> handlers.registerConditionFactory(f.conditionName(), f::create));

        handlers.registerHandler("OpTargetNpc", OpTargetNpcSkillCondition::new);
        handlers.registerHandler("OpUnlock", OpUnlockSkillCondition::new);
        handlers.registerHandler("OpWyvern", OpWyvernSkillCondition::new);
        handlers.registerHandler("PossessHolything", PossessHolythingSkillCondition::new);
        handlers.registerHandler("RemainCpPer", RemainCpPerSkillCondition::new);
        handlers.registerHandler("RemainHpPer", RemainHpPerSkillCondition::new);
        handlers.registerHandler("RemainMpPer", RemainMpPerSkillCondition::new);
        handlers.registerHandler("SoulSaved", SoulSavedSkillCondition::new);
        handlers.registerHandler("TargetMyParty", TargetMyPartySkillCondition::new);
        handlers.registerHandler("TargetMyPledge", TargetMyPledgeSkillCondition::new);
        handlers.registerHandler("TargetRace", TargetRaceSkillCondition::new);
    }
}
