package com.guilhermebertol.comandos;

import com.guilhermebertol.utils.MqttMensagem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

                }
            }

        }

        return false;

    }

}
