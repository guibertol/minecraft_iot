package com.guilhermebertol.comandos;

import com.guilhermebertol.utils.MqttMensagem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class Iot implements CommandExecutor {

    private final Plugin plugin;

    public Iot(Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){

            Player player = (Player) sender;

            if(command.getName().equalsIgnoreCase("iot")){
                if(args[0].equalsIgnoreCase("mensagem")){

                    String resultado = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        MqttMensagem.enviarMensagem(resultado);
                    });

                    player.sendMessage("§bMensagem IOT enviada! "+resultado);

                    return true;

                }else if(args[0].equalsIgnoreCase("cria_item")){

                    //CRIAÇÃO DE ITEMS
                    ItemStack i = new ItemStack(Material.DIAMOND, 1, (short) 0); //Cria o item

                    //Manipula as informações do item
                    ItemMeta im = i.getItemMeta();
                    im.setDisplayName("§dInformação do item");
                    im.setLore(Arrays.asList("Item diamante"));
                    i.setItemMeta(im);

                    player.getInventory().addItem(i); //Adiciona ao inventario
                    return true;

                }
            }

        }

        return false;

    }

}
