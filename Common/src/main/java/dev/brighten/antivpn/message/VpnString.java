package dev.brighten.antivpn.message;

import dev.brighten.antivpn.api.APIPlayer;
import lombok.*;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class VpnString {
    private final String key;
    private final String defaultMessage;
    private String message;
    @Setter
    private Function<VpnString, String> configStringGetter;

    public VpnString(String key, String defaultMessage) {
        this.key = key;
        this.defaultMessage = defaultMessage;
        this.message = defaultMessage;
    }

    @SneakyThrows
    public void updateString() {
        if(configStringGetter == null) throw new Exception("The configStringGetter for string " + key + " is null!");

        message = configStringGetter.apply(this);
    }

    public String getFormattedMessage(Var<String, Object>... replacements) {
        String formatted = message;

        for (Var<String, Object> replacement : replacements) {
            formatted = formatted
                    .replace("%" + replacement.getKey() + "%", replacement.getReplacement().toString());
        }

        return formatted;
    }

    public void sendMessage(APIPlayer player, Var<String, Object>... replacements) {
        String formatted = message;

        for (Var<String, Object> replacement : replacements) {
            formatted = formatted
                    .replace("%" + replacement.getKey() + "%", replacement.getReplacement().toString());
        }
        player.sendMessage(formatted);
    }

    @Getter
    @RequiredArgsConstructor
    public static class Var<S, O> {
        private final String key;
        private final Object replacement;
    }
}
