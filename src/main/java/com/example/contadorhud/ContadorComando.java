package com.example.contadorhud;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * /contador <tiempo>   -> inicia (ej: 90, 30s, 5m, 1h30m, 2h15m10s)
 * /contador pausar | reanudar | detener
 * /contador posicion <arriba-centro | arriba-izquierda | arriba-derecha | izquierda | centro |
 *                     derecha | abajo-izquierda | abajo-centro | abajo-derecha>
 * Solo operadores (nivel 2).
 */
@Mod.EventBusSubscriber(modid = ContadorMod.MODID)
public class ContadorComando {

    private static final Pattern FORMATO =
            Pattern.compile("^(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?$", Pattern.CASE_INSENSITIVE);

    @SubscribeEvent
    public static void registrar(RegisterCommandsEvent e) {
        LiteralArgumentBuilder<CommandSourceStack> raiz = Commands.literal("contador")
                .requires(src -> src.hasPermission(2));

        raiz.then(Commands.literal("pausar").executes(c -> pausar(c.getSource())));
        raiz.then(Commands.literal("reanudar").executes(c -> reanudar(c.getSource())));
        raiz.then(Commands.literal("detener").executes(c -> detener(c.getSource())));

        LiteralArgumentBuilder<CommandSourceStack> pos = Commands.literal("posicion");
        for (Posicion p : Posicion.values()) {
            pos.then(Commands.literal(p.nombre).executes(c -> mover(c.getSource(), p)));
        }
        raiz.then(pos);

        raiz.then(Commands.argument("tiempo", StringArgumentType.word())
                .suggests((ctx, b) -> SharedSuggestionProvider.suggest(
                        new String[]{"30s", "5m", "10m", "1h"}, b))
                .executes(c -> iniciar(c.getSource(), StringArgumentType.getString(c, "tiempo"))));

        e.getDispatcher().register(raiz);
    }

    private static int mover(CommandSourceStack src, Posicion p) {
        ContadorManager.mover(p);
        src.sendSuccess(() -> Component.literal("Contador movido a: " + p.nombre), true);
        return 1;
    }

    private static int iniciar(CommandSourceStack src, String texto) {
        long seg = parsear(texto);
        if (seg <= 0 || seg > Integer.MAX_VALUE) {
            src.sendFailure(Component.literal("Tiempo no válido. Ejemplos: 90, 30s, 5m, 1h30m"));
            return 0;
        }
        ContadorManager.iniciar(seg);
        src.sendSuccess(() -> Component.literal("Contador iniciado: " + Tiempo.formatear(seg)), true);
        return 1;
    }

    private static int pausar(CommandSourceStack src) {
        if (!ContadorManager.pausar()) { src.sendFailure(Component.literal("No hay un contador en marcha.")); return 0; }
        src.sendSuccess(() -> Component.literal("Contador en pausa."), true);
        return 1;
    }

    private static int reanudar(CommandSourceStack src) {
        if (!ContadorManager.reanudar()) { src.sendFailure(Component.literal("No hay un contador en pausa.")); return 0; }
        src.sendSuccess(() -> Component.literal("Contador reanudado."), true);
        return 1;
    }

    private static int detener(CommandSourceStack src) {
        if (!ContadorManager.detener()) { src.sendFailure(Component.literal("No hay ningún contador activo.")); return 0; }
        src.sendSuccess(() -> Component.literal("Contador detenido."), true);
        return 1;
    }

    /** Devuelve segundos, o -1 si el formato no es válido. */
    private static long parsear(String t) {
        try {
            if (t.matches("\\d+")) return Long.parseLong(t);
            Matcher m = FORMATO.matcher(t);
            if (t.isEmpty() || !m.matches()) return -1;
            long h = valor(m, 1), mi = valor(m, 2), s = valor(m, 3);
            return Math.addExact(Math.addExact(Math.multiplyExact(h, 3600L), Math.multiplyExact(mi, 60L)), s);
        } catch (NumberFormatException | ArithmeticException ex) {
            return -1;
        }
    }

    private static long valor(Matcher m, int grupo) {
        String g = m.group(grupo);
        return g == null ? 0 : Long.parseLong(g);
    }
}
