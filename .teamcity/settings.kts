import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

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

version = "2026.2"

project {

    buildType(BuildJava)
}

object BuildJava : BuildType({
    name = "Build_Java"

    vcs {
        root(DslContext.settingsRoot)

        cleanCheckout = true
        checkoutDir = "%env.TEAMCITY_BUILDCONF_NAME%-%build.number%"
    }

    steps {
        script {
            name = "Compile Clas"
            id = "simpleRunner"
            scriptContent = """
                #!/bin/bash
                echo "Compile Class"
                javac HelloWorld/Main.java
                echo "Job completed"
            """.trimIndent()
        }
        script {
            name = "Run Class"
            id = "Run_Class"
            scriptContent = """
                #!/bin/bash
                echo "Run class"
                java -cp . HelloWorld.Main
            """.trimIndent()
        }
        script {
            name = "Create JAR file"
            id = "Create_JAR_file"
            scriptContent = """
                #!/bin/bash
                echo "Creating the jar file"
                jar cfme Main.jar Manifest.txt HelloWorld.Main HelloWorld/Main.class
            """.trimIndent()
        }
        script {
            name = "Run JAR file"
            id = "Run_JAR_file"
            scriptContent = """
                #!/bin/bash
                echo "Running the jar file"
                ls -l
                java -jar Main.jar
                # echo ${'$'}{'${'$'}'}PWD
            """.trimIndent()
        }
    }
})
