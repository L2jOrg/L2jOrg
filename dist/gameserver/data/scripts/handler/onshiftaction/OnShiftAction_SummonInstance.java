package handler.onshiftaction;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.SummonInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;

/**
 * @author VISTALL
 * @date 2:51/19.08.2011
 */
public class OnShiftAction_SummonInstance extends ScriptOnShiftActionHandler<SummonInstance>
{
	@Override
	public Class<SummonInstance> getClazz()
	{
		return SummonInstance.class;
	}

	@Override
	public boolean call(SummonInstance summonInstance, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
			return false;

		HtmlMessage msg = new HtmlMessage(0);
		msg.setFile("scripts/actions/admin.L2SummonInstance.onActionShift.htm");

		msg.replace("%name%", String.valueOf(summonInstance.getName()));
		msg.replace("%level%", String.valueOf(summonInstance.getLevel()));
		msg.replace("%class%", String.valueOf(getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
		msg.replace("%xyz%", summonInstance.getLoc().x + " " + summonInstance.getLoc().y + " " + summonInstance.getLoc().z);
		msg.replace("%heading%", String.valueOf(summonInstance.getLoc().h));

		msg.replace("%owner%", String.valueOf(summonInstance.getPlayer().getName()));
		msg.replace("%ownerId%", String.valueOf(summonInstance.getPlayer().getObjectId()));

		msg.replace("%npcId%", String.valueOf(summonInstance.getNpcId()));
		msg.replace("%expPenalty%", String.valueOf(summonInstance.getExpPenalty()));

		msg.replace("%maxHp%", String.valueOf(summonInstance.getMaxHp()));
		msg.replace("%maxMp%", String.valueOf(summonInstance.getMaxMp()));
		msg.replace("%currHp%", String.valueOf((int) summonInstance.getCurrentHp()));
		msg.replace("%currMp%", String.valueOf((int) summonInstance.getCurrentMp()));

		msg.replace("%pDef%", String.valueOf(summonInstance.getPDef(null)));
		msg.replace("%mDef%", String.valueOf(summonInstance.getMDef(null, null)));
		msg.replace("%pAtk%", String.valueOf(summonInstance.getPAtk(null)));
		msg.replace("%mAtk%", String.valueOf(summonInstance.getMAtk(null, null)));
		msg.replace("%paccuracy%", String.valueOf(summonInstance.getPAccuracy()));
		msg.replace("%pevasionRate%", String.valueOf(summonInstance.getPEvasionRate(null)));
		msg.replace("%pcrt%", String.valueOf(summonInstance.getPCriticalHit(null)));
		msg.replace("%maccuracy%", String.valueOf(summonInstance.getMAccuracy()));
		msg.replace("%mevasionRate%", String.valueOf(summonInstance.getMEvasionRate(null)));
		msg.replace("%mcrt%", String.valueOf(summonInstance.getMCriticalHit(null, null)));
		msg.replace("%runSpeed%", String.valueOf(summonInstance.getRunSpeed()));
		msg.replace("%walkSpeed%", String.valueOf(summonInstance.getWalkSpeed()));
		msg.replace("%pAtkSpd%", String.valueOf(summonInstance.getPAtkSpd()));
		msg.replace("%mAtkSpd%", String.valueOf(summonInstance.getMAtkSpd()));
		msg.replace("%dist%", String.valueOf((int) summonInstance.getRealDistance(player)));

		msg.replace("%STR%", String.valueOf(summonInstance.getSTR()));
		msg.replace("%DEX%", String.valueOf(summonInstance.getDEX()));
		msg.replace("%CON%", String.valueOf(summonInstance.getCON()));
		msg.replace("%INT%", String.valueOf(summonInstance.getINT()));
		msg.replace("%WIT%", String.valueOf(summonInstance.getWIT()));
		msg.replace("%MEN%", String.valueOf(summonInstance.getMEN()));

		player.sendPacket(msg);
		return true;
	}
}
