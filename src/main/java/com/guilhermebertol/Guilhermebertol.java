package com.guilhermebertol;

import com.guilhermebertol.comandos.Iot;
import com.guilhermebertol.eventos.BlocoEvento;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.guilhermebertol.utils.MqttMensagem;

public class Guilhermebertol extends JavaPlugin{

    private static Guilhermebertol instance;
    //private static MYSQL mysql;

    @Override
    public void onEnable() {

        instance = this;
        Bukkit.getConsoleSender().sendMessage("§2 MOD IOT Minecraft inicializado");
        loadConfiguration();
        System.out.println(getConfig().getString("Config.ip"));

        //Registrando os eventos do MOD
        //Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getPluginManager().registerEvents(new BlocoEvento(this), this);

        registrarComandos();

    }

    public void registrarComandos(){
        getCommand("iot").setExecutor(new Iot(this));
    }

    @Override
    public void onDisable() {

    }

    public void loadConfiguration(){
        //Aqui informa que nao sera copiado configuraçõs do playes
        //Mas sim as configs que definirmos em arquivo config.yml
        getConfig().options().copyDefaults(false);
        saveDefaultConfig();
    }

    public static Guilhermebertol getInstance(){
        return instance;
    }

}
