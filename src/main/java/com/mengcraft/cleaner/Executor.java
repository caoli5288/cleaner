package com.mengcraft.cleaner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import com.mengcraft.cleaner.util.ArrayBuilder;
import com.mengcraft.cleaner.util.ArrayVector;
import com.mengcraft.cleaner.util.Option;
import com.mengcraft.cleaner.util.OptionDefine;
import com.mengcraft.cleaner.util.OptionParser;

public class Executor implements CommandExecutor {

    private final Main main;
    private final List<String> list;

    public Executor(Main main) {
        this.main = main;
        this.list = main.whiteList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
            String lable, String[] args) {
        ArrayVector<String> vec = new ArrayVector<>(args);
        if (vec.hasNext()) {
            String next = vec.next();
            if (next.equals("clean")) {
                sender.sendMessage(clean(vec));
            } else if (next.equals("find")) {
                sender.sendMessage(find(vec));
            }
        } else {
            ArrayBuilder<String> builder = new ArrayBuilder<>();
            builder.append("§6/cleaner clean ...");
            builder.append("§6/cleaner find ...");
            sender.sendMessage(builder.build(String.class));
        }
        return true;
    }

    private String[] find(ArrayVector<String> vec) {
        if (vec.hasNext()) {
            String next = vec.next();
            if (next.equals("chunk")) {
                return findChunk();
            }
        }
        return findInfomation();
    }

    private String[] findInfomation() {
        Map<String, Integer> map = new HashMap<>();

        for (World world : main.getServer().getWorlds()) {
            count(map, world.getEntities().toArray(new Entity[] {}));
        }

        return infomation(map);
    }

    private String[] findChunk() {

        Map<Chunk, Integer> map = new HashMap<>();

        for (World world : main.getServer().getWorlds()) {
            put(map, world);
        }

        Chunk chunk = select(map);

        if (chunk == null) {
            return new String[] { "§6没有找到任何实体信息" };
        }

        ArrayBuilder<String> builder = new ArrayBuilder<>();

        Entity[] entities = chunk.getEntities();

        Map<String, Integer> count = new HashMap<>();

        count(count, entities);

        Location loc = entities[0].getLocation();

        builder.append("§6实体数最多的区块位于:");
        builder.append("§6- World(" + chunk.getWorld().getName() + "),"
                + "Location(" + loc.getBlockX() + "," +
                loc.getBlockY() + "," +
                loc.getBlockZ() + ")");
        builder.append("§6该区块实体信息如下列：");

        String[] infomation = infomation(count);

        for (String string : infomation) {
            builder.append(string);
        }

        return builder.build(String.class);
    }

    private String[] infomation(Map<String, Integer> count) {
        ArrayBuilder<String> builder = new ArrayBuilder<>();

        for (Entry<String, Integer> entry : count.entrySet()) {
            builder.append("§6- Entity(" + entry.getKey() + ")," +
                    "Counts(" + entry.getValue() + ")");
        }

        builder.append("§6- Summation(" + sum(count.values())
                + ")");

        return builder.build(String.class);
    }

    private int sum(Collection<Integer> values) {
        int out = 0;
        for (Integer in : values) {
            out += in;
        }
        return out;
    }

    private void count(Map<String, Integer> map, Entity[] entities) {
        for (Entity entity : entities) {
            String type = entity.getType().name().toLowerCase();
            Integer value = map.get(type);
            if (value != null) {
                map.put(type, value + 1);
            } else {
                map.put(type, 1);
            }
        }
    }

    private Chunk select(Map<Chunk, Integer> map) {
        Entry<Chunk, Integer> output = null;
        for (Entry<Chunk, Integer> entry : map.entrySet()) {
            if (output == null || output.getValue() < entry.getValue()) {
                output = entry;
            }
        }
        return output != null ? output.getKey() : null;
    }

    private void put(Map<Chunk, Integer> map, World world) {
        for (Entity entity : world.getEntities()) {
            Chunk chunk = entity.getLocation().getChunk();
            Integer value = map.get(chunk);

            if (value != null) {
                map.put(chunk, value + 1);
            } else {
                map.put(chunk, 1);
            }
        }
    }

    private String clean(ArrayVector<String> vec) {
        if (vec.hasNext()) {

            OptionDefine[] defines = {
                    new OptionDefine("world", 1),
                    new OptionDefine("type", 1),
                    new OptionDefine("limit", 1)
            };

            String[] remain = getRemainArray(vec);
            Option option = new OptionParser(defines).parse(remain);

            if (option.others().size() > 0 || option.alones().size() > 0) {
                return "§4未知的命令参数！";
            }

            List<World> worlds;

            if (option.has("world")) {
                String name = option.get("world").argument();
                World world = main.getServer().getWorld(name);

                if (world == null) {
                    return "§4找不到" + name + "世界！";
                }

                worlds = new ArrayList<>();
                worlds.add(world);
            } else {
                worlds = main.getServer().getWorlds();
            }

            String type = option.has("type") ?
                    option.get("type").argument() :
                    null;

            int limit;

            if (option.has("limit")) {
                String line = option.get("limit").argument();
                try {
                    limit = new Integer(line);
                } catch (NumberFormatException e) {
                    return "§4密度限制输入有误";
                }
            } else {
                limit = 16;
            }

            return "§6清理掉了" +
                    clean(worlds, type, limit) + "个实体";
        } else {
            return "§6清理掉了" +
                    clean(main.getServer().getWorlds(), null, 16) + "个实体";
        }
    }

    private String[] getRemainArray(ArrayVector<String> vec) {
        ArrayBuilder<String> builder = new ArrayBuilder<>();
        while (vec.hasNext()) {
            builder.append(vec.next());
        }
        return builder.build(String.class);
    }

    private int clean(List<World> worlds, String type, int limit) {
        List<Entity> list = new ArrayList<>();

        for (World world : worlds) {
            putIfType(list, world, type);
        }

        int before = list.size();
        Iterator<Entity> iterat = list.iterator();

        Entity entity;
        List<Entity> near;

        while (iterat.hasNext()) {

            entity = iterat.next();
            near = entity.getNearbyEntities(8, 8, 8);

            if (type != null) {
                cutIfType(near, type);
            }

            if (near.size() + 1 > limit) {
                entity.remove();
                iterat.remove();
            }
        }

        return before - list.size();
    }

    private void cutIfType(List<Entity> near, String type) {
        Iterator<Entity> iterat = near.iterator();

        Entity entity;
        String target;

        while (iterat.hasNext()) {
            entity = iterat.next();
            target = entity.getType().name();
            // Remove if not equals type.
            if (!target.equalsIgnoreCase(type)) {
                iterat.remove();
            }

        }
    }

    private void putIfType(List<Entity> list, World world, String type) {
        for (Entity entity : world.getEntities()) {
            String name = entity.getType().name();
            boolean isEntity = name.equals("PLAYER") ? false :
                    type != null ? type.toUpperCase().equals(name) :
                            !list.contains(name);
            if (isEntity) {
                list.add(entity);
            }
        }
    }

}
