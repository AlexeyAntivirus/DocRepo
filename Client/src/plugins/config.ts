import Vue, {VueConstructor} from "vue"
import {PluginObject} from "vue"

export interface GlobalConfig {
    readonly serverUrl: string
	readonly isInternetExplorer: boolean
}

export const globalConfig: GlobalConfig = {
    serverUrl: "/docrepo/",
	isInternetExplorer: window.navigator.userAgent.indexOf("MSIE ") > 0
}

declare var window: any

declare module "vue/types/vue" {
	interface Vue {
		$globalConfig: GlobalConfig
	}
}

export const GlobalConfigPlugin: PluginObject<GlobalConfig> = {
    install: (VueInstance: VueConstructor, options) => {
        VueInstance.prototype.$globalConfig = globalConfig
    }
}

Vue.use(GlobalConfigPlugin)

export default GlobalConfigPlugin