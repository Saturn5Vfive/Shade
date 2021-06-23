package net.shade;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;

public class Shade implements ModInitializer {

	public static KeyBinding cGuiKey;
	public static KeyBinding customKey;


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.


		System.out.println("Shade has started");

		cGuiKey = new KeyBinding("Console", InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_V, "Shade");
		KeyBindingHelper.registerKeyBinding(cGuiKey);

		customKey = new KeyBinding("Custom", InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_M, "Shade");
		KeyBindingHelper.registerKeyBinding(customKey);
	}
}