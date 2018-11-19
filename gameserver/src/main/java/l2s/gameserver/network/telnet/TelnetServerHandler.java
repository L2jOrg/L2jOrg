package l2s.gameserver.network.telnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import l2s.gameserver.Config;
import l2s.gameserver.network.telnet.commands.TelnetBan;
import l2s.gameserver.network.telnet.commands.TelnetConfig;
import l2s.gameserver.network.telnet.commands.TelnetDebug;
import l2s.gameserver.network.telnet.commands.TelnetPerfomance;
import l2s.gameserver.network.telnet.commands.TelnetSay;
import l2s.gameserver.network.telnet.commands.TelnetServer;
import l2s.gameserver.network.telnet.commands.TelnetStatus;
import l2s.gameserver.network.telnet.commands.TelnetWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelnetServerHandler extends SimpleChannelUpstreamHandler implements TelnetCommandHolder
{
	private static final Logger _log = LoggerFactory.getLogger(TelnetServerHandler.class);

	//The following regex splits a line into its parts, separated by spaces, unless there are quotes, in which case the quotes take precedence.  
	private static final Pattern COMMAND_ARGS_PATTERN = Pattern.compile("\"([^\"]*)\"|([^\\s]+)");

	private Set<TelnetCommand> _commands = new LinkedHashSet<TelnetCommand>();

	public TelnetServerHandler()
	{
		_commands.add(new TelnetCommand("help", "h"){
			@Override
			public String getUsage()
			{
				return "help [command]";
			}

			@Override
			public String handle(String[] args)
			{
				if(args.length == 0)
				{
					StringBuilder sb = new StringBuilder();
					sb.append("Available commands:\n");
					for(TelnetCommand cmd : _commands)
					{
						sb.append(cmd.getCommand()).append("\n");
					}

					return sb.toString();
				}
				else
				{
					TelnetCommand cmd = TelnetServerHandler.this.getCommand(args[0]);
					if(cmd == null)
						return "Unknown command.\n";

					return "usage:\n" + cmd.getUsage() + "\n";
				}
			}
		});

		addHandler(new TelnetBan());
		addHandler(new TelnetConfig());
		addHandler(new TelnetDebug());
		addHandler(new TelnetPerfomance());
		addHandler(new TelnetSay());
		addHandler(new TelnetServer());
		addHandler(new TelnetStatus());
		addHandler(new TelnetWorld());
	}

	public void addHandler(TelnetCommandHolder handler)
	{
		for(TelnetCommand cmd : handler.getCommands())
			_commands.add(cmd);
	}

	@Override
	public Set<TelnetCommand> getCommands()
	{
		return _commands;
	}

	private TelnetCommand getCommand(String command)
	{
		for(TelnetCommand cmd : _commands)
			if(cmd.equals(command))
				return cmd;

		return null;
	}

	private String tryHandleCommand(String command, String[] args)
	{
		TelnetCommand cmd = getCommand(command);

		if(cmd == null)
			return "Unknown command.\n";

		String response = cmd.handle(args);
		if(response == null)
			response = "usage:\n" + cmd.getUsage() + "\n";

		return response;
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		// Send greeting for a new connection.
		e.getChannel().write("Welcome to L2 GameServer telnet console.\n");
		e.getChannel().write("It is " + new Date() + " now.\n");
		if(!Config.TELNET_PASSWORD.isEmpty())
		{
			// Ask password
			e.getChannel().write("Password:");
			ctx.setAttachment(Boolean.FALSE);
		}
		else
		{
			e.getChannel().write("Type 'help' to see all available commands.\n");
			ctx.setAttachment(Boolean.TRUE);
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
	{
		// Cast to a String first.
		// We know it is a String because we put some codec in TelnetPipelineFactory.
		String request = (String) e.getMessage();

		// Generate and write a response.
		String response = null;
		boolean close = false;

		if(Boolean.FALSE.equals(ctx.getAttachment()))
			if(Config.TELNET_PASSWORD.equals(request))
			{
				ctx.setAttachment(Boolean.TRUE);
				request = "";
			}
			else
			{
				response = "Wrong password!\n";
				close = true;
			}

		if(Boolean.TRUE.equals(ctx.getAttachment()))
			if(request.isEmpty())
				response = "Type 'help' to see all available commands.\n";
			else if(request.toLowerCase().equals("exit"))
			{
				response = "Have a good day!\n";
				close = true;
			}
			else
			{
				Matcher m = COMMAND_ARGS_PATTERN.matcher(request);

				m.find();
				String command = m.group();

				List<String> args = new ArrayList<String>();
				String arg;
				while(m.find())
				{
					arg = m.group(1);
					if(arg == null)
						arg = m.group(0);
					args.add(arg);
				}

				response = tryHandleCommand(command, args.toArray(new String[args.size()]));
			}

		// We do not need to write a ChannelBuffer here.
		// We know the encoder inserted at TelnetPipelineFactory will do the conversion.
		ChannelFuture future = e.getChannel().write(response);

		// Close the connection after sending 'Have a good day!'
		// if the client has sent 'exit'.
		if(close)
			future.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	{
		if(e.getCause() instanceof IOException)
			e.getChannel().close();
		else
			_log.error("", e.getCause());
	}
}
