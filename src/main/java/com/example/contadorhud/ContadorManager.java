package com.example.contadorhud;

import com.example.contadorhud.network.ContadorPaquete;
import com.example.contadorhud.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/** Estado del contador en el servidor. Usa el reloj real, así que el lag no lo atrasa. */
@Mod.EventBusSubscriber(modid = ContadorMod.MODID)
public class ContadorManager {

    private enum Estado { INACTIVO, CORRIENDO, PAUSADO }

    private static Estado estado = Estado.INACTIVO;
    private static Posicion posicion = Posicion.ARRIBA_CENTRO;
    private static long finMs;
    private static long restanteMs;
    private static int ultimoEnviado = -1;

    public static void iniciar(long segundos) {
        finMs = System.currentTimeMillis() + segundos * 1000L;
        estado = Estado.CORRIENDO;
        ultimoEnviado = (int) segundos;
        enviarATodos(ultimoEnviado);
    }

    public static boolean pausar() {
        if (estado != Estado.CORRIENDO) return false;
        restanteMs = Math.max(0, finMs - System.currentTimeMillis());
        estado = Estado.PAUSADO;
        enviarATodos(segundosDe(restanteMs));
        return true;
    }

    public static boolean reanudar() {
        if (estado != Estado.PAUSADO) return false;
        finMs = System.currentTimeMillis() + restanteMs;
        estado = Estado.CORRIENDO;
        return true;
    }

    public static boolean detener() {
        if (estado == Estado.INACTIVO) return false;
        estado = Estado.INACTIVO;
        enviarATodos(-1);
        return true;
    }

    public static void mover(Posicion nueva) {
        posicion = nueva;
        enviarATodos(segundosVisibles());
    }

    /** Segundos que deben verse ahora mismo, o -1 si no hay nada que mostrar. */
    private static int segundosVisibles() {
        return switch (estado) {
            case CORRIENDO -> segundosDe(finMs - System.currentTimeMillis());
            case PAUSADO -> segundosDe(restanteMs);
            default -> -1;
        };
    }

    private static int segundosDe(long ms) {
        return (int) ((ms + 999) / 1000);
    }

    private static void enviarATodos(int segundos) {
        ModNetwork.CANAL.send(PacketDistributor.ALL.noArg(),
                new ContadorPaquete(segundos, posicion.ordinal()));
    }

    @SubscribeEvent
    public static void alTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || estado != Estado.CORRIENDO) return;
        int seg = segundosDe(finMs - System.currentTimeMillis());
        if (seg <= 0) {
            estado = Estado.INACTIVO;
            enviarATodos(0); // el cliente muestra 00:00 unos segundos y lo oculta
            return;
        }
        if (seg != ultimoEnviado) {
            ultimoEnviado = seg;
            enviarATodos(seg);
        }
    }

    @SubscribeEvent
    public static void alEntrar(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer jugador)) return;
        int seg = segundosVisibles();
        if (seg >= 0) {
            ModNetwork.CANAL.send(PacketDistributor.PLAYER.with(() -> jugador),
                    new ContadorPaquete(seg, posicion.ordinal()));
        }
    }

    @SubscribeEvent
    public static void alCerrarServidor(ServerStoppedEvent e) {
        estado = Estado.INACTIVO;
    }
}
