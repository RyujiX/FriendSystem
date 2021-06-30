package net.ryu.friendsystem.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Txt {

    private final Pattern REPLACE_ALL_RGB_PATTERN = Pattern.compile("(&)?&#([0-9a-fA-F]{6})");
    private final Pattern REPLACE_GRADIENT_PATTERN = Pattern.compile("<gradient:([0-9A-Fa-f]{6}),([0-9A-Fa-f]{6})>(.*?)</gradient>");

    private final Version version = Version.getServerVersion(Bukkit.getServer());

    public String parse(String string) {
        final StringBuffer stringBuffer = new StringBuffer();
        String message = string;

        if (version.isNewerOrSameThan(Version.v1_16_R1)) {
            final Matcher gMatcher = REPLACE_GRADIENT_PATTERN.matcher(message);
            while (gMatcher.find()) {
                String start = gMatcher.group(1);
                String end = gMatcher.group(2);
                String content = gMatcher.group(3);

                message = string.replace(gMatcher.group(), colorGradient(content, new Color(Integer.parseInt(start, 16)), new Color(Integer.parseInt(end, 16))));
            }

            final Matcher matcher = REPLACE_ALL_RGB_PATTERN.matcher(message);
            while (matcher.find()) {
                final boolean isEscaped = matcher.group(1) != null;
                if (!isEscaped) {
                    try {
                        final String hexCode = matcher.group(2);
                        matcher.appendReplacement(stringBuffer, parseHexColor(hexCode));
                        continue;
                    } catch (final NumberFormatException ignored) {}
                }
                matcher.appendReplacement(stringBuffer, "&#$2");
            }
            matcher.appendTail(stringBuffer);
        }

        return ChatColor.translateAlternateColorCodes('&', stringBuffer.toString());
    }

    public String parseHexColor(String hexColor) throws NumberFormatException {
        if (!version.isNewerOrSameThan(Version.v1_16_R1)) {
            throw new NumberFormatException("Cannot use RGB colors in versions < 1.16");
        }

        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }
        if (hexColor.length() != 6) {
            throw new NumberFormatException("Invalid hex length");
        }
        Color.decode("#" + hexColor);
        final StringBuilder assembledColorCode = new StringBuilder();
        assembledColorCode.append(ChatColor.COLOR_CHAR + "x");
        for (final char curChar : hexColor.toCharArray()) {
            assembledColorCode.append(ChatColor.COLOR_CHAR).append(curChar);
        }
        return assembledColorCode.toString();
    }

    private ChatColor[] createGradient(Color start, Color end, int step) {
        ChatColor[] colors = new ChatColor[step];
        int stepR = Math.abs(start.getRed() - end.getRed()) / (step - 1);
        int stepG = Math.abs(start.getGreen() - end.getGreen()) / (step - 1);
        int stepB = Math.abs(start.getBlue() - end.getBlue()) / (step - 1);
        int[] direction = new int[]{
                start.getRed() < end.getRed() ? +1 : -1,
                start.getGreen() < end.getGreen() ? +1 : -1,
                start.getBlue() < end.getBlue() ? +1 : -1
        };

        for (int i = 0; i < step; i++) {
            Color color = new Color(start.getRed() + ((stepR * i) * direction[0]), start.getGreen() + ((stepG * i) * direction[1]), start.getBlue() + ((stepB * i) * direction[2]));
            colors[i] = ChatColor.of(color);
        }
        return colors;
    }

    public String colorGradient(String string, Color start, Color end) {
        StringBuilder stringBuilder = new StringBuilder();

        String message = ChatColor.stripColor(string);
        ChatColor[] colors = createGradient(start, end, message.length());
        String[] characters = string.split("");

        int color = 0;
        boolean bold = false, italic = false, magic = false, underline = false, strikethrough = false;

        for (int i = 0; i < string.length(); i++) {
            if (characters[i].equals("&")) {
                if (!characters[i + 1].equals("r")) {
                    if ("l".equals(characters[i + 1])) {
                        bold = true;
                    } else if (characters[i + 1].equals("o")) {
                        italic = true;
                    } else if (characters[i + 1].equals("k")) {
                        magic = true;
                    } else if (characters[i + 1].equals("n")) {
                        underline = true;
                    } else if (characters[i + 1].equals("m")) {
                        strikethrough = true;
                    }
                } else {
                    bold = false;
                    italic = false;
                    magic = false;
                    underline = false;
                    strikethrough = false;
                }
                i = i + 1;
                continue;
            }

            stringBuilder
                    .append(colors[color])
                    .append(bold ? ChatColor.BOLD : "")
                    .append(italic ? ChatColor.ITALIC : "")
                    .append(magic ? ChatColor.MAGIC : "")
                    .append(underline ? ChatColor.UNDERLINE : "")
                    .append(strikethrough ? ChatColor.STRIKETHROUGH : "")
                    .append(characters[i]);

            color++;
        }
        return stringBuilder.toString();
    }
}
