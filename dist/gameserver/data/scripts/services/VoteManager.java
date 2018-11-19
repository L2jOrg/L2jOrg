package services;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;

import l2s.commons.dbutils.DbUtils;
import l2s.commons.lang.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.database.mysql;
import l2s.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2s.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class VoteManager implements IVoicedCommandHandler, OnInitScriptListener
{
	private static class Vote
	{
		public boolean active;
		public String name;
		public int id;
		public int maxPerAccount;
		public TreeMap<Integer, String> variants = new TreeMap<Integer, String>();
		public Map<String, Integer[]> results = new HashMap<String, Integer[]>();
	}

	private static Map<Integer, Vote> VoteList = new HashMap<Integer, Vote>();

	@SuppressWarnings("unchecked")
	private boolean vote(String command, Player activeChar, String args)
	{
		if(args != null && !args.isEmpty()) // применение голоса
		{
			String[] param = args.split(" ");
			if(param.length >= 2 && Util.isNumber(param[0]) && Util.isNumber(param[1]))
			{
				String playerId = activeChar.getAccountName();
				Vote v = VoteList.get(Integer.parseInt(param[0]));
				if(v == null || !v.active)
					return false;
				int var = Integer.parseInt(param[1]);
				Integer[] alreadyResults = v.results.get(playerId);
				if(alreadyResults == null)
				{
					v.results.put(playerId, new Integer[] { var });
					mysql.set("INSERT IGNORE INTO vote (`id`, `HWID`, `vote`) VALUES (?,?,?)", param[0], playerId, param[1]);
				}
				else if(alreadyResults.length < v.maxPerAccount)
				{
					for(int id : alreadyResults)
						if(id == var)
						{
							Functions.show("Error: you have already voted for this entry.", activeChar);
							return false;
						}
					v.results.put(playerId, ArrayUtils.add(alreadyResults, var));
					mysql.set("INSERT IGNORE INTO vote (`id`, `HWID`, `vote`) VALUES (?,?,?)", param[0], playerId, param[1]);
				}
				else
				{
					Functions.show("Error: you have reached votes limit.", activeChar);
					return false;
				}
			}
		}

		int count = 0;
		StringBuilder html = new StringBuilder("!VoteManager:\n<br>");
		String playerId = activeChar.getAccountName();
		for(Entry<Integer, Vote> e : VoteList.entrySet())
			if(e.getValue().active)
			{
				count++;
				html.append(e.getValue().name).append(":<br>");
				Integer[] already = e.getValue().results.get(playerId);
				if(already != null && already.length >= e.getValue().maxPerAccount)
					html.append("You have already voted.<br>");
				else
				{
					Entry<Integer, String>[] variants = new Entry[e.getValue().variants.size()];
					int i = 0;
					for(Entry<Integer, String> variant : e.getValue().variants.entrySet())
					{
						variants[i] = variant;
						i++;
					}
					shuffle(variants);

					variants: for(Entry<Integer, String> variant : variants)
					{
						if(already != null)
							for(Integer et : already)
								if(et.equals(variant.getKey()))
									continue variants;
						html.append("[user_vote " + e.getValue().id + " " + variant.getKey() + "|" + variant.getValue() + "]<br1>");
					}
					html.append("<br>");
				}
			}
		if(count == 0)
			html.append("No active votes now.");
		Functions.show(html.toString(), activeChar);

		return true;
	}

	private static void shuffle(Entry<Integer, String>[] array)
	{
		int j;
		Entry<Integer, String> tmp;
		// i is the number of items remaining to be shuffled.
		for(int i = array.length; i > 1; i--)
		{
			// Pick a random element to swap with the i-th element.
			j = Rnd.get(i); // 0 <= j <= i-1 (0-based array)
			// Swap array elements.
			tmp = array[j];
			array[j] = array[i - 1];
			array[i - 1] = tmp;
		}
	}

	public static void load()
	{
		VoteList.clear();

		// грузим голосования
		try
		{
			File file = new File(Config.DATAPACK_ROOT, "data/vote.xml");
			DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
			factory2.setValidating(false);
			factory2.setIgnoringComments(true);
			Document doc2 = factory2.newDocumentBuilder().parse(file);

			for(Node n2 = doc2.getFirstChild(); n2 != null; n2 = n2.getNextSibling())
				if("list".equalsIgnoreCase(n2.getNodeName()))
					for(Node d2 = n2.getFirstChild(); d2 != null; d2 = d2.getNextSibling())
						if("vote".equalsIgnoreCase(d2.getNodeName()))
						{
							Vote v = new Vote();
							v.id = Integer.parseInt(d2.getAttributes().getNamedItem("id").getNodeValue());
							v.maxPerAccount = Integer.parseInt(d2.getAttributes().getNamedItem("maxPerAccount").getNodeValue());
							v.name = d2.getAttributes().getNamedItem("name").getNodeValue();
							v.active = Boolean.parseBoolean(d2.getAttributes().getNamedItem("active").getNodeValue());

							for(Node i = d2.getFirstChild(); i != null; i = i.getNextSibling())
								if("variant".equalsIgnoreCase(i.getNodeName()))
									v.variants.put(Integer.parseInt(i.getAttributes().getNamedItem("id").getNodeValue()), i.getAttributes().getNamedItem("desc").getNodeValue());

							VoteList.put(v.id, v);
						}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		// грузим голоса
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("SELECT * FROM vote");
			rs = st.executeQuery();
			while(rs.next())
			{
				Vote v = VoteList.get(rs.getInt("id"));
				if(v != null)
				{
					String HWID = rs.getString("HWID");
					Integer[] rez = v.results.get(HWID);
					v.results.put(HWID, ArrayUtils.add(rez, rs.getInt("vote")));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, st, rs);
		}
	}

	private String[] _commandList = new String[] { "vote" };

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		if(command.equalsIgnoreCase("vote"))
			return vote(command, activeChar, args);
		return false;
	}

	@Override
	public void onInit()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		load();
	}
}