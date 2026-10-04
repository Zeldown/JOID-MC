package fr.augma.joidmc;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Constants.MOD_ID)
public class JOIDMCMod {

	public JOIDMCMod(final FMLJavaModLoadingContext context) {
		if (FMLLoader.getDist() == Dist.CLIENT) {
			JOIDMCClient.init(context.getModBusGroup());
		}
	}

}