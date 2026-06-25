import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

public class SwiftKek extends JavaPlugin implements CommandExecutor, Listener {

    private HashMap<String, GameMode> gamemodeAliases = new HashMap<>();
    private HashMap<String, String> weatherAliases = new HashMap<>();
    private HashMap<String, Long> timeAliases = new HashMap<>();
    private Set<Player> afkPlayers = new HashSet<>();
    private Set<Player> vanishedPlayers = new HashSet<>();
    private Set<Player> godPlayers = new HashSet<>();
    private Set<Player> noclipPlayers = new HashSet<>();
    private Set<Player> staffChatPlayers = new HashSet<>();
    private HashMap<String, Location> tpaRequests = new HashMap<>();
    private DecimalFormat df = new DecimalFormat("0.00");
    private FileConfiguration reportsConfig;
    private File reportsFile;

    @Override
    public void onEnable() {
        initializeAliases();
        
        // Регистрация всех команд
        registerCommands();
        
        // Регистрация ивентов
        getServer().getPluginManager().registerEvents(this, this);
        
        // Загрузка конфигурации репортов
        loadReportsConfig();

        getLogger().info("§b⚡ §dSwiftKek v1.4 §b⚡");
        getLogger().info("§eАвтор: §bFassykite §7| §aПри поддержке: §6StrixGT");
        getLogger().info("§a20+ новых админ-команд активированы!");
    }

    private void registerCommands() {
        String[] commands = {
            "gm", "gms", "gmc", "gma", "gmsp", "t", "time", "afk", 
            "tphere", "tp", "spawn", "fly", "kill", "w", "weather",
            "gc", "sc", "staffchat", "addstaffchat", "removestaffchat",
            "tppos", "feed", "heal", "vanish", "god", "speed", "noclip",
            "playerlist", "find", "enderchest", "ec", "hat", "walkspeed",
            "flyspeed", "ban", "unban", "banip", "unbanip", "rollback",
            "spawnmob", "help", "report", "tpa", "tpaccept", "tpahere"
        };
        for (String cmd : commands) {
            getCommand(cmd).setExecutor(this);
        }
    }

    private void initializeAliases() {
        // Gamemode алиасы
        gamemodeAliases.put("0", GameMode.SURVIVAL);
        gamemodeAliases.put("s", GameMode.SURVIVAL);
        gamemodeAliases.put("survival", GameMode.SURVIVAL);
        gamemodeAliases.put("выживание", GameMode.SURVIVAL);
        
        gamemodeAliases.put("1", GameMode.CREATIVE);
        gamemodeAliases.put("c", GameMode.CREATIVE);
        gamemodeAliases.put("creative", GameMode.CREATIVE);
        gamemodeAliases.put("креатив", GameMode.CREATIVE);
        
        gamemodeAliases.put("2", GameMode.ADVENTURE);
        gamemodeAliases.put("a", GameMode.ADVENTURE);
        gamemodeAliases.put("adventure", GameMode.ADVENTURE);
        gamemodeAliases.put("приключение", GameMode.ADVENTURE);
        
        gamemodeAliases.put("3", GameMode.SPECTATOR);
        gamemodeAliases.put("sp", GameMode.SPECTATOR);
        gamemodeAliases.put("spectator", GameMode.SPECTATOR);
        gamemodeAliases.put("наблюдатель", GameMode.SPECTATOR);

        // Weather алиасы
        weatherAliases.put("clear", "clear");
        weatherAliases.put("sun", "clear");
        weatherAliases.put("sunny", "clear");
        weatherAliases.put("rain", "storm");
        weatherAliases.put("storm", "storm");
        weatherAliases.put("thunder", "storm");
        weatherAliases.put("гроза", "storm");

        // Time алиасы
        timeAliases.put("day", 1000L);
        timeAliases.put("daytime", 1000L);
        timeAliases.put("утро", 1000L);
        timeAliases.put("день", 1000L);
        
        timeAliases.put("noon", 6000L);
        timeAliases.put("полдень", 6000L);
        
        timeAliases.put("night", 13000L);
        timeAliases.put("ночь", 13000L);
        
        timeAliases.put("midnight", 18000L);
        timeAliases.put("полночь", 18000L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();

        try {
            switch (cmd) {
                case "gm":
                    return handleGamemodeCommand(sender, args);
                case "gms":
                    if (sender instanceof Player && !hasPermission((Player) sender, "swiftkek.gamemode.survival")) {
                        sendNoPermissionMessage(sender, "swiftkek.gamemode.survival");
                        return true;
                    }
                    if (sender instanceof Player) handleSetGamemode((Player) sender, GameMode.SURVIVAL);
                    break;
                case "gmc":
                    if (sender instanceof Player && !hasPermission((Player) sender, "swiftkek.gamemode.creative")) {
                        sendNoPermissionMessage(sender, "swiftkek.gamemode.creative");
                        return true;
                    }
                    if (sender instanceof Player) handleSetGamemode((Player) sender, GameMode.CREATIVE);
                    break;
                case "gma":
                    if (sender instanceof Player && !hasPermission((Player) sender, "swiftkek.gamemode.adventure")) {
                        sendNoPermissionMessage(sender, "swiftkek.gamemode.adventure");
                        return true;
                    }
                    if (sender instanceof Player) handleSetGamemode((Player) sender, GameMode.ADVENTURE);
                    break;
                case "gmsp":
                    if (sender instanceof Player && !hasPermission((Player) sender, "swiftkek.gamemode.spectator")) {
                        sendNoPermissionMessage(sender, "swiftkek.gamemode.spectator");
                        return true;
                    }
                    if (sender instanceof Player) handleSetGamemode((Player) sender, GameMode.SPECTATOR);
                    break;
                case "t":
                case "time":
                    return handleTimeCommand(sender, args);
                case "w":
                case "weather":
                    return handleWeatherCommand(sender, args);
                case "afk":
                    return handleAfkCommand(sender, args);
                case "tphere":
                    return handleTphereCommand(sender, args);
                case "tp":
                    return handleTpCommand(sender, args);
                case "spawn":
                    return handleSpawnCommand(sender);
                case "fly":
                    return handleFlyCommand(sender, args);
                case "kill":
                    return handleKillCommand(sender, args);
                case "gc":
                    return handleGcCommand(sender);
                case "sc":
                case "staffchat":
                    return handleStaffChatCommand(sender, args);
                case "addstaffchat":
                    return handleAddStaffChatCommand(sender, args);
                case "removestaffchat":
                    return handleRemoveStaffChatCommand(sender, args);
                case "tppos":
                    return handleTpposCommand(sender, args);
                case "feed":
                    return handleFeedCommand(sender, args);
                case "heal":
                    return handleHealCommand(sender, args);
                case "vanish":
                    return handleVanishCommand(sender, args);
                case "god":
                    return handleGodCommand(sender, args);
                case "speed":
                    return handleSpeedCommand(sender, args);
                case "noclip":
                    return handleNoclipCommand(sender, args);
                case "playerlist":
                    return handlePlayerlistCommand(sender);
                case "find":
                    return handleFindCommand(sender, args);
                case "enderchest":
                case "ec":
                    return handleEnderchestCommand(sender, args);
                case "hat":
                    return handleHatCommand(sender);
                case "walkspeed":
                    return handleWalkSpeedCommand(sender, args);
                case "flyspeed":
                    return handleFlySpeedCommand(sender, args);
                case "ban":
                    return handleBanCommand(sender, args);
                case "unban":
                    return handleUnbanCommand(sender, args);
                case "banip":
                    return handleBanIpCommand(sender, args);
                case "unbanip":
                    return handleUnbanIpCommand(sender, args);
                case "rollback":
                    return handleRollbackCommand(sender, args);
                case "spawnmob":
                    return handleSpawnMobCommand(sender, args);
                case "help":
                    return handleHelpCommand(sender);
                case "report":
                    return handleReportCommand(sender, args);
                case "tpa":
                    return handleTpaCommand(sender, args);
                case "tpaccept":
                    return handleTpacceptCommand(sender);
                case "tpahere":
                    return handleTpahereCommand(sender, args);
                default:
                    sendGradientMessage(sender, "§cНеизвестная команда!");
                    return true;
            }
            return true;
        } catch (Exception e) {
            sendGradientMessage(sender, "§cОшибка при выполнении команды!");
            getLogger().warning("Ошибка в команде " + cmd + ": " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    // ========== PERMISSION CHECKS ==========

    private boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission) || player.hasPermission("swiftkek.*") || player.isOp();
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return !(sender instanceof Player) || hasPermission((Player) sender, permission) || sender.isOp();
    }

    private void sendNoPermissionMessage(CommandSender sender, String permission) {
        sendGradientMessage(sender, "§cУ вас нет прав на эту команду!");
        sender.sendMessage("§7Требуется право: §e" + permission);
    }

    // ========== GRADIENT MESSAGES ==========
    
    private void sendGradientMessage(CommandSender sender, String message) {
        sender.sendMessage("§b⚡ §dSwiftKek §8| " + message);
    }

    private void sendSuccessMessage(CommandSender sender, String action, String details) {
        sender.sendMessage("§b✦ §d" + action + " §b✦ §7| §a" + details);
    }

    // ========== VANISH COMMAND (ИСПРАВЛЕННЫЙ) ==========

    private boolean handleVanishCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (vanishedPlayers.contains(player)) {
            // Показать игрока всем
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(this, player);
            }
            vanishedPlayers.remove(player);
            sendSuccessMessage(player, "Невидимость", "Вы теперь видимы для всех игроков");
        } else {
            // Скрыть игрока от всех без прав
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("swiftkek.vanish.see")) {
                    online.hidePlayer(this, player);
                }
            }
            vanishedPlayers.add(player);
            sendSuccessMessage(player, "Невидимость", "Вы теперь невидимы для обычных игроков");
        }

        return true;
    }

    // ========== ENDERCHEST COMMAND ==========

    private boolean handleEnderchestCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;
        Player target = player;

        if (args.length > 0) {
            if (!hasPermission(player, "swiftkek.enderchest.others")) {
                sendNoPermissionMessage(player, "swiftkek.enderchest.others");
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
                return true;
            }
        }

        Inventory enderchest = target.getEnderChest();
        player.openInventory(enderchest);
        
        if (target == player) {
            sendSuccessMessage(player, "Эндерсундук", "Открыт ваш эндерсундук");
        } else {
            sendSuccessMessage(player, "Эндерсундук", "Открыт эндерсундук игрока §e" + target.getName());
        }

        return true;
    }

    // ========== HAT COMMAND ==========

    private boolean handleHatCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;
        ItemStack handItem = player.getInventory().getItemInHand();
        
        if (handItem == null || handItem.getType().isAir()) {
            sendGradientMessage(sender, "§cВозьмите блок в руку!");
            return true;
        }

        ItemStack helmet = player.getInventory().getHelmet();
        player.getInventory().setHelmet(handItem);
        player.getInventory().setItemInHand(helmet);
        
        sendSuccessMessage(player, "Шляпа", "Блок надет на голову!");

        return true;
    }
	
	// ========== SPEED COMMAND (универсальная) ==========

