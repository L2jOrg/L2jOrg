package l2s.gameserver.instancemanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import l2s.gameserver.Config;
import l2s.gameserver.handler.petition.IPetitionHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.SayPacket2;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.tables.GmListTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Petition Manager
 *
 * @author n0nam3
 * @date 21/08/2010 0:11
 *
 */
public final class PetitionManager implements IPetitionHandler
{
	private static final Logger _log = LoggerFactory.getLogger(PetitionManager.class.getName());

	public static enum PetitionState
	{
		Pending,
		Responder_Cancel,
		Responder_Missing,
		Responder_Reject,
		Responder_Complete,
		Petitioner_Cancel,
		Petitioner_Missing,
		In_Process,
		Completed
	}

	public static enum PetitionType
	{
		Immobility,
		Recovery_Related,
		Bug_Report,
		Quest_Related,
		Bad_User,
		Suggestions,
		Game_Tip,
		Operation_Related,
		Other
	}

	private static final PetitionManager _instance = new PetitionManager();

	public static final PetitionManager getInstance()
	{
		return _instance;
	}

	private AtomicInteger _nextId = new AtomicInteger();
	private Map<Integer, Petition> _pendingPetitions = new ConcurrentHashMap<Integer, Petition>();
	private Map<Integer, Petition> _completedPetitions = new ConcurrentHashMap<Integer, Petition>();

	private class Petition
	{
		private long _submitTime = System.currentTimeMillis();
		private long _endTime = -1;

		private int _id;
		private PetitionType _type;
		private PetitionState _state = PetitionState.Pending;
		private String _content;

		private List<SayPacket2> _messageLog = new ArrayList<SayPacket2>();

		private int _petitioner;
		private int _responder;

		public Petition(Player petitioner, String petitionText, int petitionType)
		{
			_id = getNextId();
			_type = PetitionType.values()[petitionType - 1];
			_content = petitionText;
			_petitioner = petitioner.getObjectId();
		}

		protected boolean addLogMessage(SayPacket2 cs)
		{
			return _messageLog.add(cs);
		}

		protected List<SayPacket2> getLogMessages()
		{
			return _messageLog;
		}

		public boolean endPetitionConsultation(PetitionState endState)
		{
			setState(endState);
			_endTime = System.currentTimeMillis();

			if(getResponder() != null && getResponder().isOnline())
				if(endState == PetitionState.Responder_Reject)
					getPetitioner().sendMessage("Your petition was rejected. Please try again later.");
				else
				{
					// Ending petition consultation with <Player>.
					getResponder().sendPacket(new SystemMessage(SystemMessage.ENDING_PETITION_CONSULTATION_WITH_S1).addString(getPetitioner().getName()));

					if(endState == PetitionState.Petitioner_Cancel)
						// Receipt No. <ID> petition cancelled.
						getResponder().sendPacket(new SystemMessage(SystemMessage.RECEIPT_NO_S1_PETITION_CANCELLED).addNumber(getId()));
				}

			// End petition consultation and inform them, if they are still online.
			if(getPetitioner() != null && getPetitioner().isOnline())
				getPetitioner().sendPacket(new SystemMessage(SystemMessage.ENDING_PETITION_CONSULTATION));

			getCompletedPetitions().put(getId(), this);
			return getPendingPetitions().remove(getId()) != null;
		}

		public String getContent()
		{
			return _content;
		}

		public int getId()
		{
			return _id;
		}

		public Player getPetitioner()
		{
			return World.getPlayer(_petitioner);
		}

		public Player getResponder()
		{
			return World.getPlayer(_responder);
		}

		@SuppressWarnings("unused")
		public long getEndTime()
		{
			return _endTime;
		}

		public long getSubmitTime()
		{
			return _submitTime;
		}

		public PetitionState getState()
		{
			return _state;
		}

		public String getTypeAsString()
		{
			return _type.toString().replace("_", " ");
		}

		public void sendPetitionerPacket(L2GameServerPacket responsePacket)
		{
			if(getPetitioner() == null || !getPetitioner().isOnline())
				//endPetitionConsultation(PetitionState.Petitioner_Missing);
				return;

			getPetitioner().sendPacket(responsePacket);
		}

		public void sendResponderPacket(L2GameServerPacket responsePacket)
		{
			if(getResponder() == null || !getResponder().isOnline())
			{
				endPetitionConsultation(PetitionState.Responder_Missing);
				return;
			}

			getResponder().sendPacket(responsePacket);
		}

		public void setState(PetitionState state)
		{
			_state = state;
		}

