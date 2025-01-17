package dev.brighten.antivpn.database;

import dev.brighten.antivpn.utils.VPNResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface VPNDatabase {
    Optional<VPNResponse> getStoredResponse(String ip);

    void cacheResponse(VPNResponse toCache);

    boolean isWhitelisted(UUID uuid);

    boolean isWhitelisted(String ip);

    void setWhitelisted(UUID uuid, boolean whitelisted);

    void setWhitelisted(String ip, boolean whitelisted);

    List<UUID> getAllWhitelisted();

    List<String> getAllWhitelistedIps();

    void getStoredResponseAsync(String ip, Consumer<Optional<VPNResponse>> result);

    void isWhitelistedAsync(UUID uuid, Consumer<Boolean> result);

    void isWhitelistedAsync(String ip, Consumer<Boolean> result);

    void alertsState(UUID uuid, Consumer<Boolean> result);

    void updateAlertsState(UUID uuid, boolean state);

    void init();

    void shutdown();
}
