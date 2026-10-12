package com.example.contadorhud.client;

import com.example.contadorhud.ContadorMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ContadorMod.MODID, value = Dist.CLIENT)
public class EventosCliente {
    @SubscribeEvent
    public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
        ContadorCliente.limpiar();
    }
}