		public void setResponder(Player responder)
		{
			if(getResponder() != null)
				return;

			_responder = responder.getObjectId();
		}
	}

	private PetitionManager()
	{
		_log.info("Initializing PetitionManager");
	}

	public int getNextId()
	{
		return _nextId.incrementAndGet();
	}

	public void clearCompletedPetitions()
	{
		int numPetitions = getPendingPetitionCount();

		getCompletedPetitions().clear();
		_log.info("PetitionManager: Completed petition data cleared. " + numPetitions + " petition(s) removed.");
	}

	public void clearPendingPetitions()
	{
		int numPetitions = getPendingPetitionCount();

		getPendingPetitions().clear();
		_log.info("PetitionManager: Pending petition queue cleared. " + numPetitions + " petition(s) removed.");
	}

	public boolean acceptPetition(Player respondingAdmin, int petitionId)
	{
		if(!isValidPetition(petitionId))
			return false;

		Petition currPetition = getPendingPetitions().get(petitionId);

		if(currPetition.getResponder() != null)
			return false;

		currPetition.setResponder(respondingAdmin);
		currPetition.setState(PetitionState.In_Process);

		// Petition application accepted. (Send to Petitioner)
		currPetition.sendPetitionerPacket(new SystemMessage(SystemMessage.PETITION_APPLICATION_ACCEPTED));

		// Petition application accepted. Reciept No. is <ID>
		currPetition.sendResponderPacket(new SystemMessage(SystemMessage.PETITION_APPLICATION_ACCEPTED_RECEIPT_NO_IS_S1).addNumber(currPetition.getId()));

		// Petition consultation with <Player> underway.
		currPetition.sendResponderPacket(new SystemMessage(SystemMessage.PETITION_CONSULTATION_WITH_S1_UNDER_WAY).addString(currPetition.getPetitioner().getName()));
		return true;
	}

	public boolean cancelActivePetition(Player player)
	{
		for(Petition currPetition : getPendingPetitions().values())
		{
			if(currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId())
				return currPetition.endPetitionConsultation(PetitionState.Petitioner_Cancel);

			if(currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId())
				return currPetition.endPetitionConsultation(PetitionState.Responder_Cancel);
		}

		return false;
	}

	public void checkPetitionMessages(Player petitioner)
	{
		if(petitioner != null)
			for(Petition currPetition : getPendingPetitions().values())
			{
				if(currPetition == null)
					continue;

				if(currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == petitioner.getObjectId())
				{
					for(SayPacket2 logMessage : currPetition.getLogMessages())
						petitioner.sendPacket(logMessage);

					return;
				}
			}
	}

	public boolean endActivePetition(Player player)
	{
		if(!player.isGM())
			return false;

		for(Petition currPetition : getPendingPetitions().values())
		{
			if(currPetition == null)
				continue;

			if(currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId())
				return currPetition.endPetitionConsultation(PetitionState.Completed);
		}

		return false;
	}

	protected Map<Integer, Petition> getCompletedPetitions()
	{
		return _completedPetitions;
	}

	protected Map<Integer, Petition> getPendingPetitions()
	{
		return _pendingPetitions;
	}

	public int getPendingPetitionCount()
	{
		return getPendingPetitions().size();
	}

	public int getPlayerTotalPetitionCount(Player player)
	{
		if(player == null)
			return 0;

		int petitionCount = 0;

		for(Petition currPetition : getPendingPetitions().values())
		{
			if(currPetition == null)
				continue;

			if(currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId())
				petitionCount++;
		}

		for(Petition currPetition : getCompletedPetitions().values())
		{
			if(currPetition == null)
				continue;

			if(currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId())
				petitionCount++;
		}

		return petitionCount;
	}

	public boolean isPetitionInProcess()
	{
		for(Petition currPetition : getPendingPetitions().values())
		{
			if(currPetition == null)
				continue;

			if(currPetition.getState() == PetitionState.In_Process)
				return true;
		}

		return false;
	}

	public boolean isPetitionInProcess(int petitionId)
	{
		if(!isValidPetition(petitionId))
			return false;

		Petition currPetition = getPendingPetitions().get(petitionId);
		return currPetition.getState() == PetitionState.In_Process;
	}

	public boolean isPlayerInConsultation(Player player)
	{
		if(player != null)
			for(Petition currPetition : getPendingPetitions().values())
			{
				if(currPetition == null)
					continue;

				if(currPetition.getState() != PetitionState.In_Process)
					continue;

				if(currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId() || currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId())
					return true;
			}

		return false;
	}

