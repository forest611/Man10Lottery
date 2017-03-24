package red.man10.lottery;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

/**
 * Created by takatronix on 2017/03/25.
 */
public class Lottery {
    private final LotteryPlugin plugin;

    public Lottery(LotteryPlugin plugin) {
        this.plugin = plugin;
    }

    String      prefix;
    String      name;
    String      dispName;
    List<String> lore;
    double   price;
    double   prize;
    double   stock;
    double   current_stock;
    int      max_chance;
    int      count;


    boolean hasMoney(double price){
        return true;
    }
    boolean withdrawMoney(Player p,double price){
        return true;
    }
    boolean depositMoney(Player p,double price){
        return true;
    }
    void    serverMessage(String string){
        Bukkit.getServer().broadcastMessage(prefix + string);
    }
    void    saveConfig(){


        plugin.getConfig().set(name + ".count",count);
        plugin.getConfig().set(name + ".current_stock",current_stock);
        plugin.saveConfig();

    }
    public boolean load(String name){
        Object o = plugin.getConfig().get(name);
        if(o == null){
           return false;
        }

        this.name = name;
        this.prefix = plugin.getConfig().getString(name + ".prefix");
        this.dispName = plugin.getConfig().getString(name + ".dispName");
        this.lore = plugin.getConfig().getStringList(name+".lore");
        this.max_chance = plugin.getConfig().getInt(name+".max_chance");
        this.count = plugin.getConfig().getInt(name+".count");
        this.prize = plugin.getConfig().getDouble(name+".prize");
        this.price = plugin.getConfig().getDouble(name+".price");
        this.stock = plugin.getConfig().getDouble(name+".stock");
        this.current_stock = plugin.getConfig().getDouble(name+".current_stock");
        return true;
    }
    public String desc(){
        String s = name + "disp:"+dispName +"max_chance:"+max_chance +"prize:"+prize + " price"+price +" stock:" +stock + " current_stock:" + current_stock + " lore"+lore;
        return s;
    }


    int buy(Player p){
        if(hasMoney(price) == false){
            return -1;
        }
        if(withdrawMoney(p,price) == false){
            return -2;
        }
        count ++;

        boolean result = cast(p);

        //      あたり
        if(result == true){
            return 0;
        }

        current_stock += stock;
        return 1;
    }

    //      指定枚数購入する
    int buy(Player p,int num){
        int n = 0;
        for(int i = 0;i < num;i++){
            int ret = buy(p);
            if(ret == -1){
                saveConfig();

                p.sendMessage(prefix + " §2&l宝くじを買うお金が足りません");
                return n;
            }
            if(ret == -2){
                saveConfig();
                p.sendMessage(prefix + " §2&l支払いに失敗しました");
                return n;
            }
            n++;
            //      あたり
            if(ret == 0){
                double payout = prize + current_stock;
                serverMessage(" §b§l§n " + this.dispName+ " 当選！！！！   ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!! ");
                serverMessage(" §4§l§n" + p.getName() + "は"+n+"枚購入し、当選した！！");
                serverMessage(" §6§l§n"+ p.getName()+"は"+(int)payout+"円をゲットした！！！！");
                current_stock = 0;
                depositMoney(p,payout);
                saveConfig();
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,0);

        return n;
    }
}

    saveConfig();
    double paid = price * n;
    double price = prize + current_stock;
        p.sendMessage(prefix + " " + dispName+"を"+n+"枚購入し、" + (int)paid+"円支払いました");
                p.sendMessage(prefix + " はずれ！ §e§l賞金＋ストックが、"+(int)price+"円にアップした！！");
        return n;
    }

    //      くじを引く
    boolean cast(Player p){
//        long seed = System.currentTimeMillis();
        Random rndSeed = new Random();
        int val = rndSeed.nextInt(max_chance);
//        p.sendMessage("引いた:"+val);
        if(val == 0){
            return true;
        }
        return false;
    }


}
