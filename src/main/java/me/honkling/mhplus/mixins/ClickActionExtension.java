package me.honkling.mhplus.mixins;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class ClickActionExtension implements Runnable {
	@Override
	public void run() {
		MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

		String clickEventAction = remapper.mapClassName("intermediary", "net.minecraft.class_2558$class_2559");
		ClassTinkerers.enumBuilder(clickEventAction, String.class, boolean.class).addEnum("RUN_CODE", "run_code", false).build();
	}
}
