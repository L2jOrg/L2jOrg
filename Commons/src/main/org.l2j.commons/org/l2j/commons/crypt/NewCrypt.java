/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.commons.crypt;

import io.github.joealisson.mmocore.Buffer;

import java.io.IOException;

/**
 * @author JoeAlisson
 */
public class NewCrypt {

	BlowfishEngine crypterEngine;
	BlowfishEngine decrypterEngine;

	public NewCrypt(byte[] blowfishKey) {
		crypterEngine = new BlowfishEngine();
		crypterEngine.init(true, blowfishKey);
		decrypterEngine = new BlowfishEngine();
		decrypterEngine.init(false, blowfishKey);
	}
	
	public NewCrypt(String key)
	{
		this(key.getBytes());
	}
	
	public static boolean verifyChecksum(Buffer data, final int offset, final int size) {
		// check if size is multiple of 4 and if there is more then only the checksum
		if ((size & 3) != 0 || size <= 4) {
			return false;
		}
		
		long checksum = 0;
		int count = size - 4;
		int i;
		
		for (i = offset; i < count; i += 4) {
			checksum ^= data.readInt(i);
		}

		return data.readInt(i) == checksum;
	}
	
	public static void appendChecksum(Buffer data, final int offset, final int size) {
		int checksum = 0;
		int count = size - 4;
		int i;
		
		for (i = offset; i < count; i += 4) {
			checksum ^= data.readInt(i);
		}
		data.writeInt(i, checksum);
	}

	/**
	 * Packet is first XOR encoded with <code>key</code>
	 * Then, the last 4 bytes are overwritten with the the XOR "key".
     * Thus this assume that there is enough room for the key to fit without overwriting data.
     *
	 * @param raw The raw bytes to be encrypted
	 * @param offset The begining of the data to be encrypted
	 * @param size Length of the data to be encrypted
	 * @param key The 4 bytes (int) XOR key
	 */
	public static void encXORPass(Buffer raw, final int offset, final int size, int key) {
		int stop = size - 8;
		int pos = 4 + offset;
		int edx;
		int ecx = key; // Initial xor key
		
		while (pos < stop) {
			edx = raw.readInt(pos);
			ecx += edx;
			edx ^= ecx;
			raw.writeInt(pos, edx);
			pos+=4;
		}
		raw.writeInt(pos, ecx);
	}
	
	public synchronized void decrypt(Buffer raw, final int offset, final int size) throws IOException {
		int block = decrypterEngine.getBlockSize();
		int count = size / block;
		
		for (int i = 0; i < count; i++) {
			decrypterEngine.processBlock(raw, offset + (i * block));
		}
	}
	
	public void crypt(Buffer raw, final int offset, final int size) throws IOException {
		int block = crypterEngine.getBlockSize();
		int count = size / block;
		for (int i = 0; i < count; i++) {
			crypterEngine.processBlock(raw, offset + (i * block));
		}
	}
}
