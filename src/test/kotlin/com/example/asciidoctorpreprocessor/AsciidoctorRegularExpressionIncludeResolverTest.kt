package com.example.asciidoctorpreprocessor

import org.apache.commons.logging.LogFactory
import org.junit.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.getForEntity
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.regex.Pattern

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
class AsciidoctorRegularExpressionIncludeResolverTest {

	private val log = LogFactory.getLog(javaClass)
	private val template = RestTemplateBuilder().build()
	private val cloudNativeJavaBookRoot = File("${System.getenv()["HOME"]}/cloud-native-java-book/")

	@Test
	fun test() {
		val content: String = BufferedReader(InputStreamReader(FileInputStream(File("/home/jlong/Desktop/misc/sample.adoc")))).use { it.readText() }
		val result = this.process(content)
	}

	fun process(content: String): String {
		val includeRe = Pattern.compile("""include::(.*)\[.*?\]""".trim())
		val matcher = includeRe.matcher(content)
		while (matcher.find()) {
			val start = matcher.start()
			val stop = matcher.end()
			val url = matcher
					.group(1)
					.replace("{book-root}", "")
					.let {
						if (it.startsWith(".")) it.substring(1) else it
					}
			val line = matcher.group()
			val resolvedContent = this.handlerFor(url)(url)

		}
		return ""
	}

	private val handlers: Map<(String) -> Boolean, (String) -> String> =
			mapOf({ target: String -> target.contains("snippets") } to { target: String ->
				/* todo have this actually read the file from the file system */ target
			})

	private fun handlerFor(target: String): (String) -> String =
			this.handlers.entries.firstOrNull { entry -> entry.key(target) }?.value ?: {
				// default handler, checks Github
				this.template.getForEntity<String>(this.targetToCNJGitHubUrl(it)).body
			}

	private fun targetToCNJGitHubUrl(target: String) =
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