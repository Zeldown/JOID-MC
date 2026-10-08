package dev.joid.impl.minecraft.demo;

import org.lwjgl.glfw.GLFW;

import dev.joid.demo.ui.UIDemoChoice;
import dev.joid.impl.minecraft.IMinecraftDemo;
import dev.joid.impl.minecraft.JoidMinecraft;
import dev.joid.impl.minecraft.registry.KeyMappingRegistry;
import dev.joid.internal.JOID;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class MinecraftDemo implements IMinecraftDemo {

	@Override
	public void bootstrap() {
		final KeyMapping.Category category = new KeyMapping.Category(Identifier.fromNamespaceAndPath(JoidMinecraft.MOD_ID, "demo"));
		KeyMappingRegistry.register(new KeyMapping("key.joid.demo", GLFW.GLFW_KEY_P, category), () -> JOID.open(new UIDemoChoice()));
	}

}