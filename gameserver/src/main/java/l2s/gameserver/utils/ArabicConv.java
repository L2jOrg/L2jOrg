package l2s.gameserver.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ArabicConv
{
	private static final char ALF_UPPER_MDD = 0x0622;
	private static final char ALF_UPPER_HAMAZA = 0x0623;
	private static final char ALF_LOWER_HAMAZA = 0x0625;
	private static final char ALF = 0x0627;
	private static final char LAM = 0x0644;

	private static final char ARABIC_GLPHIES[][] = new char[][]
			{
					{'\u0622', '\uFE81', '\uFE81', '\uFE82', '\uFE82', 2},
					{'\u0623', '\uFE82', '\uFE83', '\uFE84', '\uFE84', 2},
					{'\u0624', '\uFE85', '\uFE85', '\uFE86', '\uFE86', 2},
					{'\u0625', '\uFE87', '\uFE87', '\uFE88', '\uFE88', 2},
					{'\u0626', '\uFE89', '\uFE8B', '\uFE8C', '\uFE8A', 4},
					{'\u0627', '\u0627', '\u0627', '\uFE8E', '\uFE8E', 2},
					{'\u0628', '\uFE8F', '\uFE91', '\uFE92', '\uFE90', 4},
					{'\u0629', '\uFE93', '\uFE93', '\uFE94', '\uFE94', 2},
					{'\u062A', '\uFE95', '\uFE97', '\uFE98', '\uFE96', 4},
					{'\u062B', '\uFE99', '\uFE9B', '\uFE9C', '\uFE9A', 4},
					{'\u062C', '\uFE9D', '\uFE9F', '\uFEA0', '\uFE9E', 4},
					{'\u062D', '\uFEA1', '\uFEA3', '\uFEA4', '\uFEA2', 4},
					{'\u062E', '\uFEA5', '\uFEA7', '\uFEA8', '\uFEA6', 4},
					{'\u062F', '\uFEA9', '\uFEA9', '\uFEAA', '\uFEAA', 2},
					{'\u0630', '\uFEAB', '\uFEAB', '\uFEAC', '\uFEAC', 2},
					{'\u0631', '\uFEAD', '\uFEAD', '\uFEAE', '\uFEAE', 2},
					{'\u0632', '\uFEAF', '\uFEAF', '\uFEB0', '\uFEB0', 2},
					{'\u0633', '\uFEB1', '\uFEB3', '\uFEB4', '\uFEB2', 4},
					{'\u0634', '\uFEB5', '\uFEB7', '\uFEB8', '\uFEB6', 4},
					{'\u0635', '\uFEB9', '\uFEBB', '\uFEBC', '\uFEBA', 4},
					{'\u0636', '\uFEBD', '\uFEBF', '\uFEC0', '\uFEBE', 4},
					{'\u0637', '\uFEC1', '\uFEC3', '\uFEC2', '\uFEC4', 4},
					{'\u0638', '\uFEC5', '\uFEC7', '\uFEC6', '\uFEC6', 4},
					{'\u0639', '\uFEC9', '\uFECB', '\uFECC', '\uFECA', 4},
					{'\u063A', '\uFECD', '\uFECF', '\uFED0', '\uFECE', 4},
					{'\u0641', '\uFED1', '\uFED3', '\uFED4', '\uFED2', 4},
					{'\u0642', '\uFED5', '\uFED7', '\uFED8', '\uFED6', 4},
					{'\u0643', '\uFED9', '\uFEDB', '\uFEDC', '\uFEDA', 4},
					{'\u0644', '\uFEDD', '\uFEDF', '\uFEE0', '\uFEDE', 4},
					{'\u0645', '\uFEE1', '\uFEE3', '\uFEE4', '\uFEE2', 4},
					{'\u0646', '\uFEE5', '\uFEE7', '\uFEE8', '\uFEE6', 4},
					{'\u0647', '\uFEE9', '\uFEEB', '\uFEEC', '\uFEEA', 4},
					{'\u0648', '\uFEED', '\uFEED', '\uFEEE', '\uFEEE', 2},
					{'\u0649', '\uFEEF', '\uFEEF', '\uFEF0', '\uFEF0', 2},
					{'\u0671', '\u0671', '\u0671', '\uFB51', '\uFB51', 2},
					{'\u064A', '\uFEF1', '\uFEF3', '\uFEF4', '\uFEF2', 4},
					{'\u066E', '\uFBE4', '\uFBE8', '\uFBE9', '\uFBE5', 4},
					{'\u0671', '\u0671', '\u0671', '\uFB51', '\uFB51', 2},
					{'\u06AA', '\uFB8E', '\uFB90', '\uFB91', '\uFB8F', 4},
					{'\u06C1', '\uFBA6', '\uFBA8', '\uFBA9', '\uFBA7', 4},
					{'\u06E4', '\u06E4', '\u06E4', '\u06E4', '\uFEEE', 2},
					{'\u0686', '\uFB7A', '\uFB7C', '\uFB7D', '\uFB7B', 4},
					{'\u067E', '\uFB56', '\uFB58', '\uFB59', '\uFB57', 4},
					{'\u0698', '\uFB8A', '\uFB8A', '\uFB8B', '\uFB8B', 2},
					{'\u06AF', '\uFB92', '\uFB94', '\uFB95', '\uFB93', 4},
					{'\u06CC', '\uFEEF', '\uFEF3', '\uFEF4', '\uFEF0', 4},
					{'\u06A9', '\uFB8E', '\uFB90', '\uFB91', '\uFB8F', 4},
			};
	private static final Map<Character, char[]> ARABIC_GLPHIES_MAP;

	private static final char HARAKATE[] = {
			'\u064B', '\u064C', '\u064D', '\u064E', '\u064F', '\u0650', '\u0651',
			'\u0652', '\u0653', '\u0654', '\u0655', '\u0656'};

	private static final char LAM_ALEF_GLPHIES[][] =
			{
					{15270, 65270, 65269},
					{15271, 65272, 65271},
					{1573, 65274, 65273},
					{1575, 65276, 65275}
			};

	static
	{
		Map<Character, char[]> arabivGlphiesMap = new HashMap<Character, char[]>();
		for(char[] forms : ARABIC_GLPHIES)
		{
			arabivGlphiesMap.put(forms[0], forms);
		}
		ARABIC_GLPHIES_MAP = Collections.<Character, char[]>unmodifiableMap(arabivGlphiesMap);
	}

	private static char getLamAlef(char AlefCand, char LamCand, boolean isEnd)
	{
		int shiftRate = 1;
		if(isEnd)
		{
			shiftRate++;
		}

		if(LAM == LamCand)
		{
			switch(AlefCand)
			{
			case ALF_UPPER_MDD:
				return LAM_ALEF_GLPHIES[0][shiftRate];
			case ALF_UPPER_HAMAZA:
				return LAM_ALEF_GLPHIES[1][shiftRate];
			case ALF_LOWER_HAMAZA:
				return LAM_ALEF_GLPHIES[2][shiftRate];
			case ALF:
				return LAM_ALEF_GLPHIES[3][shiftRate];
			}
		}
		return 0;
	}

	private static final char getReshapedGlphy(char ch, int off)
	{
		char[] forms = ARABIC_GLPHIES_MAP.get(ch);
		if(forms != null)
		{
			if(ch != forms[0])
			{
				throw new RuntimeException();
			}
			return forms[off];
		}
		return ch;
	}

	private static final char getGlphyType(char ch)
	{
		char[] forms = ARABIC_GLPHIES_MAP.get(ch);
		if(forms != null)
		{
			if(ch != forms[0])
			{
				throw new RuntimeException();
			}
			return forms[5];
		}
		return 2;
	}

	private static String shapeArabic0(String src)
	{
		if(src.isEmpty())
			return "";

		switch(src.length())
		{
			case 0:
				return "";
			case 1:
				return new String(new char[] { getReshapedGlphy(src.charAt(0), 0) });
			case 2:
			{
				final char lam = src.charAt(0);
				final char alif = src.charAt(1);
				final char lam_alif = getLamAlef(alif, lam, true);

				if(lam_alif > 0)
					return new String(new char[]{ lam_alif });

				break;
			}
		}

		char[] reshapedLetters = new char[src.length()];

		char currLetter = src.charAt(0);
		reshapedLetters[0] = getReshapedGlphy(currLetter, 2);

		for(int i = 1; i < src.length() - 1; i++)
		{
			final char lam_alif = getLamAlef(src.charAt(i), currLetter, true);
			if(lam_alif > 0)
			{
				if((i - 2 < 0) || ((i - 2 >= 0) && getGlphyType(src.charAt(i - 2)) == 2))
				{
					reshapedLetters[i - 1] = 0;
					reshapedLetters[i] = lam_alif;
				}
				else
				{
					reshapedLetters[i - 1] = 0;
					reshapedLetters[i] = getLamAlef(src.charAt(i), currLetter, false);
				}
			}
			else
			{
				if(getGlphyType(src.charAt(i - 1)) == 2)
					reshapedLetters[i] = getReshapedGlphy(src.charAt(i), 2);
				else
					reshapedLetters[i] = getReshapedGlphy(src.charAt(i), 3);
			}
			currLetter = src.charAt(i);
		}


		final int len = src.length();
		final char lam_alif = getLamAlef(src.charAt(len - 1), src.charAt(len - 2), true);
		if(lam_alif > 0)
		{
			if(len > 3 && getGlphyType(src.charAt(len - 3)) == 2)
			{
				reshapedLetters[len - 2] = 0;
				reshapedLetters[len - 1] = lam_alif;
			}
			else
			{
				reshapedLetters[len - 2] = 0;
				reshapedLetters[len - 1] = getLamAlef(src.charAt(len - 1), src.charAt(len - 2), false);
			}
		}
		else
		{
			if(getGlphyType(src.charAt(len - 2)) == 2)
				reshapedLetters[len - 1] = getReshapedGlphy(src.charAt(len - 1), 1);
			else
				reshapedLetters[len - 1] = getReshapedGlphy(src.charAt(len - 1), 4);
		}

		StringBuilder sb = new StringBuilder();
		for(char ch : reshapedLetters)
		{
			if(ch != 0)
				sb.append(ch);
		}
		return sb.toString();
	}

	public static boolean isArChar(char ch)
	{
		char[] form = ARABIC_GLPHIES_MAP.get(ch);
		return form != null;
	}

	public static String shapeArabic(String src)
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0, len = src.length(); i < len; i++)
		{
			if(isArChar(src.charAt(i)))
			{
				final int arStart = i;
				for(; i < len && isArChar(src.charAt(i)); i++);
				sb.append(shapeArabic0(src.substring(arStart, i)));
				if(i < len)
				{
					sb.append(src.charAt(i));
				}
			}
			else
			{
				sb.append(src.charAt(i));
			}
		}
		return sb.toString();
	}


	public static final void main(String ... args)
	{

		System.out.println(shapeArabic("adfaорфывلاشستيالاشسريلشر شلصاير لريص"));
		System.out.println(shapeArabic("awdhgb شلشس لسشhgasvрпфымв لرشس2323يلسشار"));
		System.out.println(shapeArabic("dashdاتلاي تاسي تاي اتشسر صعغ غلي	 ضغعليصض dsaugd"));
	}


}