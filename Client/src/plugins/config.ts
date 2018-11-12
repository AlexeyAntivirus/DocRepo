import Vue from "vue"
import {PluginObject} from "vue"

export interface GlobalConfig {
    readonly serverUrl: string
}

export const globalConfig: GlobalConfig = {
    serverUrl: process.env.NODE_ENV === "production" ? "/docrepo/" : "http://localhost:9216/docrepo/"
}

declare var window: any

declare module "vue/types/vue" {
	interface Vue {
		$globalConfig: GlobalConfig
	}
}

export const GlobalConfigPlugin: PluginObject<GlobalConfig> = {
    install: (VueInstance: any, options) => {
        VueInstance.$globalConfig = globalConfig
        Object.defineProperties(VueInstance.prototype, {
            $globalConfig: {
                get() {
                	return globalConfig
                }
            },
        })
    }
}

Vue.use(GlobalConfigPlugin)

export default GlobalConfigPlugin