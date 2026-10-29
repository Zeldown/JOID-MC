package dev.joid.backend.minecraft.loader.registry;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import dev.joid.lib.input.key.Key;

public class KeyBindRegistryTest {

	@Test
	public void listsEachCategoryOnceInTheOrderOfRegistration() {
		KeyBindRegistry.create("joid.first", Key.A).register();
		KeyBindRegistry.create("joid.second", Key.B).category("joid:other").register();
		KeyBindRegistry.create("joid.third", Key.C).register();
		Assert.assertEquals(Arrays.asList("joid:main", "joid:other"), KeyBindRegistry.getCategories());
	}

}