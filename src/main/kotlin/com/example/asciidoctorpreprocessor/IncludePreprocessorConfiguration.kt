package com.example.asciidoctorpreprocessor

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
class IncludePreprocessorConfiguration {

	@Bean
	@ConditionalOnMissingBean
	fun defaultRestTemplate(): RestTemplate = RestTemplateBuilder().build()

	@Bean
	@ConditionalOnMissingBean
	fun includePreprocessor(template: RestTemplate): IncludePreprocessor {

		val handlers: Map<(String) -> Boolean, (String) -> String> =
				mapOf({ target: String -> target.contains("snippets") } to { target: String -> target })

		val default: (String) -> String = {
			val targetToCNJGitHubUrl: (String) -> String = { target ->
				target.replace("//", "")
						.let { if (it.startsWith("/")) it.substring(1) else it }
						.let {
							val dividedBySlash = if (it.startsWith(".")) {
								it.substring(1)
							} else {
								it
							}
									.split('/')
							val root = dividedBySlash[0]
							val rest = dividedBySlash.slice(IntRange(1, dividedBySlash.size - 1)).joinToString("/")
							"https://raw.githubusercontent.com/cloud-native-java/$root/master/$rest"
						}
			}

			val cnjGitHubUrl = targetToCNJGitHubUrl(it)
			val responseEntity = template.getForEntity<String>(cnjGitHubUrl)
			responseEntity.body
		}

		return IncludePreprocessor(handlers, default)
	}


}