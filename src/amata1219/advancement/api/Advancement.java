package amata1219.advancement.api;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Advancement {

    private final NamespacedKey id;
    private String title;
    private String parent;
    private String trigger;
    private String icon;
    private String description;
    private String background;
    private String frame;
    private Integer subID = 0;
    private Integer amount = 0;
    private boolean announce;
    private boolean toast = true;
    private final List<ItemStack> items;

    public Advancement(NamespacedKey id) {
        this.id = id;
        items = Lists.newArrayList();
        announce = true;
    }

    public NamespacedKey getID() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public Advancement withIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public Advancement withIcon(Material material) {
        icon = getMinecraftIDFrom(new ItemStack(material));
        return this;
    }

    public Advancement withIcon(MaterialData material) {
        icon = getMinecraftIDFrom(new ItemStack(material.getItemType()));
        subID = (int) material.getData();
        return this;
    }

    public Advancement withIconData(int subID) {
        this.subID = subID;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Advancement withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getBackground() {
        return background;
    }

    public Advancement withBackground(String url) {
        background = url;
        return this;
    }

    public Advancement withAmount(int i) {
        amount = i;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Advancement withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getParent() {
        return parent;
    }

    public Advancement withParent(String parent) {
        this.parent = parent;
        return this;
    }

    public Advancement withToast(boolean bool) {
        toast = bool;
        return this;
    }

    public String getTrigger() {
        return trigger;
    }

    public Advancement withTrigger(String trigger) {
        this.trigger = trigger;
        return this;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public Advancement withItem(ItemStack is) {
        items.add(is);
        return this;
    }

    public String getFrame() {
        return frame;
    }

    public Advancement withFrame(Advancement.FrameType frame) {
        this.frame = frame.getName();
        return this;
    }

    public boolean getAnnouncement() {
        return announce;
    }

    public Advancement withAnnouncement(boolean announce) {
        this.announce = announce;
        return this;
    }

    public String getJSON() {
        if (amount > 0) {
            return getJson(amount);
        } else {
            JSONObject json = new JSONObject();
            JSONObject icon = new JSONObject();
            icon.put("item", getIcon());
            icon.put("data", getIconSubID());
            JSONObject display = new JSONObject();
            display.put("icon", icon);
            display.put("title", getTitle());
            display.put("description", getDescription());
            display.put("background", getBackground());
            display.put("frame", getFrame());
            display.put("announce_to_chat", getAnnouncement());
            display.put("show_toast", getToast());
            json.put("parent", getParent());
            JSONObject criteria = new JSONObject();
            JSONObject conditions = new JSONObject();
            JSONObject elytra = new JSONObject();
            JSONArray itemArray = new JSONArray();
            JSONObject itemJSON = new JSONObject();

            for (ItemStack i : getItems()) {
                itemJSON.put("item", "minecraft:" + i.getType().name().toLowerCase());
                itemJSON.put("amount", i.getAmount());
                itemArray.add(itemJSON);
            }

            conditions.put("items", itemArray);
            elytra.put("trigger", getTrigger());
            elytra.put("conditions", conditions);
            criteria.put("elytra", elytra);
            json.put("criteria", criteria);
            json.put("display", display);
            Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
            String prettyJson = gson.toJson(json);
            return prettyJson;
        }
    }

    public String getJson(int amaunt) {
        if (!getFrame().equalsIgnoreCase("challenge")) {
            return getJSON();
        } else {
            JSONObject json = new JSONObject();
            JSONObject icon = new JSONObject();
            icon.put("item", getIcon());
            icon.put("data", getIconSubID());
            JSONObject display = new JSONObject();
            display.put("icon", icon);
            display.put("title", getTitle());
            display.put("description", getDescription());
            display.put("background", getBackground());
            display.put("frame", getFrame());
            display.put("announce_to_chat", getAnnouncement());
            display.put("show_toast", getToast());
            json.put("parent", getParent());
            JSONObject criteria = new JSONObject();
            JSONObject conditions = new JSONObject();
            JSONArray itemArray = new JSONArray();
            JSONObject itemJSON = new JSONObject();

            for (ItemStack i : getItems()) {
                itemJSON.put("item", "minecraft:" + i.getType().name().toLowerCase());
                itemJSON.put("amount", i.getAmount());
                itemArray.add(itemJSON);
            }

            for (int i = 0; i <= amaunt; ++i) {
                JSONObject elytra = new JSONObject();
                elytra.put("trigger", "minecraft:impossible");
                conditions.put("items", itemArray);
                elytra.put("conditions", conditions);
                criteria.put("key" + i, elytra);
            }

            json.put("criteria", criteria);
            json.put("display", display);
            Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
            String prettyJson = gson.toJson(json);
            return prettyJson;
        }
    }

    private boolean getToast() {
        return toast;
    }

    private int getIconSubID() {
        return subID;
    }

    public void loadAdvancement() {
        for (World world : Bukkit.getWorlds()) {
            Path path = Paths.get(world.getWorldFolder() + File.separator + "data" + File.separator + "advancements" + File.separator + id.getNamespace() + File.separator + getID().getKey().split("/")[0]);
            Path path2 = Paths.get(world.getWorldFolder() + File.separator + "data" + File.separator + "advancements" + File.separator + id.getNamespace() + File.separator + getID().getKey().split("/")[0] + File.separator + getID().getKey().split("/")[1] + ".json");
            if (!path.toFile().exists()) {
                path.toFile().mkdirs();
            }

            if (!path2.toFile().exists()) {
                File file = path2.toFile();

                try {
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    writer.write(getJSON());
                    writer.flush();
                    writer.close();
                } catch (IOException var7) {
                    var7.printStackTrace();
                }
            }
        }

        if (Bukkit.getAdvancement(getID()) == null) {
            Bukkit.getUnsafe().loadAdvancement(getID(), getJSON());
        }

        AdvancementAPI.addAdvancement(this);
    }

    public void delete() {
        Bukkit.getUnsafe().removeAdvancement(getID());
    }

    public void delete(Player... player) {
        for (Player p : player) {
            if (p.getAdvancementProgress(getAdvancement()).isDone()) {
                p.getAdvancementProgress(getAdvancement()).revokeCriteria("elytra");
            }
        }

        Bukkit.getScheduler().runTaskLater(AdvancementAPI.getPlugin(), () -> CraftMagicNumbers.INSTANCE.removeAdvancement(getID()), 5L);
    }

    public static String getMinecraftIDFrom(ItemStack stack) {
        int check = Item.getId(CraftItemStack.asNMSCopy(stack).getItem());
        MinecraftKey matching = Item.REGISTRY.keySet().stream().filter((key) -> Item.getId(Item.REGISTRY.get(key)) == check).findFirst().orElse(null);
        return Objects.toString(matching, null);
    }

    public void sendPlayer(Player... player) {
        for (Player p : player) {
            if (!p.getAdvancementProgress(getAdvancement()).isDone()) {
                p.getAdvancementProgress(getAdvancement()).awardCriteria("elytra");
            }
        }

    }

    public void sendPlayer(String criteria, Player... player) {
        for (Player p : player) {
            if (!p.getAdvancementProgress(getAdvancement()).isDone()) {
                p.getAdvancementProgress(getAdvancement()).awardCriteria(criteria);
            }
        }

    }

    public boolean next(Player p) {
        if (!p.getAdvancementProgress(getAdvancement()).isDone()) {

            for (String criteria : getAdvancement().getCriteria()) {
                if (p.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria) == null) {
                    p.getAdvancementProgress(getAdvancement()).awardCriteria(criteria);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean next(Player p, long diff, boolean onlyLast) {
        if (!p.getAdvancementProgress(getAdvancement()).isDone()) {
            Date oldData = null;
            String str = "";

            String criteria;
            for (Iterator<String> var8 = getAdvancement().getCriteria().iterator(); var8.hasNext(); str = criteria) {
                criteria = var8.next();
                if (p.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria) == null) {
                    if (oldData == null) {
                        p.getAdvancementProgress(getAdvancement()).awardCriteria(criteria);
                        return true;
                    }

                    long oldTime = oldData.getTime();
                    long current = System.currentTimeMillis();
                    if (current - diff <= oldTime) {
                        p.getAdvancementProgress(getAdvancement()).awardCriteria(criteria);
                        return true;
                    }

                    if (onlyLast) {
                        p.getAdvancementProgress(getAdvancement()).revokeCriteria(str);
                        return false;
                    }

                    for (String string : getAdvancement().getCriteria()) {
                        p.getAdvancementProgress(getAdvancement()).revokeCriteria(string);
                    }

                    p.getAdvancementProgress(getAdvancement()).awardCriteria((String) getAdvancement().getCriteria().stream().findFirst().get());
                    return false;
                }

                oldData = p.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria);
            }
        }

        return false;
    }

    public Date getLastAwardTime(Player p) {
        if (!p.getAdvancementProgress(getAdvancement()).isDone()) {
            Date oldData = null;

            String criteria;
            for (Iterator<String> var4 = getAdvancement().getCriteria().iterator(); var4.hasNext(); oldData = p.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria)) {
                criteria = var4.next();
                if (p.getAdvancementProgress(getAdvancement()).getDateAwarded(criteria) == null) {
                    return oldData;
                }
            }
        }

        return null;
    }

    public org.bukkit.advancement.Advancement getAdvancement() {
        return Bukkit.getAdvancement(getID());
    }

    public enum AdvancementBackground {
        ADVENTURE("minecraft:textures/gui/advancements/backgrounds/adventure.png"),
        END("minecraft:textures/gui/advancements/backgrounds/end.png"),
        HUSBANDRY("minecraft:textures/gui/advancements/backgrounds/husbandry.png"),
        NETHER("minecraft:textures/gui/advancements/backgrounds/nether.png"),
        STONE("minecraft:textures/gui/advancements/backgrounds/stone.png"),
        fromNamespace(null);

        public String str;

        AdvancementBackground(String str) {
            this.str = str;
        }

        public void fromNamespace(String string) {
            str = string;
        }
    }

    public enum FrameType {
        CHALLANGE("challenge"),
        GOAL("goal"),
        DEFAULT("task");

        private String str;

        FrameType(String str) {
            this.str = str;
        }

        public String getName() {
            return str;
        }
    }

}