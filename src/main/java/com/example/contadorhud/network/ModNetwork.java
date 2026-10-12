package com.example.contadorhud.network;

import com.example.contadorhud.ContadorMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String VERSION = "1";

    public static final SimpleChannel CANAL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ContadorMod.MODID, "main"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    public static void registrar() {
        CANAL.registerMessage(0, ContadorPaquete.class,
                ContadorPaquete::codificar, ContadorPaquete::decodificar, ContadorPaquete::manejar);
    }
}
