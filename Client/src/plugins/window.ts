import Vue, {VueConstructor} from "vue"
import {PluginObject} from "vue"

export interface WindowSize {
	readonly actualWidth: number
	readonly actualHeight: number
}

export interface WindowSizeChangedEvent {
	originalEvent: Event
	target: Window
	windowSize: WindowSize
}

export type WindowSizeChangedCallback = (size: WindowSizeChangedEvent) => void

export class ResizeWatcher {
	private readonly publisher: Vue
	private actualWidth: number
	private actualHeight: number

	constructor() {
		this.publisher = new Vue()
		this.actualWidth = window.innerWidth
		this.actualHeight = window.innerHeight

		window.addEventListener("resize", (event) => {
			const target = event.target as Window

			this.actualWidth = target.innerWidth
			this.actualHeight = target.innerHeight

			const windowSizeChangedEvent: WindowSizeChangedEvent = {
				originalEvent: event,
				windowSize: {
					actualWidth: this.actualWidth,
					actualHeight: this.actualHeight
				},
				target
			}

			this.publisher.$emit("WindowSizeChanged", windowSizeChangedEvent)
		})

		this.publisher.$emit("WindowSizeChanged", (windowSizeChangedEvent: WindowSizeChangedEvent) => {
			document.body.style.width = `${windowSizeChangedEvent.windowSize.actualWidth}px`
			document.body.style.height = `${windowSizeChangedEvent.windowSize.actualHeight}px`
		})

		this.dispatchWindowResizeChangedEvent()
	}

	public get width(): number {
		return this.actualWidth
	}

	public get height(): number {
		return this.actualHeight
	}

	public get size(): WindowSize {
		return {
			actualHeight: this.actualHeight,
			actualWidth: this.actualWidth
		}
	}

	public addWindowSizeChangedListener(callback: WindowSizeChangedCallback) {
		this.publisher.$on("WindowSizeChanged", callback)
		this.dispatchWindowResizeChangedEvent()
	}

	public removeWindowSizeChangedListener(callback: WindowSizeChangedCallback) {
		this.publisher.$off("WindowSizeChanged", callback)
	}

	public dispatchWindowResizeChangedEvent() {
		const event = document.createEvent("Event")
		event.initEvent("resize", true, true)

		window.dispatchEvent(event)
	}
}

declare module "vue/types/vue" {
	interface Vue {
		$resizeWatcher: ResizeWatcher
	}
}

export const WindowSizeWatcherPlugin: PluginObject<ResizeWatcher> = {
	install(VueInstance: VueConstructor, options: any) {
		VueInstance.prototype.$resizeWatcher = new ResizeWatcher()
	}
}