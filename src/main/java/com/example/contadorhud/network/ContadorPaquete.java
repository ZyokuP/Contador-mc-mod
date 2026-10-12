package com.example.contadorhud.network;

import com.example.contadorhud.client.ContadorCliente;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** segundos >= 0 -> mostrar ese valor | -1 -> ocultar. Incluye la posición en pantalla. */
public class ContadorPaquete {
    private final int segundos;
    private final int posicion;

    public ContadorPaquete(int segundos, int posicion) {
        this.segundos = segundos;
        this.posicion = posicion;
    }

    public static void codificar(ContadorPaquete msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.segundos);
        buf.writeByte(msg.posicion);
    }

    public static ContadorPaquete decodificar(FriendlyByteBuf buf) {
        return new ContadorPaquete(buf.readInt(), buf.readByte());
    }

    public static void manejar(ContadorPaquete msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ContadorCliente.establecer(msg.segundos, msg.posicion));
        ctx.get().setPacketHandled(true);
    }
}
