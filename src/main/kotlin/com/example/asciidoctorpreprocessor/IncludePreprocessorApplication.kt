package com.example.asciidoctorpreprocessor

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@SpringBootApplication
class IncludePreprocessorApplication

fun main(args: Array<String>) {
	SpringApplication.run(IncludePreprocessorApplication::class.java, *args)
}
