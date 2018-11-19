package l2s.gameserver.network.telnet.commands;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.telnet.TelnetCommand;
import l2s.gameserver.network.telnet.TelnetCommandHolder;
import l2s.gameserver.tables.GmListTable;

public class TelnetWorld implements TelnetCommandHolder
{
	private Set<TelnetCommand> _commands = new LinkedHashSet<TelnetCommand>();

	public TelnetWorld()
	{
		_commands.add(new TelnetCommand("find"){

			@Override
			public String getUsage()
			{
				return "find <name>";
			}

			@Override
			public String handle(String[] args)
			{
				if(args.length == 0)
					return null;

				Iterable<Player> players = GameObjectsStorage.getPlayers();
				Iterator<Player> itr = players.iterator();
				StringBuilder sb = new StringBuilder();
				int count = 0;
				Player player;
				Pattern pattern = Pattern.compile(args[0] + "\\S+", Pattern.CASE_INSENSITIVE);
				while(itr.hasNext())
				{
					player = itr.next();

					if(pattern.matcher(player.getName()).matches())
					{
						count++;
						sb.append(player).append("\n");
					}
				}

				if(count == 0)
					sb.append("Player not found.").append("\n");
				else
				{
					sb.append("=================================================\n");
					sb.append("Found: ").append(count).append(" players.").append("\n");
				}

				return sb.toString();
			}

		});
		_commands.add(new TelnetCommand("whois", "who"){

			@Override
			public String getUsage()
			{
				return "whois <name>";
			}

			@Override
			public String handle(String[] args)
			{
				if(args.length == 0)
					return null;

				Player player = GameObjectsStorage.getPlayer(args[0]);
				if(player == null)
					return "Player not found.\n";

				StringBuilder sb = new StringBuilder();

				sb.append("Name: .................... ").append(player.getName()).append("\n");
				sb.append("ID: ...................... ").append(player.getObjectId()).append("\n");
				sb.append("Account Name: ............ ").append(player.getAccountName()).append("\n");
				sb.append("IP: ...................... ").append(player.getIP()).append("\n");
				sb.append("Level: ................... ").append(player.getLevel()).append("\n");
				sb.append("Location: ................ ").append(player.getLoc()).append("\n");
				if(player.getClan() != null)
				{
					sb.append("Clan: .................... ").append(player.getClan().getName()).append("\n");
					if(player.getAlliance() != null)
						sb.append("Ally: .................... ").append(player.getAlliance().getAllyName()).append("\n");
				}
				sb.append("Offline: ................. ").append(player.isInOfflineMode()).append("\n");

				sb.append(player.toString()).append("\n");

				return sb.toString();
			}

		});
		_commands.add(new TelnetCommand("gmlist", "gms"){

			@Override
			public String getUsage()
			{
				return "gmlist";
			}

			@Override
			public String handle(String[] args)
			{
				List<Player> gms = GmListTable.getAllGMs();
				int count = gms.size();

				if(count == 0)
					return "GMs not found.\n";

				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < count; i++)
					sb.append(gms.get(i)).append("\n");

				sb.append("Found: ").append(count).append(" GMs.").append("\n");

				return sb.toString();
			}

		});
	}

	@Override
	public Set<TelnetCommand> getCommands()
	{
		return _commands;
	}
}