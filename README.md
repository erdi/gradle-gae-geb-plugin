# Gradle GAE Geb plugin

This plugin is a simple integration point between [Gradle GAE plugin](https://github.com/bmuschko/gradle-gae-plugin) and [Geb browser automation framework](http://www.gebish.org/). It doesn't provide any new tasks but only sets Geb's `baseUrl` based on Gradle GAE plugin's conventions using `SystemPropertiesBuildAdapter` mechanism.