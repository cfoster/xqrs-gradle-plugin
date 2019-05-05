package com.xmllondon.xqrs


import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class XqrsPluginTest {
    @Test
    public void demo_plugin_should_add_task_to_project() {
        Project project = ProjectBuilder.builder().build()
        project.getPlugins().apply 'com.xmllondon.xqrs.plugin'

        assertTrue(project.tasks.xqrsInit instanceof XqrsInitTask)
    }
}
