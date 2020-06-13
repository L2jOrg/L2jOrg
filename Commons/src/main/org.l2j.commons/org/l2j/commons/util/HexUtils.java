/*
 * Copyright © 2019-2020 L2JOrg
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
package org.l2j.commons.util;

import java.util.Arrays;

/**
 * @author HorridoJoho
 */
public class HexUtils
{
	// lookup table for hex characters
	private static final char[] _NIBBLE_CHAR_LOOKUP =
	{
		'0',
		'1',
		'2',
		'3',
		'4',
		'5',
		'6',
		'7',
		'8',
		'9',
		'A',
		'B',
		'C',
		'D',
		'E',
		'F'
	};
	private static final char[] _NEW_LINE_CHARS = System.getProperty("line.separator").toCharArray();
	
	/**
	 * Method to generate the hexadecimal character presentation of a byte<br>
	 * This call is equivalent to {@link HexUtils#b2HexChars(byte, char[], int)} with parameters (data, null, 0)
	 * @param data byte to generate the hexadecimal character presentation from
	 * @return a new char array with exactly 2 elements
	 */
	public static char[] b2HexChars(byte data)
	{
		return b2HexChars(data, null, 0);
	}
	
	/**
	 * Method to generate the hexadecimal character presentation of a byte
	 * @param data byte to generate the hexadecimal character presentation from
	 * @param dstHexChars the char array the hexadecimal character presentation should be copied to, if this is null, dstOffset is ignored and a new char array with 2 elements is created
	 * @param dstOffset offset at which the hexadecimal character presentation is copied to dstHexChars
	 * @return the char array the hexadecimal character presentation was copied to
	 */
	public static char[] b2HexChars(byte data, char[] dstHexChars, int dstOffset)
	{
		if (dstHexChars == null)
		{
			dstHexChars = new char[2];
			dstOffset = 0;
		}
		
		// /////////////////////////////
		// NIBBLE LOOKUP
		dstHexChars[dstOffset] = _NIBBLE_CHAR_LOOKUP[(data & 0xF0) >> 4];
		dstHexChars[dstOffset + 1] = _NIBBLE_CHAR_LOOKUP[data & 0x0F];
		
		return dstHexChars;
	}
	
	/**
	 * Method to generate the hexadecimal character presentation of an integer This call is equivalent to {@link HexUtils#int2HexChars(int, char[], int)} with parameters (data, null, 0)
	 * @param data integer to generate the hexadecimal character presentation from
	 * @return new char array with 8 elements
	 */
	public static char[] int2HexChars(int data)
	{
		return int2HexChars(data, new char[8], 0);
	}
	
	/**
	 * Method to generate the hexadecimal character presentation of an integer
	 * @param data integer to generate the hexadecimal character presentation from
	 * @param dstHexChars the char array the hexadecimal character presentation should be copied to, if this is null, dstOffset is ignored and a new char array with 8 elements is created
	 * @param dstOffset offset at which the hexadecimal character presentation is copied to dstHexChars
	 * @return the char array the hexadecimal character presentation was copied to
	 */
	public static char[] int2HexChars(int data, char[] dstHexChars, int dstOffset)
	{
		if (dstHexChars == null)
		{
			dstHexChars = new char[8];
			dstOffset = 0;
		}
		
		b2HexChars((byte) ((data & 0xFF000000) >> 24), dstHexChars, dstOffset);
		b2HexChars((byte) ((data & 0x00FF0000) >> 16), dstHexChars, dstOffset + 2);
		b2HexChars((byte) ((data & 0x0000FF00) >> 8), dstHexChars, dstOffset + 4);
		b2HexChars((byte) (data & 0x000000FF), dstHexChars, dstOffset + 6);
		return dstHexChars;
	}
	
	/**
	 * Method to generate the hexadecimal character presentation of a byte array<br>
	 * This call is equivalent to {@link HexUtils#bArr2HexChars(byte[], int, int, char[], int)} with parameters (data, offset, len, null, 0)
	 * @param data byte array to generate the hexadecimal character presentation from
	 * @param offset offset where to start in data array
	 * @param len number of bytes to generate the hexadecimal character presentation from
	 * @return a new char array with len*2 elements
	 */
	public static char[] bArr2HexChars(byte[] data, int offset, int len)
	{
		return bArr2HexChars(data, offset, len, null, 0);
	}
	
	/**
	 * Method to generate the hexadecimal character presentation of a byte array
	 * @param data byte array to generate the hexadecimal character presentation from
	 * @param offset offset where to start in data array
	 * @param len number of bytes to generate the hexadecimal character presentation from
	 * @param dstHexChars the char array the hexadecimal character presentation should be copied to, if this is null, dstOffset is ignored and a new char array with len*2 elements is created
	 * @param dstOffset offset at which the hexadecimal character presentation is copied to dstHexChars
	 * @return the char array the hexadecimal character presentation was copied to
	 */
	public static char[] bArr2HexChars(byte[] data, int offset, int len, char[] dstHexChars, int dstOffset)
	{
		if (dstHexChars == null)
		{
			dstHexChars = new char[len * 2];
			dstOffset = 0;
		}
		
		for (int dataIdx = offset, charsIdx = dstOffset; dataIdx < (len + offset); ++dataIdx, ++charsIdx)
		{
			// /////////////////////////////
			// NIBBLE LOOKUP, we duplicate the code from b2HexChars here, we want to save a few cycles(for charsIdx increment)
			dstHexChars[charsIdx] = _NIBBLE_CHAR_LOOKUP[(data[dataIdx] & 0xF0) >> 4];
			dstHexChars[++charsIdx] = _NIBBLE_CHAR_LOOKUP[data[dataIdx] & 0x0F];
		}
		
		return dstHexChars;
	}
	
