package ai.areas.TowerOfInsolence.HeavenlyRift;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Folk;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;

import java.util.StringTokenizer;

/**
 * @reworked by Thoss
 */
public class DimensionalVortexInstance extends Folk
{
	private static final long serialVersionUID = 1L;

	private static final int ITEM_ID = 49759;

	public DimensionalVortexInstance(NpcTemplate template)
	{
		super(template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("tryenter"))
		{
			if(player.getInventory().getInventoryItemCount(ITEM_ID, -1) >= 1)
			{
				if(isBusy())
				{
					//TODO: show busy window
					//showBusyWindow(player);
					return;
				}

				if(player.isGM())
				{
					setBusy(true);

					player.destroyItemByItemId("Rift", ITEM_ID, 1, this, false);

					GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 0);
					GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);

					player.teleToLocation(112685, 13362, 10966);

					ThreadPool.schedule(new HeavenlyRift.ClearZoneTask(this), 60000);
					return;
				}
				
				if(!player.isInParty())
				{
					player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
					return;
				}

				Party party = player.getParty();
				if(!party.isLeader(player))
				{
					player.sendPacket(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
					return;
				}
				
				for(Player partyMember : party.getMembers())
				{
					if(!GameUtils.checkIfInRange(1000, player, partyMember, false))
					{
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED);
						sm.addPcName(partyMember);
						party.broadcastToPartyMembers(player, sm);
						return;
					}
				}

				setBusy(true);

				player.destroyItemByItemId("Rift", ITEM_ID, 1, this, false);

				GlobalVariablesManager.getInstance().set("heavenly_rift_complete", 0);
				GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);

				for(Player partyMember : party.getMembers())
					partyMember.teleToLocation(112685, 13362, 10966);

				ThreadPool.schedule(new HeavenlyRift.ClearZoneTask(this), 20 * 60 * 1000);
			}
			else
			{
				showChatWindow(player, "default/" + getId() + "-3.htm");
			}
		}	
		else if(cmd.equals("exchange"))
		{
			long count_have = player.getInventory().getInventoryItemCount(49767, -1);
			if(count_have < 10) //exchange ratio 10:1
			{
				showChatWindow(player, "default/" + getId() + "-2.htm");
				return;	
			}

			if(count_have % 10 != 0) //odd
				count_have -= count_have % 10;

			long to_give = count_have / 10;

			player.destroyItemByItemId("Rift", 49767, count_have, this, true);
			player.addItem("Rift", 49759, to_give, this, true);
		}
		else
			super.onBypassFeedback(player, command);
	}
}