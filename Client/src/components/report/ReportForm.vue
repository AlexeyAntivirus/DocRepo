<template>
	<el-container>
		<el-col ref="placeholder"
		        :xs="{offset: 0, span: 24}"
		        :sm="{offset: 0, span: 24}"
		        :md="{offset: 4, span: 16}"
		        :lg="{offset: 4, span: 16}"
		        :xl="{offset: 4, span: 16}"
		        style="overflow-y: auto;">
			<el-card shadow="never">
				<el-form>
					<el-form-item size="small" label="Тип роботи: " prop="workType">
						<el-radio-group v-model="value.workType">
							<el-radio label="Дипломна"></el-radio>
							<el-radio label="Курсова"></el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item size="small" label="Навчальний рік: ">
						<el-col :span="8">
							<el-form-item prop="beginYear">
								<el-input-number size="mini" v-model="value.beginYear" style="width: 100%;"
								                 @change="onBeginYearChanged" :min="1930" :max="2070"/>
							</el-form-item>
						</el-col>
						<el-col class="line" :span="2">-</el-col>
						<el-col :span="8">
							<el-form-item prop="endYear">
								<el-input-number size="mini" v-model="value.endYear" style="width: 100%;"
								                 @change="onEndYearChanged" :min="1931" :max="2071"/>
							</el-form-item>
						</el-col>
					</el-form-item>
					<!--<el-form-item v-if="value.workType === 'Курсова'" size="small" prop="discipline" label="Дисципліна: ">-->
						<!--<el-select size="small" v-model="value.discipline"-->
						           <!--remote filterable-->
						           <!--:loading="isLoading"-->
						           <!--loading-text="Зачекайте доки їде запит."-->
						           <!--:remote-method="findDisciplinesByParam"-->
						           <!--@change="onDisciplineChange"-->
						           <!--value-key="id" style="width: 100%">-->
							<!--<el-option v-for="discipline in disciplineSuggestions" :key="discipline.id"-->
							           <!--:value="discipline" :label="discipline.shortName"/>-->
						<!--</el-select>-->
					<!--</el-form-item>-->
					<el-form-item size="small" prop="educationProgram" label="ОП (Введіть повну назву): ">
						<el-input size="small" v-model="value.educationProgram"/>
					</el-form-item>
					<el-form-item>
						<el-button type="primary" @click="download">Отримати звіт</el-button>
					</el-form-item>
				</el-form>
			</el-card>
		</el-col>
	</el-container>
</template>

<script lang="tsx">
	import Vue from "vue"
	import {Component} from "vue-property-decorator"
	import {WindowSizeChangedEvent} from "@/plugins/window"
	import {DisciplineView} from "@/entities/entities"

	@Component({
		name: "ReportForm"
	})
	export default class ReportForm extends Vue {

		private value: any = {
			workType: "Дипломна",
			beginYear: 2018,
			endYear: 2019,
			educationProgram: ""
		}

		private disciplineSuggestions: DisciplineView[] = []

		private mounted() {
			this.$resizeWatcher.addWindowSizeChangedListener(this.onResize)
		}

		private beforeDestroy() {
			this.$resizeWatcher.removeWindowSizeChangedListener(this.onResize)
		}

		private onResize(size: WindowSizeChangedEvent) {
			const placeholder: Vue = this.$refs.placeholder as Vue
			placeholder.$el.style.height = `${size.windowSize.actualHeight - 96}px`
		}

		private onBeginYearChanged(val: number) {
			if (val === this.value.endYear) {
				this.value.endYear = val + 1
			}
		}

		private onEndYearChanged(val: number) {
			if (val === this.value.beginYear) {
				this.value.beginYear = val - 1
			}
		}

		private download() {
			this.axios.post("/works/report", this.value, {
				responseType: "blob"
			})
				.then(result => {
					const element = document.createElement("a")
					const url = URL.createObjectURL(new Blob([result.data]))

					element.setAttribute("href", url)
					element.setAttribute("download", "report.docx")

					element.style.display = "none"
					document.body.appendChild(element)

					element.click()

					document.body.removeChild(element)
				})
				.catch(reason => {
					this.$alert("З'явилася якась помилка.\n" + reason, "Помилка", {
						type: "error",
						confirmButtonText: "OK",
					})
				})
		}

	}
</script>

<style scoped>
	.line {
		text-align: center;
	}
</style>