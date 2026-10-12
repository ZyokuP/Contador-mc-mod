package com.example.contadorhud.client;

import com.example.contadorhud.Posicion;
import com.example.contadorhud.Tiempo;

/** Estado del contador en el cliente (sin clases exclusivas del cliente). */
public class ContadorCliente {
    private static int segundos = -1;
    private static long ocultarEn = 0;
    private static Posicion posicion = Posicion.ARRIBA_CENTRO;

    public static void establecer(int s, int indicePosicion) {
        segundos = s;
        posicion = Posicion.deIndice(indicePosicion);
        ocultarEn = (s == 0) ? System.currentTimeMillis() + 3000 : 0;
    }

    public static void limpiar() {
        segundos = -1;
        posicion = Posicion.ARRIBA_CENTRO;
    }

    public static Posicion posicion() {
        return posicion;
    }

    /** Texto a dibujar, o null si no hay nada que mostrar. */
    public static String texto() {
        if (segundos < 0) return null;
        if (segundos == 0 && System.currentTimeMillis() > ocultarEn) {
            segundos = -1;
            return null;
        }
        return Tiempo.formatear(segundos);
    }
}
