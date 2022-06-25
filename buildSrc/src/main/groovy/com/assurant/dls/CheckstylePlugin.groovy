package com.assurant.dls

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property

class CheckstylePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def greetingExtension = project.extensions.create("greeting", GreetingPluginExtension)
        def checkstyleExtension = project.extensions.create("checkstyle", CheckstylePluginExtension)

        project.task('hello')
                .doLast({
                    println "${greetingExtension.message.get()} from ${greetingExtension.greeter.get()}"
                })

        project.task('checkstyle')
                .doFirst({ project.file('build/reports').mkdirs() })
                .doLast("trigger linter", {
                    project.javaexec {
                        mainClass.set("com.puppycrawl.tools.checkstyle.Main")
                        args("-c")
                        args(checkstyleExtension.config.get())
                        args("-f")
                        args("sarif")
                        args("-o")
                        args("build/reports/checkstyle-analysis.sarif")
                        args(checkstyleExtension.file.get())
                        jvmArgs("-DsuppressionFile=${checkstyleExtension.suppressionFile.get()}")
                        jvmArgs("-Dbasedir=${project.projectDir}")
                        classpath = project.configurations.runtimeClasspath
                    }
                })
    }
}

interface GreetingPluginExtension {
    Property<String> getMessage()

    Property<String> getGreeter()
}

interface CheckstylePluginExtension {
    Property<String> getFile()

    Property<String> getConfig()

    Property<String> getSuppressionFile()
}
