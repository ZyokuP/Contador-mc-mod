package com.example.contadorhud;

import java.util.Locale;

public final class Tiempo {
    private Tiempo() {}

    /** 75 -> "01:15"  |  3725 -> "1:02:05" */
    public static String formatear(long totalSegundos) {
        long h = totalSegundos / 3600;
        long m = (totalSegundos % 3600) / 60;
        long s = totalSegundos % 60;
        if (h > 0) return String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s);
        return String.format(Locale.ROOT, "%02d:%02d", m, s);
    }
}
