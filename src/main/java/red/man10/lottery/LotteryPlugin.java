package red.man10.lottery;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class LotteryPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents (this,this);
        this.saveDefaultConfig();
        this.reloadConfig();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    /////////////////////////////////
    //      コマンド処理
    /////////////////////////////////
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;

        //      引数がない場合
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")){
            sender.sendMessage("reloaded.");
            reloadConfig();
            return true;
        }

        if(args[0].equalsIgnoreCase("buy")){
            int     n = 1;
            if(args.length == 3){
                n = Integer.parseInt(args[2]);
            }

            Lottery l = new Lottery(this);
            if(l.load(args[1]) == false){
                p.sendMessage("§4§l"+args[1]+":指定された宝くじはありません");
                return false;
            }

            l.buy(p,n);

        }
        if(args[0].equalsIgnoreCase("stat")){
            int     n = 1;
            if(args.length != 2){
                return false;
            }

            Lottery l = new Lottery(this);
            if(l.load(args[1]) == false){
                p.sendMessage("§4§l"+args[1]+":指定された宝くじはありません");
                return false;
            }


            p.sendMessage(l.prefix+ l.name +"buy:"+l.count + " win:"+l.win);
            return true;
        }


        return true;
    }



    void showHelp(CommandSender p){
        p.sendMessage("§e============== §d●§f●§a●§e Man10宝くじ §d●§f●§a● §e===============");
        p.sendMessage("§c/mkuji buy [くじ名] [枚数]");
        p.sendMessage("§c/mkuji get [くじ名] [枚数]");
        p.sendMessage("§c/mkuji reload - リロード");
        p.sendMessage("§c/mkuji stat - 統計表示");
        p.sendMessage("§e  by takatronix http://man10.red");
        p.sendMessage("§c* red commands for Admin");
    }

}
