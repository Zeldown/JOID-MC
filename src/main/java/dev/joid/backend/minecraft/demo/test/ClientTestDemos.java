package dev.joid.backend.minecraft.demo.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import dev.joid.demo.ui.DemoEntry;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientTestDemos {

	public static final String CHOICE              = "dev.joid.backend.minecraft.demo.ui.UIDemoMinecraftChoice";
	public static final String MINECRAFT           = "dev.joid.backend.minecraft.demo.ui.UIDemoMinecraft";
	public static final String CONTAINER           = "dev.joid.backend.minecraft.demo.ui.UIDemoContainer";
	public static final String CONTAINER_POPUP     = "dev.joid.backend.minecraft.demo.ui.UIDemoContainer$Popup";
	public static final String OVERLAY             = "dev.joid.backend.minecraft.demo.ui.UIDemoOverlayLayer";
	public static final String OVERLAY_HOTBAR      = "dev.joid.backend.minecraft.demo.ui.UIDemoOverlayLayer$Hotbar";
	public static final String OVERLAY_EXPERIENCE  = "dev.joid.backend.minecraft.demo.ui.UIDemoOverlayLayer$Experience";
	public static final String OVERLAY_CROSSHAIR   = "dev.joid.backend.minecraft.demo.ui.UIDemoOverlayLayer$Crosshair";
	public static final String OVERLAY_INTERACTIVE = "dev.joid.backend.minecraft.demo.ui.UIDemoOverlayLayer$Interactive";
	public static final String OVERLAY_ALWAYS      = "dev.joid.backend.minecraft.demo.ui.UIDemoOverlayLayer$Always";

	public static final String ITEM_NODE   = "dev.joid.backend.minecraft.lib.ui.node.impl.design.item.ItemNode";
	public static final String BLOCK_NODE  = "dev.joid.backend.minecraft.lib.ui.node.impl.design.block.BlockNode";
	public static final String ENTITY_NODE = "dev.joid.backend.minecraft.lib.ui.node.impl.design.entity.EntityNode";
	public static final String SLOT_NODE   = "dev.joid.backend.minecraft.lib.ui.node.impl.structure.slot.SlotNode";

	public static final String FONT          = "dev.joid.backend.minecraft.lib.font.impl.minecraft.MinecraftFont";
	public static final String OVERLAY_LAYER = "dev.joid.backend.minecraft.lib.ui.core.data.overlay.layer.OverlayLayer";

	public static Class<?> load(final String name) {
		try {
			return Class.forName(name);
		} catch (final ClassNotFoundException exception) {
			throw new AssertionError("The version misses the class " + name + " of the client test plan", exception);
		}
	}

	public static Class<? extends UI> type(final String name) {
		return ClientTestDemos.load(name).asSubclass(UI.class);
	}

	public static Class<? extends Node> nodeType(final String name) {
		return ClientTestDemos.load(name).asSubclass(Node.class);
	}

	public static UI create(final String name) {
		try {
			return ClientTestDemos.type(name).getConstructor().newInstance();
		} catch (final ReflectiveOperationException exception) {
			throw new AssertionError("Unable to create " + name, exception);
		}
	}

	public static Object invoke(final String name, final String method) {
		try {
			final Method target = ClientTestDemos.load(name).getMethod(method);
			return target.invoke(null);
		} catch (final ReflectiveOperationException exception) {
			throw new AssertionError("Unable to call " + name + "." + method, exception);
		}
	}

	public static Object constant(final String name, final String field) {
		try {
			return ClientTestDemos.load(name).getField(field).get(null);
		} catch (final ReflectiveOperationException exception) {
			throw new AssertionError("Unable to read " + name + "." + field, exception);
		}
	}

	public static Object layer(final String layer) {
		for (final Object constant : ClientTestDemos.load(ClientTestDemos.OVERLAY_LAYER).getEnumConstants()) {
			if (((Enum<?>) constant).name().equals(layer)) {
				return constant;
			}
		}
		throw new AssertionError("OverlayLayer has no layer " + layer);
	}

	public static List<DemoEntry> entries() {
		return (List<DemoEntry>) ClientTestDemos.field(null, ClientTestDemos.load(ClientTestDemos.CHOICE), "LIST");
	}

	public static Class<? extends UI> entryType(final DemoEntry entry) {
		return ClientTestDemos.type(entry.getHover());
	}

	public static Object field(final Object target, final String name) {
		return ClientTestDemos.field(target, target.getClass(), name);
	}

	public static Object field(final Object target, final Class<?> type, final String name) {
		for (Class<?> clazz = type; clazz != null; clazz = clazz.getSuperclass()) {
			try {
				final Field field = clazz.getDeclaredField(name);
				field.setAccessible(true);
				return field.get(target);
			} catch (final NoSuchFieldException exception) {
				continue;
			} catch (final IllegalAccessException exception) {
				throw new AssertionError("Unable to read the field " + name + " of " + clazz.getName(), exception);
			}
		}
		throw new AssertionError("No field " + name + " in " + type.getName());
	}

}