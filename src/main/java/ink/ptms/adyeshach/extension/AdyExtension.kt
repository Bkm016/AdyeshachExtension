package ink.ptms.adyeshach.extension

import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.Bukkit

object AdyExtension : Plugin() {

    @TInject(migrate = true)
    lateinit var conf: TConfig
        private set
}