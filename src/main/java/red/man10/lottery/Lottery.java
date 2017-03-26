package red.man10.lottery;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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
    int      win;
    String      command;


    boolean hasMoney(Player p,double price){
        double bal = plugin.vault.getBalance(p.getUniqueId());
        if(bal >= price) {
            return true;
        }
        return false;
    }

    boolean withdrawMoney(Player p,double price){
        return plugin.vault.withdraw(p.getUniqueId(),price);
    }

    boolean depositMoney(Player p,double price){
        return plugin.vault.deposit(p.getUniqueId(),price);
    }
    void    serverMessage(String string){
        Bukkit.getServer().broadcastMessage(prefix + string);
    }
    void    saveConfig(){


        plugin.getConfig().set(name + ".win",win);
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
        this.command = plugin.getConfig().getString(name + ".command");
        this.dispName = plugin.getConfig().getString(name + ".dispName");
        this.lore = plugin.getConfig().getStringList(name+".lore");
        this.max_chance = plugin.getConfig().getInt(name+".max_chance");
        this.count = plugin.getConfig().getInt(name+".count");
        this.win = plugin.getConfig().getInt(name+".win");
        this.prize = plugin.getConfig().getDouble(name+".prize");
        this.price = plugin.getConfig().getDouble(name+".price");
        this.stock = plugin.getConfig().getDouble(name+".stock");
        this.current_stock = plugin.getConfig().getDouble(name+".current_stock");
        this.lore = plugin.getConfig().getStringList(name+".lore");
        return true;
    }
    public String desc(){
        String s = name + "disp:"+dispName +"max_chance:"+max_chance +"prize:"+prize + " price"+price +" stock:" +stock + " current_stock:" + current_stock + " lore"+lore;
        return s;
    }


    int buy(Player p){
        if(hasMoney(p,price) == false){
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

    int check(Player p){
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
            int ret = check(p);
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
                serverMessage(" §6§l§n"+ p.getName()+"は"+payout+"円をゲットした！！！！");
                current_stock = 0;
                win++;
                depositMoney(p,payout);
                saveConfig();
                playSound(p);

               return n;
             }
        }

        saveConfig();
          double paid = price * n;
         double price = prize + current_stock;
        p.sendMessage(prefix + " " + dispName+"を"+n+"枚購入し、" + paid+"円支払いました");
        p.sendMessage(prefix + " はずれ！ §e§l賞金＋ストックが、"+price+"円にアップした！！！");
        return n;
    }
    //      指定枚数開く
    int open(Player p,int num){
        int n = 0;
        for(int i = 0;i < num;i++){
            int ret = check(p);
            n++;
            //      あたり
            if(ret == 0){
                double payout = prize + current_stock;
                serverMessage(" §b§l§n " + this.dispName+ " 当選！！！！   ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!! ");
                serverMessage(" §6§l§n"+ p.getName()+"は"+payout+"円をゲットした！！！！");
                current_stock = 0;
                win++;
                depositMoney(p,payout);
                saveConfig();
                playSound(p);
                return n;
            }
        }

        saveConfig();
        double price = prize + current_stock;
        p.sendMessage(prefix + "  §9§lはずれ!!!§f("+num+"枚) §f§l現在の賞金＋ストックは§e§l"+price+"円です");
        return n;
    }

    //      くじを引く
    boolean cast(Player p){
        Random rndSeed = new Random();
        int val = rndSeed.nextInt(max_chance);
        if(val == 0){
            return true;
        }
        return false;
    }


    boolean playSound(Player p){
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH,1,2);
        return true;
    }

    Material controllerMaterial = Material.PAPER;

    boolean isController(ItemStack item){
        if(item.getType() != controllerMaterial){
            return false;
        }
        String name = item.getItemMeta().getDisplayName();
        if(name == null){
            return false;
        }
        if(dispName == null){
            return false;
        }

        if(!dispName.contentEquals(name)){
            return false;
        }
        return true;
    }


    void giveController(Player p){
        ItemStack item = new ItemStack(controllerMaterial,1);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(dispName);
        im.setLore(lore);
        item.setItemMeta(im);
        p.getInventory().addItem(item);
    }


}
