/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 *
 * File Created @ [23/01/2016, 21:48:13 (GMT)]
 */
package vazkii.psi.client.core.helper;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class TextHelper {

	@SideOnly(Side.CLIENT)
	public static List<String> renderText(int x, int y, int width, String unlocalizedText, boolean centered, boolean doit, Object... format) {
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		boolean unicode = font.getUnicodeFlag();
		font.setUnicodeFlag(true);
		String text = I18n.translateToLocal(unlocalizedText).replaceAll("&", "\u00a7");
		if(format != null && format.length > 0)
			text = String.format(text, format);

		String[] textEntries = text.split("<br>");
		List<List<String>> lines = new ArrayList();

		String controlCodes = "";
		for(String s : textEntries) {
			List<String> words = new ArrayList();
			String lineStr = "";
			String[] tokens = s.split(" ");
			for(String token : tokens) {
				String prev = lineStr;
				String spaced = token + " ";
				lineStr += spaced;

				controlCodes = toControlCodes(getControlCodes(prev));
				if(font.getStringWidth(token) > width) {
					if (!words.isEmpty()) {
						lines.add(words);
						words = new ArrayList();
					}

					String temp = "";
					int nowWordWidth = 0;
					for (int i = 0; i < token.length(); i++) {
						char c = token.charAt(i);
						int charWidth = font.getCharWidth(c);
						if (nowWordWidth + charWidth > width) {
							words.add(temp);
							lines.add(words);
							temp = "";
							nowWordWidth = 0;
							words = new ArrayList();
						}

						nowWordWidth += charWidth;
						temp += c;
					}
					if (!temp.isEmpty()) {
						words.add(temp);
						lines.add(words);
						words = new ArrayList();
					}

					continue;
				}
				else if(font.getStringWidth(lineStr) > width) {
					lines.add(words);
					lineStr = controlCodes + spaced;
					words = new ArrayList();
				}

				words.add(controlCodes + token);
			}

			if(!lineStr.isEmpty())
				lines.add(words);
			lines.add(new ArrayList());
		}

		List<String> textLines = new ArrayList();

		String lastLine = "";
		for(List<String> words : lines) {
			words.size();
			int xi = x;
			int spacing = 4;
			int wcount = words.size();
			int compensationSpaces = 0;
			boolean justify = false;

			if(justify) {
				String s = Joiner.on("").join(words);
				int swidth = font.getStringWidth(s);
				int space = width - swidth;

				spacing = wcount == 1 ? 0 : space / (wcount - 1);
				compensationSpaces = wcount == 1 ? 0 : space % (wcount - 1);
			}

			String lineStr = "";
			for(String s : words) {
				int extra = 0;
				if(compensationSpaces > 0) {
					compensationSpaces--;
					extra++;
				}

				int swidth = font.getStringWidth(s);
				if(doit) {
					if(centered)
						font.drawString(s, xi + width / 2 - swidth / 2, y, 0xFFFFFF);
					else font.drawString(s, xi, y, 0xFFFFFF);
				}
				xi += swidth + spacing + extra;
				lineStr += s + " ";
			}

			if(!lineStr.isEmpty() || lastLine.isEmpty()) {
				y += 10;
				textLines.add(lineStr);
			}
			lastLine = lineStr;
		}

		font.setUnicodeFlag(unicode);
		return textLines;
	}

	public static String getControlCodes(String s) {
		String controls = s.replaceAll("(?<!\u00a7)(.)", "");
		String wiped = controls.replaceAll(".*r", "r");
		return wiped;
	}

	public static String toControlCodes(String s) {
		return s.replaceAll(".", "\u00a7$0");
	}

}
