package ink.ptms.adyeshach.addon.tags

import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject

object AdyTags : Plugin() {

    @TInject(migrate = true)
    lateinit var conf: TConfig
        private set
}