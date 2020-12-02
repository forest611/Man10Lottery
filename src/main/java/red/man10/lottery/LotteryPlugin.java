package red.man10.lottery;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class LotteryPlugin extends JavaPlugin implements Listener {
    VaultManager vault = null;

    Boolean enable = true;

    static Executor es = Executors.newCachedThreadPool();

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        loadLottery();
        vault = new VaultManager(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    /////////////////////////////////
    //      コマンド処理
    /////////////////////////////////
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        Player p = (Player) sender;
        //      引数がない場合
        if (args.length == 0) {
            if (!p.hasPermission("man10.lottery.op"))return false;
            showHelp(sender);
            return true;
        }
        if(args[0].equalsIgnoreCase("help")){
            if (!p.hasPermission("man10.lottery.op"))return false;
            showHelp(p);
            return true;
        }
        if(args[0].equalsIgnoreCase("reload")){
            if (!p.hasPermission("man10.lottery.op"))return false;
            sender.sendMessage("reloaded.");
            reloadConfig();
            return true;
        }

        if(args[0].equalsIgnoreCase("buy")){
            if (!p.hasPermission("man10.lottery.user"))return false;
            int     n = 1;
            if(args.length == 3){
                n = Integer.parseInt(args[2]);
            }

            Lottery l = new Lottery(this);
            if(!l.load(args[1])){
                p.sendMessage("§4§l"+args[1]+":指定された宝くじはありません");
                return false;
            }

            l.buy(p,n);

        }
        if(args[0].equalsIgnoreCase("stat")){
            if (!p.hasPermission("man10.lottery.op"))return false;
            if(args.length != 2){
                return false;
            }

            Lottery l = new Lottery(this);
            if(!l.load(args[1])){
                p.sendMessage("§4§l"+args[1]+":指定された宝くじはありません");
                return false;
            }


            p.sendMessage(l.prefix+ l.name +" buy:"+l.count + " win:"+l.win);
            if(l.win == 0){
                return true;
            }
            p.sendMessage(l.prefix+ "確率: 1/"+l.count / l.win);

            return true;
        }
//        if(args[0].equalsIgnoreCase("get")){
//            int     n = 1;
//            if(args.length != 2){
//                return false;
//            }
//
//            Lottery l = new Lottery(this);
//            if(!l.load(args[1])){
//                p.sendMessage("§4§l"+args[1]+":指定された宝くじはありません");
//                return false;
//            }
//
//            l.load(args[1]);
//            l.giveController(p);
//
//            return true;
//        }

        return true;
    }

//    @EventHandler
//    public void onInvclick(InventoryClickEvent e) {
//        Player p = (Player)e.getWhoClicked();
//      //  p.sendMessage(e.getEventName());
//    }
//
//    @EventHandler
//    public void onPlayerInteract(PlayerInteractEvent e) {
//        Player p = e.getPlayer();
//
//        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK ) {
//           // p.sendMessage("左クリック");
//
//            ItemStack item = p.getInventory().getItemInMainHand();
//            if(nano.isController(item)){
//                nano.open(p,item.getAmount());
//                item.setAmount(0);
//            }
//            if(mini.isController(item)){
//                mini.open(p,item.getAmount());
//                item.setAmount(0);
//            }
//            if(nom.isController(item)){
//                nom.open(p,item.getAmount());
//                item.setAmount(0);
//            }
//            if(big.isController(item)){
//                big.open(p,item.getAmount());
//                item.setAmount(0);
//            }
//
//        }
//    }

    void showHelp(CommandSender p){
        p.sendMessage("§e============== §d●§f●§a●§e Man10宝くじ §d●§f●§a● §e===============");
        p.sendMessage("§c/mkuji buy [くじ名] [枚数]");
        p.sendMessage("§c/mkuji get [くじ名] [枚数]");
        p.sendMessage("§c/mkuji reload - リロード");
        p.sendMessage("§c/mkuji stat [くじ名] - 統計表示");

        p.sendMessage("クジの種類: Man10Nano/Man10Mini/Man10/Man10Big");
        p.sendMessage("§e  by takatronix http://man10.red");
        p.sendMessage("§c* red commands for Admin");
    }

    Lottery    nano = new Lottery(this);
    Lottery    mini = new Lottery(this);
    Lottery    nom = new Lottery(this);
    Lottery    big = new Lottery(this);

    void  loadLottery(){
        nano.load("Man10Nano");
        mini.load("Man10Mini");
        nom.load("Man10");
        big.load("Man10Big");
    }



}
