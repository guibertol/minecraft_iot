package com.guilhermebertol.eventos;

import com.guilhermebertol.utils.MqttMensagem;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.example.Main;

public class BlocoEvento implements Listener {

    private final Plugin plugin;

    public BlocoEvento(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void quandoQuebrarBloco(BlockBreakEvent event){

        Player jogador = event.getPlayer();

        Block b = event.getBlock();
        String tipo = b.getType().toString();

        jogador.sendMessage("Bloco quebrado "+jogador.getName());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            MqttMensagem.enviarMensagem("Bloco quebrado "+tipo, "blocos");
        });

        //Adicionando efeito de poção
        jogador.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, true, true));

    }

    @EventHandler
    public void mensagemServidor(ServerListPingEvent event){
        if(!Bukkit.hasWhitelist()){
            event.setMotd("§2Servidor IOT esta ligado, venha jogar!!!");
        }
    }

}
