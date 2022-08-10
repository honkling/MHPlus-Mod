package me.honkling.mhplus

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class MHPlusMixinPlugin : IMixinConfigPlugin {
    override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?): Boolean {
        val cpeName = "me.honkling.mhplus.mixins.ClientPlayerEntity19Mixin"
        val args: List<String> = FabricLoader.getInstance().getLaunchArguments(true).toList()
        val versionIndex: Int = args.indexOf("--version")
        val version = args[versionIndex + 1].substring(2, 4).toInt()
        if (mixinClassName == cpeName) {
            return version >= 19
        }
        return true
    }

    // Boilerplate

    override fun onLoad(mixinPackage: String?) {}

    override fun getRefMapperConfig(): String? {
        return null
    }

    override fun acceptTargets(myTargets: MutableSet<String>?, otherTargets: MutableSet<String>?) {}

    override fun getMixins(): MutableList<String>? {
        return null
    }

    override fun preApply(
        targetClassName: String?,
        targetClass: ClassNode?,
        mixinClassName: String?,
        mixinInfo: IMixinInfo?
    ) {}

    override fun postApply(
        targetClassName: String?,
        targetClass: ClassNode?,
        mixinClassName: String?,
        mixinInfo: IMixinInfo?
    ) {}
}