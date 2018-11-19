package handler.onshiftaction;

import org.apache.commons.lang3.StringUtils;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.utils.HtmlUtils;

/**
 * @author VISTALL
 * @date 2:51/19.08.2011
 */
public class OnShiftAction_PetInstance extends ScriptOnShiftActionHandler<PetInstance>
{
	@Override
	public Class<PetInstance> getClazz()
	{
		return PetInstance.class;
	}

	@Override
	public boolean call(PetInstance pet, Player player)
	{
		if(!player.getPlayerAccess().CanViewChar)
			return false;

		HtmlMessage msg = new HtmlMessage(0);
		msg.setFile("scripts/actions/admin.L2PetInstance.onActionShift.htm");

		msg.replace("<?npc_name?>", HtmlUtils.htmlNpcName(pet.getNpcId()));
		msg.replace("%title%", String.valueOf(StringUtils.isEmpty(pet.getTitle()) ? "Empty" : pet.getTitle()));
		msg.replace("%level%", String.valueOf(pet.getLevel()));
		msg.replace("%class%", String.valueOf(pet.getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
		msg.replace("%xyz%", pet.getLoc().x + " " + pet.getLoc().y + " " + pet.getLoc().z);
		msg.replace("%heading%", String.valueOf(pet.getLoc().h));

		msg.replace("%owner%", String.valueOf(pet.getPlayer().getName()));
		msg.replace("%ownerId%", String.valueOf(pet.getPlayer().getObjectId()));
		msg.replace("%npcId%", String.valueOf(pet.getNpcId()));
		msg.replace("%controlItemId%", String.valueOf(pet.getControlItem().getItemId()));

		msg.replace("%exp%", String.valueOf(pet.getExp()));
		msg.replace("%sp%", String.valueOf(pet.getSp()));

		msg.replace("%maxHp%", String.valueOf(pet.getMaxHp()));
		msg.replace("%maxMp%", String.valueOf(pet.getMaxMp()));
		msg.replace("%currHp%", String.valueOf((int) pet.getCurrentHp()));
		msg.replace("%currMp%", String.valueOf((int) pet.getCurrentMp()));

		msg.replace("%pDef%", String.valueOf(pet.getPDef(null)));
		msg.replace("%mDef%", String.valueOf(pet.getMDef(null, null)));
		msg.replace("%pAtk%", String.valueOf(pet.getPAtk(null)));
		msg.replace("%mAtk%", String.valueOf(pet.getMAtk(null, null)));
		msg.replace("%paccuracy%", String.valueOf(pet.getPAccuracy()));
		msg.replace("%pevasionRate%", String.valueOf(pet.getPEvasionRate(null)));
		msg.replace("%pcrt%", String.valueOf(pet.getPCriticalHit(null)));
		msg.replace("%maccuracy%", String.valueOf(pet.getMAccuracy()));
		msg.replace("%mevasionRate%", String.valueOf(pet.getMEvasionRate(null)));
		msg.replace("%mcrt%", String.valueOf(pet.getMCriticalHit(null, null)));
		msg.replace("%runSpeed%", String.valueOf(pet.getRunSpeed()));
		msg.replace("%walkSpeed%", String.valueOf(pet.getWalkSpeed()));
		msg.replace("%pAtkSpd%", String.valueOf(pet.getPAtkSpd()));
		msg.replace("%mAtkSpd%", String.valueOf(pet.getMAtkSpd()));
		msg.replace("%dist%", String.valueOf((int) pet.getRealDistance(player)));

		msg.replace("%STR%", String.valueOf(pet.getSTR()));
		msg.replace("%DEX%", String.valueOf(pet.getDEX()));
		msg.replace("%CON%", String.valueOf(pet.getCON()));
		msg.replace("%INT%", String.valueOf(pet.getINT()));
		msg.replace("%WIT%", String.valueOf(pet.getWIT()));
		msg.replace("%MEN%", String.valueOf(pet.getMEN()));

		player.sendPacket(msg);
		return true;
	}

	@Bypass("actions.OnActionShift:servitorEffects")
	public void servitorEffects(Player player, NpcInstance npc, String[] par)
	{
		if(par == null || par.length == 0)
			return;

		Servitor servitor = player.getServitor(Integer.parseInt(par[0]));
		if(servitor == null)
			return;

		StringBuilder dialog = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
		dialog.append(HtmlUtils.htmlNpcName(servitor.getNpcId())).append("<br></font></center><br>");

		for(Abnormal e : servitor.getAbnormalList())
			dialog.append(e.getSkill().getName()).append("<br1>");

		dialog.append("<br><center><button value=\"");
		dialog.append(player.isLangRus() ? "Обновить" : "Refresh");
		dialog.append("\" action=\"bypass -h htmbypass_actions.OnActionShift:servitorEffects " + servitor.getObjectId() + "\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center></body></html>");

		HtmlMessage msg = new HtmlMessage(5);
		msg.setHtml(dialog.toString());
		player.sendPacket(msg);
	}
}
