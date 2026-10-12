package com.example.contadorhud.client;

import com.example.contadorhud.ContadorMod;
import com.example.contadorhud.Posicion;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ContadorMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HudContador {

    /** Distancia al borde de la pantalla (px). */
    private static final int MARGEN = 4;

    @SubscribeEvent
    public static void registrar(RegisterGuiOverlaysEvent e) {
        e.registerAboveAll("contador", (gui, g, parcial, ancho, alto) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.options.hideGui) return; // respeta F1
            String t = ContadorCliente.texto();
            if (t == null) return;

            Posicion p = ContadorCliente.posicion();
            int tw = mc.font.width(t);
            int th = mc.font.lineHeight;

            int x = switch (p.horizontal) {
                case 0 -> MARGEN + 2;
                case 2 -> ancho - tw - MARGEN - 2;
                default -> (ancho - tw) / 2;
            };
            int y = switch (p.vertical) {
                case 0 -> MARGEN;
                case 1 -> (p.horizontal == 1) ? alto / 2 - 30 : (alto - th) / 2; // en el centro, sobre la mira
                default -> (p.horizontal == 1) ? alto - 80 : alto - th - MARGEN; // abajo-centro, sobre barras y hotbar
            };

            g.drawString(mc.font, t, x, y, 0xFFFFFFFF, true);
        });
    }
}
