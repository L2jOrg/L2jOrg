package l2s.commons.net.utils;

/**
 * Представление маски сети IPv4
 * 
 * Поддерживается как CIDR представление, так и Apache-like. 
 * 
 * @author G1ta0
 */
public class Net
{
	private final int address;
	private final int netmask;

	public Net(int net, int mask)
	{
		this.address = net;
		this.netmask = mask;
	}

	public int address()
	{
		return this.address;
	}

	public int netmask()
	{
		return this.netmask;
	}

	public boolean isInRange(int address)
	{
		return ((address & this.netmask) == this.address);
	}

	public boolean isInRange(String address)
	{
		return isInRange(parseAddress(address));
	}

	public static Net valueOf(String s)
	{
		int address = 0;
		int netmask = 0;

		String[] mask = s.trim().split("\\b\\/\\b");
		if(mask.length < 1 || mask.length > 2)
			throw new IllegalArgumentException("For input string: \"" + s + "\"");

		if(mask.length == 1)
		{
			String[] octets = mask[0].split("\\.");
			if(octets.length < 1 || octets.length > 4)
				throw new IllegalArgumentException("For input string: \"" + s + "\"");

			int i;
			for(i = 1; i <= octets.length; i++)
			{
				if(!octets[i - 1].equals("*"))
				{
					address |= (Integer.parseInt(octets[i - 1]) << (32 - i * 8));
					netmask |= (0xff << (32 - i * 8));
				}
			}
		}
		else
		{
			address = parseAddress(mask[0]);
			netmask = parseNetmask(mask[1]);
		}

		return new Net(address, netmask);
	}

	public static int parseAddress(String s) throws IllegalArgumentException
	{
		int ip = 0;
		String[] octets = s.split("\\.");
		if(octets.length != 4)
			throw new IllegalArgumentException("For input string: \"" + s + "\"");

		for(int i = 1; i <= octets.length; i++)
			ip |= (Integer.parseInt(octets[i - 1]) << (32 - i * 8));

		return ip;
	}

	public static int parseNetmask(String s) throws IllegalArgumentException
	{
		int mask = 0;
		String[] octets = s.split("\\.");
		if(octets.length == 1)
		{
			int bitmask = Integer.parseInt(octets[0]);
			if(bitmask < 0 || bitmask > 32)
				throw new IllegalArgumentException("For input string: \"" + s + "\"");

			mask = (0xffffffff << (32 - bitmask));
		}
		else
		{
			for(int i = 1; i <= octets.length; i++)
				mask |= (Integer.parseInt(octets[i - 1]) << (32 - i * 8));
		}

		return mask;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		if(o == null)
			return false;
		if(o instanceof Net)
			return (((Net) o).address() == this.address && ((Net) o).netmask() == this.netmask);

		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = address;
		hash = 27 * hash + netmask;
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(address >>> 24).append(".");
		sb.append((address << 8) >>> 24).append(".");
		sb.append((address << 16) >>> 24).append(".");
		sb.append((address << 24) >>> 24);
		sb.append("/");
		sb.append(netmask >>> 24).append(".");
		sb.append((netmask << 8) >>> 24).append(".");
		sb.append((netmask << 16) >>> 24).append(".");
		sb.append((netmask << 24) >>> 24);
		return sb.toString();
	}
}
