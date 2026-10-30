package dev.joid.backend.minecraft.demo.test;

@FunctionalInterface
public interface ClientTestStep {

	public void run(final ClientTestContext context);

}