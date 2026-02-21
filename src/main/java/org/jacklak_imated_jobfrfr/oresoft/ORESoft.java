package org.jacklak_imated_jobfrfr.oresoft;
import org.bukkit.plugin.java.JavaPlugin;
import wueffi.MiniGameCore.managers.LobbyManager;

public final class ORESoft extends JavaPlugin {
    public LobbyManager lobbyManager;

    @Override
    public void onEnable() {
        getLogger().info("Starting up ORESoft Plugin...");

        LoadWorld.setup(this);
    }

    @Override
    public void onDisable() {

    }
}
