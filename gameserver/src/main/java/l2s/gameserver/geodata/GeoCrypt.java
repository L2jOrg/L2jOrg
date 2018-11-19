package l2s.gameserver.geodata;

import l2s.gameserver.GameServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class GeoCrypt
{
	public static int decrypt(int blobOff, FileChannel roChannel, ByteBuffer buff) throws IOException
	{
		int checkSum = 0;
		if(blobOff == 4)
		{
			checkSum = 0x814141AB;

			byte[] addrBytes = GameServer.getInstance().getLicenseHost().trim().getBytes();
			for (int addrByteIdx = 0; addrByteIdx < addrBytes.length; addrByteIdx++)
			{
				checkSum ^= addrBytes[addrByteIdx];
				checkSum = checkSum >>> 1 | checkSum << 31;
			}

			checkSum ^= buff.getInt();

			byte xorByte = (byte) ((checkSum >> 24 & 0xff) ^
					(checkSum >> 16 & 0xff) ^
					(checkSum >> 8 & 0xff) ^
					(checkSum >> 0 & 0xff));

			buff.clear();
			roChannel.read(buff);
			buff.rewind();
			while(buff.hasRemaining())
			{
				byte inByte = buff.get();
				byte outByte = (byte) (inByte ^ xorByte); // poor man's crypt
				buff.put(buff.position() - 1, outByte);
				xorByte = outByte;
				checkSum -= outByte;
			}
			buff.rewind();
		}
		return checkSum;
		//return 0;
	}
}