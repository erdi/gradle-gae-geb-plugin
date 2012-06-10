# Gradle GAE Geb plugin

This plugin is a simple integration point between [Gradle GAE plugin](https://github.com/bmuschko/gradle-gae-plugin) and [Geb browser automation framework](http://www.gebish.org/). It doesn't provide any new tasks but only sets Geb's `baseUrl` configuration property based on Gradle GAE plugin's conventions using `SystemPropertiesBuildAdapter` mechanism.

#Usage

**IMPORTANT:** Using this plugin only makes sense when **Gradle GAE plugin is applied to the project**. Please make sure that this is the case before applying this plugin to your project.

	apply plugin: 'gae-geb'

	buildscript {
		repositories {
			mavenCentral()
		}
		
		dependencies {
			classpath 'org.gradle.api.plugins:gradle-gae-geb-plugin:0.1'
		}
	}
	
After applying the plugin you no longer need to specify `baseUrl` in `GebConfig.groovy` as it will be derived from your build settings.