package com.guilhermebertol.eventos;

import com.guilhermebertol.utils.MqttMensagem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.material.Door;
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
    public void abrirPorta(PlayerInteractEvent event){

        if(event.getClickedBlock() == null){
            return;
        }

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }


        Player player = event.getPlayer();

        //Pega o bloco clicado
        Block block = event.getClickedBlock();
        Material type = block.getType();


        player.sendMessage(type.name());

        if (type == Material.WOODEN_DOOR) {

            Block bottom = block;
            org.bukkit.material.Door doorData = (org.bukkit.material.Door) block.getState().getData();
            if (doorData.isTopHalf()) {
                bottom = block.getRelative(BlockFace.DOWN);
            }

            Block finalBottom = bottom;

            // Espera 1 tick para garantir atualização
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                org.bukkit.material.Door door = (org.bukkit.material.Door) finalBottom.getState().getData();
                boolean aberta = door.isOpen();

                if (aberta) {
                    MqttMensagem.enviarMensagem("1", "acao/porta"); // aberta
                } else {
                    MqttMensagem.enviarMensagem("0", "acao/porta"); // fechada
                }
            }, 1L);

        }else if(type == Material.LEVER){

            Block bottom = block;
            org.bukkit.material.Lever alavancaData = (org.bukkit.material.Lever) block.getState().getData();

            // Espera 1 tick para garantir atualização
            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                boolean aberta = alavancaData.isPowered();

                if (aberta) {
                    MqttMensagem.enviarMensagem("1", "acao/alavanca"); // aberta
                } else {
                    MqttMensagem.enviarMensagem("0", "acao/alavanca"); // fechada
                }
            }, 1L);

        }

    }

    /*@EventHandler
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

    }*/

    @EventHandler
    public void mensagemServidor(ServerListPingEvent event){
        if(!Bukkit.hasWhitelist()){
            event.setMotd("§2Servidor IOT esta ligado, venha jogar!!!");
        }
    }

}
