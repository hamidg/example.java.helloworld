import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.11"

project {
    description = "Top Level project"

    buildType(CompileJavaFile)
}

object CompileJavaFile : BuildType({
    name = "Compile_Java_file"

    vcs {
        root(DslContext.settingsRoot)

        cleanCheckout = true
        checkoutDir = "%teamcity.agent.work.dir%/%env.TEAMCITY_BUILDCONF_NAME%"
    }

    steps {
        script {
            name = "Compile and run Class"
            id = "Compile"
            scriptContent = """
                #!/bin/bash
                echo "Compile Class"
                javac HelloWorld/Main.java
                echo "Run class"
                java -cp . HelloWorld.Main
                echo "Job completed"
                echo "Job completed" > job.txt
            """.trimIndent()
        }
        script{
            name = "Run JAR file"
            id = "JAR"
            scriptContent = """
                #!/bin/bash
                echo "Running the jar file"
                jar cfme Main.jar Manifest.txt HelloWorld.Main HelloWorld/Main.class
            """.trimIndent()
        }
        
    }

    triggers {
        vcs {
            branchFilter = ""
        }
    }

    features {
        perfmon {
        }
    }
})
