package dev.joid.backend.minecraft.demo.snapshot;

import dev.joid.base.lwjgl2.snapshot.Lwjgl2StateGuardBackend;
import dev.joid.test.contract.IStateGuardBackend;
import dev.joid.test.contract.StateGuardContractSuite;
import lombok.NonNull;

public class MinecraftStateGuardContractTest extends StateGuardContractSuite {

	@Override
	protected @NonNull IStateGuardBackend createBackend() {
		return new Lwjgl2StateGuardBackend(new MinecraftSnapshotBackend());
	}

}