package com.github.spongeguy.villageculture;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Villageculture implements ModInitializer {
    public static final String MOD_ID = "villageculture";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        LOGGER.info("Initializing " + MOD_ID + "!");
    }
}
