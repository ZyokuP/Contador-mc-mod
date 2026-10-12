package com.example.contadorhud;

import com.example.contadorhud.network.ModNetwork;
import net.minecraftforge.fml.common.Mod;

@Mod(ContadorMod.MODID)
public class ContadorMod {
    public static final String MODID = "contadorhud";

    public ContadorMod() {
        ModNetwork.registrar();
    }
}
