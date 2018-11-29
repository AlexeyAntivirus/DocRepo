import Vue from "vue"
import "./plugins/axios"
import App from "./App.vue"
import router from "./router"
import ElementUI from "element-ui"
import "./styles.scss"
import {WindowSizeWatcherPlugin} from "@/plugins/window"

Vue.use(WindowSizeWatcherPlugin)
Vue.use(ElementUI)

const lang = require("element-ui/lib/locale/lang/ua")
const locale = require("element-ui/lib/locale")

locale.use(lang)

Vue.config.productionTip = process.env.NODE_ENV === "production"

new Vue({
	router,
	render: (h) => h(App),
}).$mount("#app")
