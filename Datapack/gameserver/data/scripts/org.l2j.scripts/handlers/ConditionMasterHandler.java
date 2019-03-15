package handlers;

import org.l2j.gameserver.handler.ConditionHandler;

import handlers.conditions.CategoryTypeCondition;
import handlers.conditions.NpcLevelCondition;
import handlers.conditions.PlayerLevelCondition;

/**
 * @author Sdw
 */
public class ConditionMasterHandler
{
	public static void main(String[] args) {
		ConditionHandler.getInstance().registerHandler("CategoryType", CategoryTypeCondition::new);
		ConditionHandler.getInstance().registerHandler("NpcLevel", NpcLevelCondition::new);
		ConditionHandler.getInstance().registerHandler("PlayerLevel", PlayerLevelCondition::new);
	}
}
