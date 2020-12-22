package amata1219.advancement.api;

import amata1219.advancement.api.Advancement.FrameType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancementAPI extends JavaPlugin implements CommandExecutor {

    private static AdvancementAPI plugin;
    private static final List<Advancement> advancements = new ArrayList<>();

    public void onEnable() {
        plugin = this;
        this.getCommand("ad").setExecutor(this);
    }

    public void onDisable() {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        if (args.length == 0) send("タイトル", "説明", new MaterialData(Material.GOLDEN_CARROT), (Player)sender);
        else if (args.length == 2) send(args[0], args[1], new MaterialData(Material.GOLDEN_CARROT), (Player)sender);
        return true;
    }

    public static AdvancementAPI getPlugin() {
        return plugin;
    }

    public static void addAdvancement(Advancement advancement) {
        advancements.add(advancement);
    }

    public static void removeAdvancement(Advancement advancement) {
        advancements.remove(advancement);
    }

    public void send(String title, String description, MaterialData material, final Player... players) {
        final Advancement api = (new Advancement(new NamespacedKey(getPlugin(), "story/" + UUID.randomUUID().toString()))).withFrame(FrameType.GOAL).withTrigger("minecraft:impossible").withIcon(material).withTitle(title).withDescription(description).withAnnouncement(false).withBackground("minecraft:textures/blocks/bedrock.png");
        api.loadAdvancement();
        api.sendPlayer(players);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> api.delete(players), 10L);
    }

}
