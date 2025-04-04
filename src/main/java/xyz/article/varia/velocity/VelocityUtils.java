package xyz.article.varia.velocity;

import com.velocitypowered.api.command.CommandSource;

public class VelocityUtils {
    public static String reColor(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }
    public static String reColor(String textToTranslate) {
        return reColor('&', textToTranslate);
    }

    public static void sendMessageWithPrefix(CommandSource sender, String message) {
        sender.sendPlainMessage(reColor(VariaVelocity.prefix + message));
    }
}