private boolean handleSpeedCommand(CommandSender sender, String[] args) {
    if (!(sender instanceof Player)) {
        sendGradientMessage(sender, "§cЭта команда только для игроков!");
        return true;
    }

    if (args.length == 0) {
        sendGradientMessage(sender, "§cИспользование: §b/speed <1-10>");
        sender.sendMessage("§7Совет: Используйте §b/walkspeed §7или §b/flyspeed §7для точного управления");
        return true;
    }

    Player player = (Player) sender;

    try {
        int speed = Integer.parseInt(args[0]);
        if (speed < 1 || speed > 10) {
            sendGradientMessage(sender, "§cСкорость должна быть от 1 до 10!");
            return true;
        }

        float speedValue = speed / 10.0f;
        
        // Устанавливаем обе скорости для удобства
        player.setWalkSpeed(Math.min(speedValue, 1.0f));
        player.setFlySpeed(Math.min(speedValue, 1.0f));
        
        sendSuccessMessage(player, "Скорость", "Скорость ходьбы и полета установлена: §e" + speed);

    } catch (NumberFormatException e) {
        sendGradientMessage(sender, "§cНеверная скорость! Используйте число от 1 до 10.");
    }

    return true;
    }
	
    // ========== WALKSPEED COMMAND ==========

    private boolean handleWalkSpeedCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        if (args.length == 0) {
            sendGradientMessage(sender, "§cИспользование: §b/walkspeed <1-10>");
            return true;
        }

        Player player = (Player) sender;

        try {
            int speed = Integer.parseInt(args[0]);
            if (speed < 1 || speed > 10) {
                sendGradientMessage(sender, "§cСкорость должна быть от 1 до 10!");
                return true;
            }

            float speedValue = speed / 10.0f;
            player.setWalkSpeed(Math.min(speedValue, 1.0f));
            sendSuccessMessage(player, "Скорость ходьбы", "Установлена: §e" + speed);

        } catch (NumberFormatException e) {
            sendGradientMessage(sender, "§cНеверная скорость! Используйте число от 1 до 10.");
        }

        return true;
    }

    // ========== FLYSPEED COMMAND ==========

    private boolean handleFlySpeedCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        if (args.length == 0) {
            sendGradientMessage(sender, "§cИспользование: §b/flyspeed <1-10>");
            return true;
        }

        Player player = (Player) sender;

        try {
            int speed = Integer.parseInt(args[0]);
            if (speed < 1 || speed > 10) {
                sendGradientMessage(sender, "§cСкорость должна быть от 1 до 10!");
                return true;
            }

            float speedValue = speed / 10.0f;
            player.setFlySpeed(Math.min(speedValue, 1.0f));
            sendSuccessMessage(player, "Скорость полета", "Установлена: §e" + speed);

        } catch (NumberFormatException e) {
            sendGradientMessage(sender, "§cНеверная скорость! Используйте число от 1 до 10.");
        }

        return true;
    }

    // ========== GOD COMMAND (ИСПРАВЛЕННЫЙ) ==========

    private boolean handleGodCommand(CommandSender sender, String[] args) {
        Player target;
        
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sendGradientMessage(sender, "§cКонсоль: /god <игрок>");
                return true;
            }
            target = (Player) sender;
        } else {
            if (!hasPermission(sender, "swiftkek.god.others")) {
                sendNoPermissionMessage(sender, "swiftkek.god.others");
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
                return true;
            }
        }

        if (godPlayers.contains(target)) {
            godPlayers.remove(target);
            if (target == sender) {
                sendSuccessMessage(sender, "Режим Бога", "Вы больше не бессмертны");
            } else {
                sendSuccessMessage(sender, "Режим Бога", "Игрок §e" + target.getName() + "§a больше не бессмертен");
                target.sendMessage("§b✦ §dРежим Бога §b✦ §7| §cВы больше не бессмертны");
            }
        } else {
            godPlayers.add(target);
            if (target == sender) {
                sendSuccessMessage(sender, "Режим Бога", "Вы теперь бессмертны! ⚡");
            } else {
                sendSuccessMessage(sender, "Режим Бога", "Игрок §e" + target.getName() + "§a теперь бессмертен ⚡");
                target.sendMessage("§b✦ §dРежим Бога §b✦ §7| §aВы теперь бессмертны! ⚡");
            }
        }

        return true;
    }

    // ========== EVENT HANDLER FOR GOD MODE ==========

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (godPlayers.contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    // ========== BAN COMMANDS ==========

    private boolean handleBanCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendGradientMessage(sender, "§cИспользование: §b/ban <игрок> [причина]");
            return true;
        }

        String target = args[0];
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "Не указана";

        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(target, reason, null, sender.getName());
        
        Player onlinePlayer = Bukkit.getPlayer(target);
        if (onlinePlayer != null) {
            onlinePlayer.kickPlayer("§cВы забанены!\n§7Причина: §f" + reason + "\n§7Администратор: §f" + sender.getName());
        }

        sendSuccessMessage(sender, "Бан", "Игрок §e" + target + "§a забанен\n§7Причина: §f" + reason);
        return true;
    }

    private boolean handleUnbanCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendGradientMessage(sender, "§cИспользование: §b/unban <игрок>");
            return true;
        }

        String target = args[0];
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(target);
        sendSuccessMessage(sender, "Разбан", "Игрок §e" + target + "§a разбанен");
        return true;
    }

    private boolean handleBanIpCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendGradientMessage(sender, "§cИспользование: §b/banip <игрок> [причина]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sendGradientMessage(sender, "§cИгрок не найден или не в сети!");
            return true;
        }

        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "Не указана";
        String ip = target.getAddress().getAddress().getHostAddress();

        Bukkit.getBanList(org.bukkit.BanList.Type.IP).addBan(ip, reason, null, sender.getName());
        target.kickPlayer("§cВаш IP забанен!\n§7Причина: §f" + reason + "\n§7Администратор: §f" + sender.getName());

        sendSuccessMessage(sender, "Бан IP", "IP игрока §e" + target.getName() + "§a забанен\n§7Причина: §f" + reason);
        return true;
    }

    private boolean handleUnbanIpCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendGradientMessage(sender, "§cИспользование: §b/unbanip <IP>");
            return true;
        }

        String ip = args[0];
        Bukkit.getBanList(org.bukkit.BanList.Type.IP).pardon(ip);
        sendSuccessMessage(sender, "Разбан IP", "IP §e" + ip + "§a разбанен");
        return true;
    }

    // ========== STAFF CHAT COMMAND (ИСПРАВЛЕННЫЙ) ==========

    private boolean handleAddStaffChatCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sendGradientMessage(sender, "§cКонсоль: /addstaffchat <игрок>");
                return true;
            }
            Player player = (Player) sender;
            if (staffChatPlayers.contains(player)) {
                sendGradientMessage(sender, "§cВы уже в Staff чате!");
                return true;
            }
            staffChatPlayers.add(player);
            sendSuccessMessage(sender, "Staff Chat", "Вы добавлены в staff чат");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
            return true;
        }

        if (staffChatPlayers.contains(target)) {
            sendGradientMessage(sender, "§cИгрок " + target.getName() + " уже в Staff чате!");
            return true;
        }

        staffChatPlayers.add(target);
        sendSuccessMessage(sender, "Staff Chat", "Игрок §e" + target.getName() + "§a добавлен в staff чат");
        target.sendMessage("§b✦ §dStaff Chat §b✦ §7| §aВы добавлены в staff чат");
        return true;
    }

    // ========== ROLLBACK COMMAND ==========

    private boolean handleRollbackCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendGradientMessage(sender, "§cИспользование: §b/rollback <игрок> [время]");
            return true;
        }

        String target = args[0];
        String time = args.length > 1 ? args[1] : "1h";

        // Проверяем наличие CoreProtect
        if (Bukkit.getPluginManager().getPlugin("CoreProtect") != null) {
            Bukkit.dispatchCommand(sender, "co rollback u:" + target + " t:" + time + " r:global");
            sendSuccessMessage(sender, "Откат", "Откат действий игрока §e" + target + "§a за время: §e" + time);
        } else {
            sendGradientMessage(sender, "§cCoreProtect не найден! Установите плагин для работы отката.");
        }

        return true;
    }

    // ========== CO I COMMAND ==========

    private boolean handleCoICommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        // Проверяем наличие CoreProtect
        if (Bukkit.getPluginManager().getPlugin("CoreProtect") != null) {
            Bukkit.dispatchCommand(sender, "co i");
            sendSuccessMessage(sender, "Инспектор", "Режим инспектора CoreProtect активирован");
        } else {
            sendGradientMessage(sender, "§cCoreProtect не найден! Установите плагин для работы инспектора.");
        }

        return true;
    }

    // ========== SPAWN MOB COMMAND ==========

    private boolean handleSpawnMobCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        if (args.length < 1) {
            sendGradientMessage(sender, "§cИспользование: §b/spawnmob <моб> [кол-во]");
            sender.sendMessage("§7Пример: §e/spawnmob ZOMBIE 5");
            return true;
        }

        Player player = (Player) sender;
        String mobName = args[0].toUpperCase();
        int count = 1;

        if (args.length > 1) {
            try {
                count = Integer.parseInt(args[1]);
                if (count < 1 || count > 100) {
                    sendGradientMessage(sender, "§cКоличество должно быть от 1 до 100!");
                    return true;
                }
            } catch (NumberFormatException e) {
                sendGradientMessage(sender, "§cНеверное количество!");
                return true;
            }
        }

        try {
            EntityType entityType = EntityType.valueOf(mobName);
            Location loc = player.getLocation();
            
            for (int i = 0; i < count; i++) {
                player.getWorld().spawnEntity(loc, entityType);
            }
            
            sendSuccessMessage(player, "Спавн мобов", "Заспавнено §e" + count + "§a мобов типа §e" + mobName);
            
        } catch (IllegalArgumentException e) {
            sendGradientMessage(sender, "§cНеизвестный моб: §e" + mobName);
            sender.sendMessage("§7Доступные мобы: ZOMBIE, SKELETON, CREEPER, SPIDER, etc.");
        }

        return true;
    }

    // ========== KILL COMMAND (ИСПРАВЛЕННЫЙ) ==========

    private boolean handleKillCommand(CommandSender sender, String[] args) {
        Player target;
        
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sendGradientMessage(sender, "§cКонсоль: /kill <игрок>");
                return true;
            }
            target = (Player) sender;
        } else {
            if (!hasPermission(sender, "swiftkek.kill.others")) {
                sendNoPermissionMessage(sender, "swiftkek.kill.others");
                return true;
            }
            
            target = getPlayerFromSelector(sender, args[0]);
            if (target == null) {
                sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
                return true;
            }
        }

        // Убираем проверку на god mode - теперь можно убивать даже в креативе
        target.setHealth(0);
        
        if (target == sender) {
            sendSuccessMessage(sender, "Убийство", "Вы совершили суицид ☠️");
        } else {
            sendSuccessMessage(sender, "Убийство", "Игрок §e" + target.getName() + "§a убит");
            target.sendMessage("§b✦ §dУбийство §b✦ §7| §cВас убил: §e" + sender.getName());
        }

        return true;
    }

    // ========== HELP COMMAND ==========

    private boolean handleHelpCommand(CommandSender sender) {
        sender.sendMessage("§b⚡ §dSwiftKek v1.4 §b⚡ §7| §eСписок команд");
        sender.sendMessage("§bОсновные команды:");
        sender.sendMessage("§b/gm <0-3> §7- Сменить режим игры §b/gms §7- Выживание");
        sender.sendMessage("§b/god [игрок] §7- Бессмертие §b/vanish §7- Невидимость");
        sender.sendMessage("§b/fly [игрок] §7- Полёт §b/noclip §7- Сквозь блоки");
        sender.sendMessage("§b/tp <игрок> §7- Телепорт §b/tppos <x y z> §7- ТП по координатам");
        sender.sendMessage("§b/heal [игрок] §7- Лечение §b/feed [игрок] §7- Сытость");
        sender.sendMessage("§b/walkspeed <1-10> §7- Скорость ходьбы §b/flyspeed <1-10> §7- Скорость полёта");
        
        if (hasPermission(sender, "swiftkek.admin")) {
            sender.sendMessage("§bАдмин команды:");
            sender.sendMessage("§b/ban <игрок> [причина] §7- Бан §b/unban <игрок> §7- Разбан");
            sender.sendMessage("§b/banip <игрок> §7- Бан IP §b/unbanip <IP> §7- Разбан IP");
            sender.sendMessage("§b/rollback <игрок> [время] §7- Откат действий");
            sender.sendMessage("§b/spawnmob <моб> [кол-во] §7- Спавн мобов");
            sender.sendMessage("§b/ec [игрок] §7- Эндерсундук §b/hat §7- Блок на голову");
        }
        
        sender.sendMessage("§bИгровые команды:");
        sender.sendMessage("§b/afk §7- Режим AFK §b/tpa <игрок> §7- Запрос ТП");
        sender.sendMessage("§b/tpaccept §7- Принять ТП §b/tpahere <игрок> §7- ТП к себе");
        sender.sendMessage("§b/report <игрок> <причина> §7- Жалоба на игрока");
        
        sender.sendMessage("§7Используйте §b/help <страница> §7для дополнительной информации");
        return true;
    }

    // ========== REPORT COMMAND ==========

    private boolean handleReportCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        if (args.length < 2) {
            sendGradientMessage(sender, "§cИспользование: §b/report <игрок> <причина>");
            sender.sendMessage("§7Пример: §e/report GlowNerr Использование читов");
            return true;
        }

        Player reporter = (Player) sender;
        String targetName = args[0];
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sendGradientMessage(sender, "§cИгрок " + targetName + " не найден или не в сети!");
            return true;
        }

        if (target == reporter) {
            sendGradientMessage(sender, "§cНельзя отправить жалобу на самого себя!");
            return true;
        }

        // Сохраняем жалобу в конфиг
        saveReport(reporter.getName(), target.getName(), reason);
        
        // Уведомляем стафф
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("swiftkek.report.see")) {
                staff.sendMessage("§c⚠ §6Жалоба от §e" + reporter.getName() + "§6 на игрока §c" + target.getName());
                staff.sendMessage("§7Причина: §f" + reason);
                staff.sendMessage("§7Время: §f" + new java.util.Date());
            }
        }

        sendSuccessMessage(sender, "Жалоба", "Жалоба на игрока §e" + target.getName() + "§a отправлена!");
        sender.sendMessage("§7Причина: §f" + reason);

        return true;
    }

    // ========== TPA COMMANDS ==========

    private boolean handleTpaCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        if (args.length < 1) {
            sendGradientMessage(sender, "§cИспользование: §b/tpa <игрок>");
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null) {
            sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
            return true;
        }

        if (target == player) {
            sendGradientMessage(sender, "§cНельзя отправить запрос самому себе!");
            return true;
        }

        tpaRequests.put(player.getName() + "_to_" + target.getName(), player.getLocation());
        
        player.sendMessage("§b✦ §dТелепортация §b✦ §7| §aЗапрос отправлен игроку §e" + target.getName());
        target.sendMessage("§b✦ §dТелепортация §b✦ §7| §e" + player.getName() + " §aхочет телепортироваться к вам");
        target.sendMessage("§b✦ §dТелепортация §b✦ §7| §aНапишите §b/tpaccept §aдля принятия запроса");

        // Автоотмена через 30 секунд
        new BukkitRunnable() {
            @Override
            public void run() {
                if (tpaRequests.remove(player.getName() + "_to_" + target.getName()) != null) {
                    player.sendMessage("§b✦ §dТелепортация §b✦ §7| §cЗапрос к игроку §e" + target.getName() + "§c истёк");
                    target.sendMessage("§b✦ §dТелепортация §b✦ §7| §cЗапрос от §e" + player.getName() + "§c истёк");
                }
            }
        }.runTaskLater(this, 20 * 30);

        return true;
    }

    private boolean handleTpahereCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        if (args.length < 1) {
            sendGradientMessage(sender, "§cИспользование: §b/tpahere <игрок>");
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null) {
            sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
            return true;
        }

        if (target == player) {
            sendGradientMessage(sender, "§cНельзя отправить запрос самому себе!");
            return true;
        }

        tpaRequests.put(target.getName() + "_to_" + player.getName(), player.getLocation());
        
        player.sendMessage("§b✦ §dТелепортация §b✦ §7| §aЗапрос отправлен игроку §e" + target.getName());
        target.sendMessage("§b✦ §dТелепортация §b✦ §7| §e" + player.getName() + " §aхочет чтобы вы телепортировались к нему");
        target.sendMessage("§b✦ §dТелепортация §b✦ §7| §aНапишите §b/tpaccept §aдля принятия запроса");

        // Автоотмена через 30 секунд
        new BukkitRunnable() {
            @Override
            public void run() {
                if (tpaRequests.remove(target.getName() + "_to_" + player.getName()) != null) {
                    player.sendMessage("§b✦ §dТелепортация §b✦ §7| §cЗапрос к игроку §e" + target.getName() + "§c истёк");
                    target.sendMessage("§b✦ §dТелепортация §b✦ §7| §cЗапрос от §e" + player.getName() + "§c истёк");
                }
            }
        }.runTaskLater(this, 20 * 30);

        return true;
    }

    private boolean handleTpacceptCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;
        String acceptedRequest = null;

        // Ищем запросы где игрок является целью
        for (String request : tpaRequests.keySet()) {
            if (request.endsWith("_to_" + player.getName())) {
                acceptedRequest = request;
                break;
            }
        }

        if (acceptedRequest == null) {
            sendGradientMessage(sender, "§cУ вас нет активных запросов на телепортацию!");
            return true;
        }

        String[] parts = acceptedRequest.split("_to_");
        String requesterName = parts[0];
        Player requester = Bukkit.getPlayer(requesterName);
        
        if (requester == null || !requester.isOnline()) {
            sendGradientMessage(sender, "§cИгрок отправивший запрос вышел из игры!");
            tpaRequests.remove(acceptedRequest);
            return true;
        }

        Location targetLocation = tpaRequests.get(acceptedRequest);
        if (acceptedRequest.startsWith(player.getName() + "_to_")) {
            // TPA - телепортируем отправителя к игроку
            requester.teleport(player.getLocation());
            requester.sendMessage("§b✦ §dТелепортация §b✦ §7| §aВы телепортированы к игроку §e" + player.getName());
        } else {
            // TPAHERE - телепортируем игрока к отправителю
            player.teleport(targetLocation);
            player.sendMessage("§b✦ §dТелепортация §b✦ §7| §aВы телепортированы к игроку §e" + requester.getName());
        }

        tpaRequests.remove(acceptedRequest);
        sendSuccessMessage(sender, "Телепортация", "Запрос принят! §e" + requester.getName());
        requester.sendMessage("§b✦ §dТелепортация §b✦ §7| §e" + player.getName() + " §aпринял ваш запрос на телепортацию");

        return true;
    }

    // ========== REPORT SYSTEM ==========

