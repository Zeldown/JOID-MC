package dev.joid.backend.minecraft.forge;

import dev.joid.backend.minecraft.forge.event.ForgeClientEvents;
import dev.joid.backend.minecraft.forge.event.ForgeScreenEvents;
import dev.joid.backend.minecraft.forge.network.ForgePayloads;
import dev.joid.backend.minecraft.forge.registry.ForgeContainerUIRegistry;
import dev.joid.backend.minecraft.forge.registry.ForgeKeyBindRegistry;
import dev.joid.backend.minecraft.forge.registry.ForgeOverlayLayerRegistry;
import dev.joid.backend.minecraft.forge.registry.ForgeReloadListenerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.eventbus.api.IEventBus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForgeJoidClient {

	public static void register(final IEventBus modBus) {
		try {
			System.out.println("[JOIDDIAG] MOD_CLASSES " + System.getenv("MOD_CLASSES"));
			System.out.println("[JOIDDIAG] classpath " + System.getProperty("java.class.path"));
			final java.lang.reflect.Method locate = dev.joid.internal.HotReload.class.getDeclaredMethod("locate", Class.class);
			locate.setAccessible(true);
			for (final String name : new String[] { "dev.joid.backend.minecraft.demo.test.ClientTestFrameUI", "dev.joid.demo.ui.simple.UIDemoSimple", "dev.joid.backend.minecraft.forge.ForgeJoid" }) {
				final Class<?> type = Class.forName(name, false, ForgeJoidClient.class.getClassLoader());
				final java.security.CodeSource source = type.getProtectionDomain().getCodeSource();
				final String entry = name.replace('.', '/') + ".class";
				System.out.println("[JOIDDIAG] " + name + " location " + (source != null ? source.getLocation() : null) + " protocol " + (source != null && source.getLocation() != null ? source.getLocation().getProtocol() + " path " + source.getLocation().getPath() : null) + " locate " + locate.invoke(null, type));
				for (ClassLoader loader = type.getClassLoader(); loader != null; loader = loader.getParent()) {
					System.out.println("[JOIDDIAG]   " + loader + " " + loader.getResource(entry) + " / " + java.util.Collections.list(loader.getResources(entry)));
				}
			}
		} catch (final Exception exception) {
			exception.printStackTrace(System.out);
		}
		System.out.flush();
		Runtime.getRuntime().halt(1);
		ForgeClientEvents.register();
		ForgeScreenEvents.register();
		ForgePayloads.registerSender();
		ForgeKeyBindRegistry.register(modBus);
		ForgeContainerUIRegistry.register(modBus);
		ForgeOverlayLayerRegistry.register();
		ForgeReloadListenerRegistry.register();
	}

}