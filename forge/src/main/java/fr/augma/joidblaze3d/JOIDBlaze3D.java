package fr.augma.joidblaze3d;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Constants.MOD_ID)
public class JOIDBlaze3D {

	public JOIDBlaze3D(final FMLJavaModLoadingContext context) {
		if (FMLLoader.getDist() == Dist.CLIENT) {
			JOIDBlaze3DClient.init(context.getModBusGroup());
		}
	}

}