	public static char[] bArr2AsciiChars(byte[] data, int offset, int len)
	{
		return bArr2AsciiChars(data, offset, len, new char[len], 0);
	}
	
	public static char[] bArr2AsciiChars(byte[] data, int offset, int len, char[] dstAsciiChars, int dstOffset)
	{
		if (dstAsciiChars == null)
		{
			dstAsciiChars = new char[len];
			dstOffset = 0;
		}
		
		for (int dataIdx = offset, charsIdx = dstOffset; dataIdx < (len + offset); ++dataIdx, ++charsIdx)
		{
			if ((data[dataIdx] > 0x1f) && (data[dataIdx] < 0x80))
			{
				dstAsciiChars[charsIdx] = (char) data[dataIdx];
			}
			else
			{
				dstAsciiChars[charsIdx] = '.';
			}
		}
		
		return dstAsciiChars;
	}
	
	private static final int _HEX_ED_BPL = 16;
	private static final int _HEX_ED_CPB = 2;
	
	/**
	 * Method to generate the hexadecimal character representation of a byte array like in a hex editor<br>
	 * Line Format: {OFFSET} {HEXADECIMAL} {ASCII}({NEWLINE})<br>
	 * {OFFSET} = offset of the first byte in line(8 chars)<br>
	 * {HEXADECIMAL} = hexadecimal character representation({@link #_HEX_ED_BPL}*2 chars)<br>
	 * {ASCII} = ascii character presentation({@link #_HEX_ED_BPL} chars)
	 * @param data byte array to generate the hexadecimal character representation
	 * @param len the number of bytes to generate the hexadecimal character representation from
	 * @return byte array which contains the hexadecimal character representation of the given byte array
	 */
	public static char[] bArr2HexEdChars(byte[] data, int len)
	{
		// {OFFSET} {HEXADECIMAL} {ASCII}{NEWLINE}
		final int lineLength = 9 + (_HEX_ED_BPL * _HEX_ED_CPB) + 1 + _HEX_ED_BPL + _NEW_LINE_CHARS.length;
		final int lenBplMod = len % _HEX_ED_BPL;
		// create text buffer
		// 1. don't allocate a full last line if not _HEX_ED_BPL bytes are shown in last line
		// 2. no new line at end of buffer
		// BUG: when the length is multiple of _HEX_ED_BPL we erase the whole ascii space with this
		// char[] textData = new char[lineLength * numLines - (_HEX_ED_BPL - (len % _HEX_ED_BPL)) - _NEW_LINE_CHARS.length];
		// FIXED HERE
		int numLines;
		char[] textData;
		if (lenBplMod == 0)
		{
			numLines = len / _HEX_ED_BPL;
			textData = new char[(lineLength * numLines) - _NEW_LINE_CHARS.length];
		}
		else
		{
			numLines = (len / _HEX_ED_BPL) + 1;
			textData = new char[(lineLength * numLines) - (_HEX_ED_BPL - (lenBplMod)) - _NEW_LINE_CHARS.length];
		}
		
		// performance penalty, only doing space filling in the loop is faster
		// Arrays.fill(textData, ' ');
		
		int dataOffset;
		int dataLen;
		int lineStart;
		int lineHexDataStart;
		int lineAsciiDataStart;
		for (int i = 0; i < numLines; ++i)
		{
			dataOffset = i * _HEX_ED_BPL;
			dataLen = Math.min(len - dataOffset, _HEX_ED_BPL);
			lineStart = i * lineLength;
			lineHexDataStart = lineStart + 9;
			lineAsciiDataStart = lineHexDataStart + (_HEX_ED_BPL * _HEX_ED_CPB) + 1;
			
			int2HexChars(dataOffset, textData, lineStart); // the offset of this line
			textData[lineHexDataStart - 1] = ' '; // separate
			bArr2HexChars(data, dataOffset, dataLen, textData, lineHexDataStart); // the data in hex
			bArr2AsciiChars(data, dataOffset, dataLen, textData, lineAsciiDataStart); // the data in ascii
			
			if (i < (numLines - 1))
			{
				textData[lineAsciiDataStart - 1] = ' '; // separate
				System.arraycopy(_NEW_LINE_CHARS, 0, textData, lineAsciiDataStart + _HEX_ED_BPL, _NEW_LINE_CHARS.length); // the new line
			}
			else if (dataLen < _HEX_ED_BPL)
			{
				// last line which shows less than _HEX_ED_BPL bytes
				final int lineHexDataEnd = lineHexDataStart + (dataLen * _HEX_ED_CPB);
				Arrays.fill(textData, lineHexDataEnd, lineHexDataEnd + ((_HEX_ED_BPL - dataLen) * _HEX_ED_CPB) + 1, ' '); // spaces, for the last line if there are not _HEX_ED_BPL bytes
			}
			else
			{
				// last line which shows _HEX_ED_BPL bytes
				textData[lineAsciiDataStart - 1] = ' '; // separate
			}
		}
		return textData;
	}
}
