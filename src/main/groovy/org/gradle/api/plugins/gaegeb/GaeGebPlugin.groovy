/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.plugins.gaegeb

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.gae.GaePlugin
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.gae.GaePluginConvention
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test
import geb.buildadapter.SystemPropertiesBuildAdapter

class GaeGebPlugin implements Plugin<Project> {

	void apply(Project project) {
		project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
			GaePluginConvention convention = project.convention.plugins.gae
			def url = "http://localhost:${convention.httpPort}/"
			Test gaeFunctionalTest = project.tasks.findByName(GaePlugin.GAE_FUNCTIONAL_TEST)
			gaeFunctionalTest.systemProperty(SystemPropertiesBuildAdapter.BASE_URL_PROPERTY_NAME, url)
		}
	}
}
