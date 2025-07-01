package io.github.linkfgfgui.barrel_allocator;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Locale;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = Barrel_allocator.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<String> DIRECTION_STRING = BUILDER
            .comment("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
            .define("DIRECTION", Direction.LEFT.name());

    static final ModConfigSpec SPEC = BUILDER.build();
    public enum Direction{
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        BACK
    }
    public static Direction direction;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        try {
            direction = Direction.valueOf(DIRECTION_STRING.get().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            direction = Direction.LEFT;
            Barrel_allocator.LOGGER.error("Invalid direction in config: {}", DIRECTION_STRING.get());
        }
    }
}
