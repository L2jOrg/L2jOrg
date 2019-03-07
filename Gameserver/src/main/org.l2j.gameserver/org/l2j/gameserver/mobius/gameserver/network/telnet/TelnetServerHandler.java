/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.telnet;

import com.l2jmobius.Config;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author UnAfraid
 */
@Sharable
public class TelnetServerHandler extends ChannelInboundHandlerAdapter
{
	private static final Pattern COMMAND_ARGS_PATTERN = Pattern.compile("\"([^\"]*)\"|([^\\s]+)");
	private static final AttributeKey<Boolean> AUTHORIZED = AttributeKey.valueOf(TelnetServerHandler.class, "AUTHORIZED");
	
	private String tryHandleCommand(ChannelHandlerContext ctx, String command, String[] args)
	{
		final ITelnetCommand cmd = TelnetServer.getInstance().getCommand(command);
		if (cmd == null)
		{
			return "Unknown command." + Config.EOL;
		}
		
		String response = cmd.handle(ctx, args);
		if (response == null)
		{
			response = "Usage:" + Config.EOL + cmd.getUsage() + Config.EOL;
		}
		
		return response;
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx)
	{
		String ip = ctx.channel().remoteAddress().toString();
		ip = ip.substring(1, ip.lastIndexOf(':')); // Trim out /127.0.0.1:14013
		
		if (!Config.TELNET_HOSTS.contains(ip))
		{
			final ChannelFuture future = ctx.write("Your ip: " + ip + " is not allowed to connect." + Config.EOL);
			future.addListener(ChannelFutureListener.CLOSE);
			ctx.flush();
			return;
		}
		
		// Send greeting for a new connection.
		ctx.write("Welcome to the telnet session." + Config.EOL);
		ctx.write("It is " + new Date() + " now." + Config.EOL);
		ctx.write("Please enter your password:" + Config.EOL);
		if (!Config.TELNET_PASSWORD.isEmpty())
		{
			// Ask password
			ctx.write("Password:");
			ctx.channel().attr(AUTHORIZED).set(Boolean.FALSE);
		}
		else
		{
			ctx.write("Type 'help' to see all available commands." + Config.EOL);
			ctx.channel().attr(AUTHORIZED).set(Boolean.TRUE);
		}
		ctx.flush();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		// Cast to a String first.
		// We know it is a String because we put some codec in TelnetPipelineFactory.
		String request = (String) msg;
		
		// Generate and write a response.
		String response = null;
		boolean close = false;
		
		if (Boolean.FALSE.equals(ctx.channel().attr(AUTHORIZED).get()))
		{
			if (Config.TELNET_PASSWORD.equals(request))
			{
				ctx.channel().attr(AUTHORIZED).set(Boolean.TRUE);
				request = "";
			}
			else
			{
				response = "Wrong password!" + Config.EOL;
				close = true;
			}
		}
		
		if (Boolean.TRUE.equals(ctx.channel().attr(AUTHORIZED).get()))
		{
			if (request.isEmpty())
			{
				response = "Type 'help' to see all available commands." + Config.EOL;
			}
			else if (request.toLowerCase().equals("exit"))
			{
				response = "Have a good day!" + Config.EOL;
				close = true;
			}
			else
			{
				final Matcher m = COMMAND_ARGS_PATTERN.matcher(request);
				
				if (m.find())
				{
					final String command = m.group();
					final List<String> args = new ArrayList<>();
					String arg;
					
					while (m.find())
					{
						arg = m.group(1);
						
						if (arg == null)
						{
							arg = m.group(0);
						}
						
						args.add(arg);
					}
					
					response = tryHandleCommand(ctx, command, args.toArray(new String[args.size()]));
					if (!response.endsWith(Config.EOL))
					{
						response += Config.EOL;
					}
				}
			}
		}
		
		// We do not need to write a ChannelBuffer here.
		// We know the encoder inserted at TelnetPipelineFactory will do the conversion.
		final ChannelFuture future = ctx.write(response);
		
		// Close the connection after sending 'Have a good day!'
		// if the client has sent 'exit'.
		if (close)
		{
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
	{
		ctx.flush();
	}
}