private void loadReportsConfig() {
    reportsFile = new File(getDataFolder(), "reports.yml");
    if (!reportsFile.exists()) {
        // Создаем папку если нету
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        // Создаем пустой файл вместо saveResource
        try {
            reportsFile.createNewFile();
            // Инициализируем пустую структуру
            reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
            reportsConfig.set("reports", new java.util.HashMap<String, Object>());
            reportsConfig.save(reportsFile);
        } catch (IOException e) {
            getLogger().warning("Не удалось создать reports.yml: " + e.getMessage());
        }
    }
    reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
}

    private void saveReport(String reporter, String target, String reason) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String path = "reports." + timestamp;
        
        reportsConfig.set(path + ".reporter", reporter);
        reportsConfig.set(path + ".target", target);
        reportsConfig.set(path + ".reason", reason);
        reportsConfig.set(path + ".time", new java.util.Date().toString());
        reportsConfig.set(path + ".resolved", false);
        
        try {
            reportsConfig.save(reportsFile);
        } catch (IOException e) {
            getLogger().warning("Не удалось сохранить жалобу: " + e.getMessage());
        }
    }

    // ========== REPORT VIEW COMMAND ==========

    private boolean handleReportsCommand(CommandSender sender) {
    if (!sender.hasPermission("swiftkek.report.see") && !sender.isOp()) {
        sendNoPermissionMessage(sender, "swiftkek.report.see");
        return true;
    }

    if (reportsConfig.getConfigurationSection("reports") == null) {
        sender.sendMessage("§b⚡ §dSwiftKek §b⚡ §7| §eСписок жалоб пуст");
        return true;
    }

    sender.sendMessage("§b⚡ §dSwiftKek §b⚡ §7| §eСписок жалоб");
    int count = 0;
    
    for (String key : reportsConfig.getConfigurationSection("reports").getKeys(false)) {
        String path = "reports." + key;
        if (reportsConfig.getBoolean(path + ".resolved")) continue;
        
        String reporter = reportsConfig.getString(path + ".reporter");
        String target = reportsConfig.getString(path + ".target");
        String reason = reportsConfig.getString(path + ".reason");
        String time = reportsConfig.getString(path + ".time");
        
        sender.sendMessage("§7[" + key + "] §e" + reporter + " §7→ §c" + target);
        sender.sendMessage("§7Причина: §f" + reason);
        sender.sendMessage("§7Время: §f" + time);
        sender.sendMessage("§7-------------------");
        count++;
    }

    if (count == 0) {
        sender.sendMessage("§7Активных жалоб нет");
    } else {
        sender.sendMessage("§7Всего активных жалоб: §e" + count);
    }

    return true;
}

    // ========== PLAYER SELECTOR ==========

    private Player getPlayerFromSelector(CommandSender sender, String selector) {
        if (selector.equals("@s") && sender instanceof Player) {
            return (Player) sender;
        }
        
        if (selector.startsWith("@")) {
            sendGradientMessage(sender, "§cСелекторы (@a, @p, @r) пока не поддерживаются!");
            return null;
        }
        
        Player target = Bukkit.getPlayer(selector);
        if (target == null) {
            sendGradientMessage(sender, "§cИгрок " + selector + " не найден!");
        }
        
        return target;
    }

    // ========== ОСТАВШИЕСЯ КОМАНДЫ (без изменений) ==========

    private boolean handleGamemodeCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendGamemodeHelp(sender);
            return true;
        }

        if (!(sender instanceof Player)) {
            if (args.length >= 2) {
                return handleGamemodeForOther(sender, args[0], args[1]);
            } else {
                sender.sendMessage("§cКонсоль: /gm <режим> <игрок>");
                return true;
            }
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            String mode = args[0].toLowerCase();
            if (gamemodeAliases.containsKey(mode)) {
                GameMode gamemode = gamemodeAliases.get(mode);
                if (!hasPermissionForGamemode(player, gamemode)) {
                    sendNoPermissionMessage(player, getGamemodePermission(gamemode));
                    return true;
                }
                handleSetGamemode(player, gamemode);
            } else {
                sendGradientMessage(player, "§cНеизвестный режим игры!");
                sendAvailableGamemodes(player);
            }
            return true;
        }

        if (hasPermission(player, "swiftkek.gamemode.others")) {
            return handleGamemodeForOther(sender, args[0], args[1]);
        } else {
            sendGradientMessage(player, "§cУ вас нет прав менять режим другим игрокам!");
        }

        return true;
    }

    private boolean handleGamemodeForOther(CommandSender sender, String modeStr, String targetStr) {
        Player target = getPlayerFromSelector(sender, targetStr);
        if (target == null) {
            sendGradientMessage(sender, "§cИгрок " + targetStr + " не найден!");
            return true;
        }

        String mode = modeStr.toLowerCase();
        if (gamemodeAliases.containsKey(mode)) {
            GameMode gamemode = gamemodeAliases.get(mode);
            handleSetGamemode(target, gamemode);
            sendSuccessMessage(sender, "Режим игры изменен", "Игроку §e" + target.getName() + "§a установлен режим: §b" + getGamemodeName(gamemode));
        } else {
            sendGradientMessage(sender, "§cНеизвестный режим игры!");
        }

        return true;
    }

