package dev.joid.backend.minecraft.fabric.test;

import dev.joid.test.contract.IStateGuardBackend;
import dev.joid.test.contract.StateGuardContractSuite;
import lombok.NonNull;

public class FabricStateGuardContractTest extends StateGuardContractSuite {

	@Override
	protected @NonNull IStateGuardBackend createBackend() {
		return new MinecraftStateGuardBackend();
	}

}