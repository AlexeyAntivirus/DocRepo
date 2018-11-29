
<template>
	<el-aside :width="width">
		<el-menu class="app-menu"
		         :default-active="$route.path"
		         router
		         background-color="#40407a"
		         text-color="#FFFFFF"
		         active-text-color="#ffb142"
		         :collapse="collapse" style="vertical-align: middle">
			<el-menu-item index="/works">
				<i class="docrepo docrepo-diploma-icon app-menu__item__icon"></i>
				<span slot="title" style="margin-left: 5px">Дипломні та курсові</span>
			</el-menu-item>
			<el-menu-item index="/students">
				<i class="docrepo docrepo-student-icon app-menu__item__icon"></i>
				<span slot="title" style="margin-left: 5px">Студенти</span>
			</el-menu-item>
			<el-menu-item index="/prep">
				<i class="docrepo docrepo-teacher-icon app-menu__item__icon"></i>
				<span slot="title" style="margin-left: 5px">Викладачі</span>
			</el-menu-item>
			<el-menu-item index="/disciplines">
				<i class="docrepo docrepo-discipline-icon app-menu__item__icon"></i>
				<span slot="title" style="margin-left: 5px">Предмети</span>
			</el-menu-item>
			<el-menu-item index="/groups">
				<i class="docrepo docrepo-group-icon app-menu__item__icon"></i>
				<span slot="title" style="margin-left: 5px">Групи</span>
			</el-menu-item>
			<el-menu-item index="/stat">
				<i class="docrepo docrepo-stat-icon app-menu__item__icon"></i>
				<span slot="title" style="margin-left: 5px">Статистика</span>
			</el-menu-item>
		</el-menu>
	</el-aside>
</template>

<script lang="ts">
	import Vue from "vue"
	import {Component} from "vue-property-decorator"
	import {WindowSize, WindowSizeChangedEvent} from "../plugins/window"

	@Component({
		name: "AppMenu"
	})
	export default class AppMenu extends Vue {

		private collapse: boolean = false
		private width: string = "200px"

		private mounted() {
			this.$resizeWatcher.addWindowSizeChangedListener(this.onWindowSizeChanged)
		}

		private beforeDestroy() {
			this.$resizeWatcher.removeWindowSizeChangedListener(this.onWindowSizeChanged)
		}

		private onWindowSizeChanged(size: WindowSizeChangedEvent) {
			if (size.windowSize.actualWidth < 1281) {
				this.collapse = true
				this.width = "65px"
			} else {
				this.collapse = false
				this.width = "200px"
			}
		}
	}
</script>

<style lang="scss" scoped>
	.app-menu {
		height: 100%;

		&__item__icon {
			color: white;
			font-size: 36px;
		}
	}
</style>