	public boolean isPetitioningAllowed()
	{
		return Config.PETITIONING_ALLOWED;
	}

	public boolean isPlayerPetitionPending(Player petitioner)
	{
		if(petitioner != null)
			for(Petition currPetition : getPendingPetitions().values())
			{
				if(currPetition == null)
					continue;

				if(currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == petitioner.getObjectId())
					return true;
			}

		return false;
	}

	private boolean isValidPetition(int petitionId)
	{
		return getPendingPetitions().containsKey(petitionId);
	}

	public boolean rejectPetition(Player respondingAdmin, int petitionId)
	{
		if(!isValidPetition(petitionId))
			return false;

		Petition currPetition = getPendingPetitions().get(petitionId);

		if(currPetition.getResponder() != null)
			return false;

		currPetition.setResponder(respondingAdmin);
		return currPetition.endPetitionConsultation(PetitionState.Responder_Reject);
	}

	public boolean sendActivePetitionMessage(Player player, String messageText)
	{
		//if(!isPlayerInConsultation(player))
		//return false;

		SayPacket2 cs;

		for(Petition currPetition : getPendingPetitions().values())
		{
			if(currPetition == null)
				continue;

			if(currPetition.getPetitioner() != null && currPetition.getPetitioner().getObjectId() == player.getObjectId())
			{
				cs = new SayPacket2(player.getObjectId(), ChatType.PETITION_PLAYER, player.getName(), messageText);
				currPetition.addLogMessage(cs);

				currPetition.sendResponderPacket(cs);
				currPetition.sendPetitionerPacket(cs);
				return true;
			}

			if(currPetition.getResponder() != null && currPetition.getResponder().getObjectId() == player.getObjectId())
			{
				cs = new SayPacket2(player.getObjectId(), ChatType.PETITION_GM, player.getName(), messageText);
				currPetition.addLogMessage(cs);

				currPetition.sendResponderPacket(cs);
				currPetition.sendPetitionerPacket(cs);
				return true;
			}
		}

		return false;
	}

	public void sendPendingPetitionList(Player activeChar)
	{
		final StringBuilder htmlContent = new StringBuilder(600 + getPendingPetitionCount() * 300);
		htmlContent.append("<html><body><center><table width=270><tr>" + "<td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" + "<td width=180><center>Petition Menu</center></td>" + "<td width=45><button value=\"Back\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br>" + "<table width=\"270\">" + "<tr><td><table width=\"270\"><tr><td><button value=\"Reset\" action=\"bypass -h admin_reset_petitions\" width=\"80\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" + "<td align=right><button value=\"Refresh\" action=\"bypass -h admin_view_petitions\" width=\"80\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br></td></tr>");

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		if(getPendingPetitionCount() == 0)
			htmlContent.append("<tr><td>There are no currently pending petitions.</td></tr>");
		else
			htmlContent.append("<tr><td><font color=\"LEVEL\">Current Petitions:</font><br></td></tr>");

		boolean color = true;
		int petcount = 0;
		for(Petition currPetition : getPendingPetitions().values())
		{
			if(currPetition == null)
				continue;

			htmlContent.append("<tr><td width=\"270\"><table width=\"270\" cellpadding=\"2\" bgcolor=").append(color ? "131210" : "444444").append("><tr><td width=\"130\">").append(dateFormat.format(new Date(currPetition.getSubmitTime())));
			htmlContent.append("</td><td width=\"140\" align=right><font color=\"").append(currPetition.getPetitioner().isOnline() ? "00FF00" : "999999").append("\">").append(currPetition.getPetitioner().getName()).append("</font></td></tr>");
			htmlContent.append("<tr><td width=\"130\">");
			if(currPetition.getState() != PetitionState.In_Process)
				htmlContent.append("<table width=\"130\" cellpadding=\"2\"><tr><td><button value=\"View\" action=\"bypass -h admin_view_petition ").append(currPetition.getId()).append("\" width=\"50\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"Reject\" action=\"bypass -h admin_reject_petition ").append(currPetition.getId()).append("\" width=\"50\" height=\"21\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table>");
			else
				htmlContent.append("<font color=\"").append(currPetition.getResponder().isOnline() ? "00FF00" : "999999").append("\">").append(currPetition.getResponder().getName()).append("</font>");
			htmlContent.append("</td>").append(currPetition.getTypeAsString()).append("<td width=\"140\" align=right>").append(currPetition.getTypeAsString()).append("</td></tr></table></td></tr>");
			color = !color;
			petcount++;
			if(petcount > 10)
			{
				htmlContent.append("<tr><td><font color=\"LEVEL\">There is more pending petition...</font><br></td></tr>");
				break;
			}
		}

		htmlContent.append("</table></center></body></html>");

		HtmlMessage htmlMsg = new HtmlMessage(0);
		htmlMsg.setHtml(htmlContent.toString());
		activeChar.sendPacket(htmlMsg);
	}

