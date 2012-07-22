package org.gradle.api.plugins.gaegeb.test

import geb.buildadapter.SystemPropertiesBuildAdapter
import org.gradle.GradleLauncher
import org.gradle.StartParameter
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.plugins.gae.GaePluginConvention
import org.gradle.api.tasks.TaskState
import org.gradle.api.tasks.testing.Test
import org.gradle.initialization.DefaultGradleLauncher
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll
import static org.gradle.api.plugins.gae.GaePlugin.GAE_FUNCTIONAL_TEST

class IntegrationSpec extends Specification {

	@Rule final TemporaryFolder dir = new TemporaryFolder()

	static class ExecutedTask {
		Task task
		TaskState state
	}

	List<ExecutedTask> executedTasks = []

	GradleLauncher launcher(String... args) {
		StartParameter startParameter = GradleLauncher.createStartParameter(args)
		startParameter.setProjectDir(dir.root)
		DefaultGradleLauncher launcher = GradleLauncher.newInstance(startParameter)
		launcher.gradle.scriptClassLoader.addParent(getClass().classLoader)
		executedTasks.clear()
		launcher.addListener(new TaskExecutionListener() {
			void beforeExecute(Task task) {
				executedTasks << new ExecutedTask(task: task)
			}

			void afterExecute(Task task, TaskState taskState) {
				executedTasks.last().state = taskState
				taskState.metaClass.upToDate = taskState.skipMessage == 'UP-TO-DATE'
			}
		})
		launcher
	}

	File getBuildFile() {
		file('build.gradle')
	}

	File file(String path) {
		def parts = path.split('/')
		if (parts.size() > 1) {
			dir.newFolder(* parts[0..-2])
		}
		dir.newFile(path)
	}

	ExecutedTask task(String name) {
		executedTasks.find { it.task.name == name }
	}

	def setup() {
		buildFile << """
			def GaeGebPlugin = project.class.classLoader.loadClass('org.gradle.api.plugins.gaegeb.GaeGebPlugin')

			apply plugin: 'gae'
			apply plugin: GaeGebPlugin

			dependencies {
				gaeSdk "com.google.appengine:appengine-java-sdk:1.6.6"
			}

			repositories {
				mavenCentral()
			}

			gae {
				downloadSdk = true
				stopKey = 'stop'
			}
		"""
	}

	def cleanup() {
		launcher('gaeStop').run()
	}

	@Unroll("buildUrl system property is set when gae.http value is '#conventionValue'")
	def "buildUrl system property is set based on httpPort convention of gae plugin"() {
		given:
		if (conventionValue) {
			buildFile << """
				gae {
					httpPort = $conventionValue
				}
			"""
		}

		when:
		launcher(GAE_FUNCTIONAL_TEST).run()

		then:
		Test testTask = task(GAE_FUNCTIONAL_TEST).task
		testTask.systemProperties[SystemPropertiesBuildAdapter.BASE_URL_PROPERTY_NAME] == "http://localhost:$propertyPort/"

		where:
		conventionValue | propertyPort
		null            | new GaePluginConvention().httpPort
		8085            | 8085
	}
}

