package com.guilhermebertol.comandos;

import com.guilhermebertol.utils.MqttMensagem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
                        MqttMensagem.enviarMensagem(resultado, "iot/mensagem");
                    });

                    //Som
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);

                    player.sendMessage("§bMensagem IOT enviada! "+resultado);

                    return true;

                }else if(args[0].equalsIgnoreCase("comecar")){

                    //CRIAÇÃO DE ITEMS
                    ItemStack i = new ItemStack(Material.LEVER, 1, (short) 0); //Cria o item

                    //Manipula as informações do item
                    ItemMeta im = i.getItemMeta();
                    im.setDisplayName("§dAlavanca IOT");
                    im.setLore(Arrays.asList("Alavanca com MQTT"));
                    i.setItemMeta(im);

                    player.getInventory().addItem(i); //Adiciona ao inventario

                    ItemStack porta = new ItemStack(Material.WOOD_DOOR, 1, (short) 0); //Cria o item
                    //Manipula as informações do item
                    ItemMeta portaInv = porta.getItemMeta();
                    portaInv.setDisplayName("§dPorta IOT");
                    portaInv.setLore(Arrays.asList("Porta com MQTT."));
                    porta.setItemMeta(portaInv);

                    player.getInventory().addItem(porta); //Adiciona ao inventario


                    return true;

                }
            }

        }

        return false;

    }

}
