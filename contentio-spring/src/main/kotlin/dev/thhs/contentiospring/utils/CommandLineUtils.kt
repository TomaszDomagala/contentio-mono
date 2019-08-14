package dev.thhs.contentiospring.utils

import java.io.File

/** Code inspired by https://stackoverflow.com/a/41495542   */
fun String.runCommandWithOutput(workingDir: File = File(System.getProperty("user.dir"))): ByteArray {
    return runCommand(workingDir).inputStream.readAllBytes()
}

fun String.runCommand(workingDir: File = File(System.getProperty("user.dir"))): Process {
    return createCommandProcess(workingDir).start()
}

fun String.createCommandProcess(workingDir: File = File(System.getProperty("user.dir"))): ProcessBuilder {
    return ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
}