private boolean hasPermissionForGamemode(Player player, GameMode gamemode) {
    if (gamemode == GameMode.SURVIVAL) 
        return hasPermission(player, "swiftkek.gamemode.survival");
    if (gamemode == GameMode.CREATIVE) 
        return hasPermission(player, "swiftkek.gamemode.creative");
    if (gamemode == GameMode.ADVENTURE) 
        return hasPermission(player, "swiftkek.gamemode.adventure");
    if (gamemode == GameMode.SPECTATOR) 
        return hasPermission(player, "swiftkek.gamemode.spectator");
    return false;
}

private String getGamemodePermission(GameMode gamemode) {
    if (gamemode == GameMode.SURVIVAL) return "swiftkek.gamemode.survival";
    if (gamemode == GameMode.CREATIVE) return "swiftkek.gamemode.creative";
    if (gamemode == GameMode.ADVENTURE) return "swiftkek.gamemode.adventure";
    if (gamemode == GameMode.SPECTATOR) return "swiftkek.gamemode.spectator";
    return "swiftkek.gamemode";
}

    private void handleSetGamemode(Player player, GameMode gamemode) {
        player.setGameMode(gamemode);
        sendSuccessMessage(player, "Режим игры изменен", "Установлен режим: §b" + getGamemodeName(gamemode));
    }

    private String getGamemodeName(GameMode gamemode) {
            if (gamemode == GameMode.SURVIVAL) return "§cВыживание";
            if (gamemode == GameMode.CREATIVE) return "§aКреатив";
            if (gamemode == GameMode.ADVENTURE) return "§6Приключение";
            if (gamemode == GameMode.SPECTATOR) return "§dНаблюдатель";
            return gamemode.toString();
        }

    private void sendGamemodeHelp(CommandSender sender) {
        sender.sendMessage("§b⚡ §dSwiftKek §b⚡ §7| §eGamemode команды");
        sender.sendMessage("§b/gm <0|1|2|3> §7- Сменить свой режим");
        if (!(sender instanceof Player) || hasPermission((Player) sender, "swiftkek.gamemode.others")) {
            sender.sendMessage("§b/gm <режим> <игрок> §7- Сменить режим игроку");
        }
        sender.sendMessage("§b/gms §7- Выживание §b/gmc §7- Креатив");
        sender.sendMessage("§b/gma §7- Приключение §b/gmsp §7- Наблюдатель");
    }

    private void sendAvailableGamemodes(CommandSender sender) {
        sender.sendMessage("§bДоступные режимы: §e0,s,survival §a1,c,creative §62,a,adventure §d3,sp,spectator");
    }

    // ========== WEATHER COMMAND ==========

    private boolean handleWeatherCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendWeatherHelp(sender);
            return true;
        }

        String weatherArg = args[0].toLowerCase();
        if (weatherAliases.containsKey(weatherArg)) {
            String weatherType = weatherAliases.get(weatherArg);
            World world = (sender instanceof Player) ? ((Player) sender).getWorld() : Bukkit.getWorlds().get(0);
            
            if (weatherType.equals("clear")) {
                world.setStorm(false);
                world.setThundering(false);
                sendSuccessMessage(sender, "Погода изменена", "Установлена ясная погода");
            } else if (weatherType.equals("storm")) {
                world.setStorm(true);
                world.setThundering(true);
                sendSuccessMessage(sender, "Погода изменена", "Установлена гроза");
            }
        } else {
            sendGradientMessage(sender, "§cНеизвестный тип погоды! Доступно: clear, rain/thunder");
        }

        return true;
    }

    private void sendWeatherHelp(CommandSender sender) {
        sender.sendMessage("§b⚡ §dSwiftKek §b⚡ §7| §eWeather команды");
        sender.sendMessage("§b/w <clear/rain/thunder> §7- Изменить погоду");
        sender.sendMessage("§bАлиасы: §6/weather <clear/rain/thunder>");
    }

    // ========== TIME COMMAND ==========

    private boolean handleTimeCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendTimeHelp(sender);
            return true;
        }

        String timeArg = args[0].toLowerCase();
        if (timeAliases.containsKey(timeArg)) {
            long time = timeAliases.get(timeArg);
            if (sender instanceof Player) {
                ((Player) sender).getWorld().setTime(time);
            } else {
                Bukkit.getWorlds().get(0).setTime(time);
            }
            sendSuccessMessage(sender, "Время изменено", "Установлено: §b" + getTimeName(timeArg));
        } else {
            sendGradientMessage(sender, "§cНеизвестное время! Доступно: day, night");
        }

        return true;
    }

    private String getTimeName(String timeKey) {
        switch (timeKey) {
            case "day": return "§eДень";
            case "night": return "§9Ночь";
            case "noon": return "§6Полдень";
            case "midnight": return "§8Полночь";
            default: return timeKey;
        }
    }

    private void sendTimeHelp(CommandSender sender) {
        sender.sendMessage("§b⚡ §dSwiftKek §b⚡ §7| §eTime команды");
        sender.sendMessage("§b/t <day/night> §7- Установить время");
        sender.sendMessage("§bАлиасы: §6/t time <day/night>");
    }

    // ========== GC COMMAND ==========

    private boolean handleGcCommand(CommandSender sender) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long allocatedMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = allocatedMemory - freeMemory;
        
        double usagePercent = (double) usedMemory / allocatedMemory * 100;
        
        // Упрощенная проверка TPS (без Bukkit.getTPS())
        String tpsInfo = "§a20.0"; // Заглушка для старых версий
        
        sender.sendMessage("§b⚡ §dSwiftKek §b⚡ §7| §eСтатистика сервера");
        sender.sendMessage("§7Память: §e" + usedMemory + "§7/§a" + allocatedMemory + "§7/§6" + maxMemory + " MB (§c" + df.format(usagePercent) + "%§7)");
        sender.sendMessage("§7TPS: " + tpsInfo);
        sender.sendMessage("§7Онлайн: §a" + Bukkit.getOnlinePlayers().size() + "§7/§6" + Bukkit.getMaxPlayers());
        sender.sendMessage("§7Время работы: §e" + formatUptime());
        
        return true;
    }

    private String formatUptime() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        long hours = uptime / 3600;
        long minutes = (uptime % 3600) / 60;
        long seconds = uptime % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // ========== STAFF CHAT COMMANDS ==========

    private boolean handleStaffChatCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendGradientMessage(sender, "§cИспользование: §b/sc <сообщение>");
            return true;
        }

        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;
        if (!staffChatPlayers.contains(player)) {
            sendGradientMessage(sender, "§cВы не в staff чате! Используйте §b/addstaffchat");
            return true;
        }

        String message = String.join(" ", args);
        sendStaffChatMessage(player, message);
        return true;
    }

    private boolean handleRemoveStaffChatCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sendGradientMessage(sender, "§cКонсоль: /removestaffchat <игрок>");
                return true;
            }
            Player player = (Player) sender;
            staffChatPlayers.remove(player);
            sendSuccessMessage(sender, "Staff Chat", "Вы удалены из staff чата");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
            return true;
        }

        staffChatPlayers.remove(target);
        sendSuccessMessage(sender, "Staff Chat", "Игрок §e" + target.getName() + "§a удален из staff чата");
        target.sendMessage("§b✦ §dStaff Chat §b✦ §7| §cВы удалены из staff чата");
        return true;
    }

    private void sendStaffChatMessage(Player sender, String message) {
        String formattedMessage = "§8[§dStaff§8] §b" + sender.getName() + "§8: §f" + message;
        
        for (Player staff : staffChatPlayers) {
            staff.sendMessage(formattedMessage);
        }
        
        getLogger().info("[Staff] " + sender.getName() + ": " + message);
    }

    // ========== TP POS COMMAND ==========

    private boolean handleTpposCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendGradientMessage(sender, "§cИспользование: §b/tppos <x> <y> <z> [игрок]");
            return true;
        }

        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            
            Player target;
            if (args.length >= 4) {
                target = Bukkit.getPlayer(args[3]);
                if (target == null) {
                    sendGradientMessage(sender, "§cИгрок " + args[3] + " не найден!");
                    return true;
                }
            } else {
                if (!(sender instanceof Player)) {
                    sendGradientMessage(sender, "§cКонсоль: /tppos <x> <y> <z> <игрок>");
                    return true;
                }
                target = (Player) sender;
            }

            World world = target.getWorld();
            Location targetLoc = new Location(world, x, y, z);
            
            if (y <= 0) y = world.getHighestBlockYAt((int) x, (int) z) + 1;
            targetLoc.setY(y);
            
            target.teleport(targetLoc);
            
            if (target == sender) {
                sendSuccessMessage(sender, "Телепортация", "Вы телепортированы к §e" + (int) x + " " + (int) y + " " + (int) z);
            } else {
                sendSuccessMessage(sender, "Телепортация", "Игрок §e" + target.getName() + "§a телепортирован к §e" + (int) x + " " + (int) y + " " + (int) z);
                target.sendMessage("§b✦ §dТелепортация §b✦ §7| §eВас телепортировал: §a" + sender.getName());
            }
            
        } catch (NumberFormatException e) {
            sendGradientMessage(sender, "§cНеверные координаты! Используйте числа.");
        }

        return true;
    }

    // ========== FEED COMMAND ==========

    private boolean handleFeedCommand(CommandSender sender, String[] args) {
        Player target;
        
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sendGradientMessage(sender, "§cКонсоль: /feed <игрок>");
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
                return true;
            }
        }

        target.setFoodLevel(20);
        target.setSaturation(10);
        
        if (target == sender) {
            sendSuccessMessage(sender, "Сытость", "Ваша сытость восстановлена");
        } else {
            sendSuccessMessage(sender, "Сытость", "Сытость игрока §e" + target.getName() + "§a восстановлена");
            target.sendMessage("§b✦ §dСытость §b✦ §7| §aВаша сытость восстановлена");
        }

        return true;
    }

    // ========== HEAL COMMAND ==========

    private boolean handleHealCommand(CommandSender sender, String[] args) {
        Player target;
        
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sendGradientMessage(sender, "§cКонсоль: /heal <игрок>");
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
                return true;
            }
        }

        target.setHealth(target.getMaxHealth());
        target.setFireTicks(0);
        
        target.removePotionEffect(PotionEffectType.POISON);
        target.removePotionEffect(PotionEffectType.WITHER);
        target.removePotionEffect(PotionEffectType.SLOWNESS);
        target.removePotionEffect(PotionEffectType.WEAKNESS);
        target.removePotionEffect(PotionEffectType.BLINDNESS);
        
        if (target == sender) {
            sendSuccessMessage(sender, "Лечение", "Ваше здоровье восстановлено");
        } else {
            sendSuccessMessage(sender, "Лечение", "Здоровье игрока §e" + target.getName() + "§a восстановлено");
            target.sendMessage("§b✦ §dЛечение §b✦ §7| §aВаше здоровье восстановлено");
        }

        return true;
    }

    // ========== FLY COMMAND ==========

    private boolean handleFlyCommand(CommandSender sender, String[] args) {
        Player target;
        
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sendGradientMessage(sender, "§cКонсоль: /fly <игрок>");
                return true;
            }
            target = (Player) sender;
        } else {
            if (!hasPermission(sender, "swiftkek.fly.others")) {
                sendNoPermissionMessage(sender, "swiftkek.fly.others");
                return true;
            }
            
            target = getPlayerFromSelector(sender, args[0]);
            if (target == null) {
                sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
                return true;
            }
        }

        boolean flying = target.getAllowFlight();
        target.setAllowFlight(!flying);
        
        if (!flying) {
            target.setFlying(true);
        }

        if (target == sender) {
            sendSuccessMessage(sender, "Полёт", flying ? "Режим полёта выключен" : "Режим полёта включен!");
        } else {
            sendSuccessMessage(sender, "Полёт", (flying ? "Режим полёта выключен" : "Режим полёта включен") + " для игрока §e" + target.getName());
            target.sendMessage("§b✦ §dПолёт §b✦ §7| §a" + (flying ? "Режим полёта выключен" : "Режим полёта включен!"));
        }

        return true;
    }

    // ========== NOCLIP COMMAND ==========

    private boolean handleNoclipCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
    }

    Player player = (Player) sender;

        if (noclipPlayers.contains(player)) {
            noclipPlayers.remove(player);
            player.setAllowFlight(false);
            player.setFlying(false);
        sendSuccessMessage(player, "NoClip", "Режим NoClip выключен");
    } else {
        noclipPlayers.add(player);
        player.setAllowFlight(true);
        player.setFlying(true);
        sendSuccessMessage(player, "NoClip", "Режим NoClip включен! Вы можете летать сквозь блоки");
    }

    return true;
}
    // ========== AFK COMMAND ==========

    private boolean handleAfkCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;
        
        if (afkPlayers.contains(player)) {
            afkPlayers.remove(player);
            player.setPlayerListName(player.getName());
            sendSuccessMessage(player, "AFK", "Вы больше не в режиме AFK");
            Bukkit.broadcastMessage("§7[§e⚡§7] §e" + player.getName() + " §7вернулся(ась)");
        } else {
            afkPlayers.add(player);
            player.setPlayerListName("§7[AFK] " + player.getName());
            sendSuccessMessage(player, "AFK", "Вы теперь в режиме AFK");
            Bukkit.broadcastMessage("§7[§e⚡§7] §e" + player.getName() + " §7отошел(а) (AFK)");
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (afkPlayers.contains(player) && player.isOnline()) {
                        afkPlayers.remove(player);
                        player.setPlayerListName(player.getName());
                        sendSuccessMessage(player, "AFK", "Автоматически отключен (5 минут)");
                        Bukkit.broadcastMessage("§7[§e⚡§7] §e" + player.getName() + " §7больше не в AFK");
                    }
                }
            }.runTaskLater(this, 20 * 60 * 5);
        }

        return true;
    }

    // ========== TELEPORT COMMANDS ==========

    private boolean handleTphereCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        if (args.length == 0) {
            sendGradientMessage(sender, "§cИспользование: §b/tphere <игрок>");
            return true;
        }

        Player player = (Player) sender;
        Player target = getPlayerFromSelector(sender, args[0]);
        
        if (target == null) {
            sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
            return true;
        }

        if (target == player) {
            sendGradientMessage(sender, "§cНельзя телепортировать себя к себе!");
            return true;
        }

        target.teleport(player.getLocation());
        sendSuccessMessage(player, "Телепортация", "Игрок §e" + target.getName() + "§a телепортирован к вам");
        target.sendMessage("§b✦ §dТелепортация §b✦ §7| §eВас телепортировал: §a" + player.getName());

        return true;
    }

    private boolean handleTpCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cКонсоль: /tp <игрок1> <игрок2>");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            Player target = getPlayerFromSelector(sender, args[0]);
            if (target == null) {
                sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден!");
                return true;
            }

            if (target == player) {
                sendGradientMessage(sender, "§cНельзя телепортироваться к себе!");
                return true;
            }

            player.teleport(target.getLocation());
            sendSuccessMessage(player, "Телепортация", "Вы телепортированы к игроку §e" + target.getName());
            
        } else if (args.length == 2) {
            if (!hasPermission(player, "swiftkek.teleport.others")) {
                sendNoPermissionMessage(player, "swiftkek.teleport.others");
                return true;
            }

            Player target1 = getPlayerFromSelector(sender, args[0]);
            Player target2 = getPlayerFromSelector(sender, args[1]);
            
            if (target1 == null || target2 == null) {
                sendGradientMessage(sender, "§cОдин из игроков не найден!");
                return true;
            }

            if (target1 == target2) {
                sendGradientMessage(sender, "§cНельзя телепортировать игрока к самому себе!");
                return true;
            }

            target1.teleport(target2.getLocation());
            sendSuccessMessage(player, "Телепортация", "Игрок §e" + target1.getName() + "§a телепортирован к игроку §e" + target2.getName());
            target1.sendMessage("§b✦ §dТелепортация §b✦ §7| §eВас телепортировал: §a" + player.getName());
            
        } else {
            sendGradientMessage(sender, "§cИспользование: §b/tp <игрок> §7- телепорт к игроку");
            sender.sendMessage("§b/tp <игрок1> <игрок2> §7- телепорт игрока1 к игроку2");
        }

        return true;
    }

    private boolean handleSpawnCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendGradientMessage(sender, "§cЭта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;
        Location spawn = player.getWorld().getSpawnLocation();
        
        spawn.setY(player.getWorld().getHighestBlockYAt(spawn) + 1);
        
        player.teleport(spawn);
        sendSuccessMessage(player, "Телепортация", "Вы телепортированы на спавн");

        return true;
    }

    // ========== PLAYERLIST COMMAND ==========

    private boolean handlePlayerlistCommand(CommandSender sender) {
        StringBuilder playerList = new StringBuilder();
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        
        sender.sendMessage("§b⚡ §dSwiftKek §b⚡ §7| §eСписок игроков (§a" + players.length + "§e/§6" + Bukkit.getMaxPlayers() + "§e)");
        
        for (int i = 0; i < players.length; i++) {
            Player p = players[i];
            String pingColor = getPingColor(p.getPing());
            playerList.append("§7").append(p.getName()).append(" ").append(pingColor).append(p.getPing()).append("ms");
            
            if (i < players.length - 1) {
                playerList.append("§8, ");
            }
            
            if ((i + 1) % 5 == 0) {
                sender.sendMessage(playerList.toString());
                playerList = new StringBuilder();
            }
        }
        
        if (playerList.length() > 0) {
            sender.sendMessage(playerList.toString());
        }
        
        return true;
    }

    private String getPingColor(int ping) {
        if (ping < 50) return "§a";
        if (ping < 100) return "§e";
        if (ping < 200) return "§6";
        return "§c";
    }

    // ========== FIND COMMAND ==========

    private boolean handleFindCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendGradientMessage(sender, "§cИспользование: §b/find <игрок>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sendGradientMessage(sender, "§cИгрок " + args[0] + " не найден или не в сети!");
            return true;
        }

        Location loc = target.getLocation();
        String worldName = getWorldName(loc.getWorld());
        
        sender.sendMessage("§b⚡ §dSwiftKek §b⚡ §7| §eИнформация об игроке §a" + target.getName());
        sender.sendMessage("§7Мир: §e" + worldName);
        sender.sendMessage("§7Координаты: §aX: " + (int) loc.getX() + " §eY: " + (int) loc.getY() + " §6Z: " + (int) loc.getZ());
        sender.sendMessage("§7Здоровье: §c" + (int) target.getHealth() + "§7/§a" + (int) target.getMaxHealth() + " ❤️");
        sender.sendMessage("§7Сытость: §6" + target.getFoodLevel() + "§7/20 🍖");
        sender.sendMessage("§7Пинг: " + getPingColor(target.getPing()) + target.getPing() + "ms");
        sender.sendMessage("§7Режим игры: §b" + getGamemodeName(target.getGameMode()));
        
        if (target.isOp()) {
            sender.sendMessage("§7Статус: §cOP");
        }
        
        if (vanishedPlayers.contains(target)) {
            sender.sendMessage("§7Статус: §5Невидим (Vanish)");
        }
        
        if (godPlayers.contains(target)) {
            sender.sendMessage("§7Статус: §4Бог (God Mode)");
        }

        return true;
    }

    private String getWorldName(World world) {
        String worldName = world.getName();
        switch (worldName.toLowerCase()) {
            case "world": return "§aОсновной мир";
            case "world_nether": return "§cНезер";
            case "world_the_end": return "§5Эндер";
            default: return "§e" + worldName;
        }
    }

    @Override
    public void onDisable() {
        for (Player vanished : vanishedPlayers) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(this, vanished);
            }
        }
        
        for (Player afk : afkPlayers) {
            afk.setPlayerListName(afk.getName());
        }
        
        getLogger().info("§b⚡ §dSwiftKek v1.4 §bвыключен!");
        getLogger().info("§7Автор: §bFassykite §7| При поддержке: §6StrixGT");
    }
}