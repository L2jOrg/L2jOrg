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

		handlers.registerHandler("OpEnergyMax", OpEnergyMaxSkillCondition::new);
		handlers.registerHandler("OpEncumbered", OpEncumberedSkillCondition::new);
		handlers.registerHandler("OpExistNpc", OpExistNpcSkillCondition::new);
		handlers.registerHandler("OpHaveSummon", OpHaveSummonSkillCondition::new);
		handlers.registerHandler("OpHome", OpHomeSkillCondition::new);
		handlers.registerHandler("OpMainjob", OpMainjobSkillCondition::new);
		handlers.registerHandler("OpNeedAgathion", OpNeedAgathionSkillCondition::new);
		handlers.registerHandler("OpNotCursed", OpNotCursedSkillCondition::new);
		handlers.registerHandler("OpPkcount", OpPkcountSkillCondition::new);
		handlers.registerHandler("OpPledge", OpPledgeSkillCondition::new);
		handlers.registerHandler("OpResurrection", OpResurrectionSkillCondition::new);
		handlers.registerHandler("OpSkillAcquire", OpSkillAcquireSkillCondition::new);
		handlers.registerHandler("OpSoulMax", OpSoulMaxSkillCondition::new);
		handlers.registerHandler("OpSweeper", OpSweeperSkillCondition::new);
		handlers.registerHandler("OpTargetDoor", OpTargetDoorSkillCondition::new);
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