	public int submitPetition(Player petitioner, String petitionText, int petitionType)
	{
		// Create a new petition instance and add it to the list of pending petitions.
		Petition newPetition = new Petition(petitioner, petitionText, petitionType);
		int newPetitionId = newPetition.getId();
		getPendingPetitions().put(newPetitionId, newPetition);

		// Notify all GMs that a new petition has been submitted.
		String msgContent = petitioner.getName() + " has submitted a new petition."; //(ID: " + newPetitionId + ").";
		GmListTable.broadcastToGMs(new SayPacket2(petitioner.getObjectId(), ChatType.HERO_VOICE, "Petition System", msgContent));

		return newPetitionId;
	}

	public void viewPetition(Player activeChar, int petitionId)
	{
		if(!activeChar.isGM())
			return;

		if(!isValidPetition(petitionId))
			return;

		Petition currPetition = getPendingPetitions().get(petitionId);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		HtmlMessage html = new HtmlMessage(0);
		html.setFile("admin/petition.htm");
		html.replace("%petition%", String.valueOf(currPetition.getId()));
		html.replace("%time%", dateFormat.format(new Date(currPetition.getSubmitTime())));
		html.replace("%type%", currPetition.getTypeAsString());
		html.replace("%petitioner%", currPetition.getPetitioner().getName());
		html.replace("%online%", (currPetition.getPetitioner().isOnline() ? "00FF00" : "999999"));
		html.replace("%text%", currPetition.getContent());

		activeChar.sendPacket(html);
	}

	@Override
	public void handle(Player player, int id, String txt)
	{
		if(GmListTable.getAllVisibleGMs().size() == 0)
		{
			player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_NOT_ANY_GMS_THAT_ARE_PROVIDING_CUSTOMER_SERVICE_CURRENTLY));
			return;
		}

		if(!PetitionManager.getInstance().isPetitioningAllowed())
		{
			player.sendPacket(new SystemMessage(SystemMessage.CANNOT_CONNECT_TO_PETITION_SERVER));
			return;
		}

		if(PetitionManager.getInstance().isPlayerPetitionPending(player))
		{
			player.sendPacket(new SystemMessage(SystemMessage.ALREADY_APPLIED_FOR_PETITION));
			return;
		}

		if(PetitionManager.getInstance().getPendingPetitionCount() == Config.MAX_PETITIONS_PENDING)
		{
			player.sendPacket(new SystemMessage(SystemMessage.THE_PETITION_SYSTEM_IS_CURRENTLY_UNAVAILABLE_PLEASE_TRY_AGAIN_LATER));
			return;
		}

		int totalPetitions = PetitionManager.getInstance().getPlayerTotalPetitionCount(player) + 1;

		if(totalPetitions > Config.MAX_PETITIONS_PER_PLAYER)
		{
			player.sendPacket(new SystemMessage(SystemMessage.WE_HAVE_RECEIVED_S1_PETITIONS_FROM_YOU_TODAY_AND_THAT_IS_THE_MAXIMUM_THAT_YOU_CAN_SUBMIT_IN_ONE_DAY_YOU_CANNOT_SUBMIT_ANY_MORE_PETITIONS));
			return;
		}

		if(txt.length() > 255)
		{
			player.sendPacket(new SystemMessage(SystemMessage.PETITIONS_CANNOT_EXCEED_255_CHARACTERS));
			return;
		}

		if(id >= PetitionManager.PetitionType.values().length)
		{
			_log.warn("PetitionManager: Invalid petition type : " + id);
			return;
		}

		int petitionId = PetitionManager.getInstance().submitPetition(player, txt, id);

		player.sendPacket(new SystemMessage(SystemMessage.PETITION_APPLICATION_ACCEPTED_RECEIPT_NO_IS_S1).addNumber(petitionId));
		player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_SUBMITTED_S1_PETITIONS_YOU_MAY_SUBMIT_S2_MORE_PETITIONS_TODAY).addNumber(totalPetitions).addNumber(Config.MAX_PETITIONS_PER_PLAYER - totalPetitions));
		player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_PETITIONS_PENDING).addNumber(PetitionManager.getInstance().getPendingPetitionCount()));
	}
}
