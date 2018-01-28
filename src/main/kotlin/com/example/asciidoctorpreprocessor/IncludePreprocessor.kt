package com.example.asciidoctorpreprocessor

import java.util.regex.Pattern

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
class IncludePreprocessor(
		private val handlers: Map<(String) -> Boolean, (String) -> String>,
		private val defaultHandler: (String) -> String,
		private val includeRe: Pattern = Pattern.compile("""include::(.*)\[.*?\]""".trim())) {

	fun resolveIncludesIn(content: String): String {
		val matcher = includeRe.matcher(content)
		var c = content
		while (matcher.find()) {
			val url = matcher
					.group(1)
					.replace("{book-root}", "")
					.let {
						if (it.startsWith(".")) it.substring(1) else it
					}
			val line = matcher.group()
			val handler = this.handlers.entries.firstOrNull { entry -> entry.key(url) }?.value ?: defaultHandler
			val resolvedContent = handler(url)
			c = c.replace(line, resolvedContent)
		}
		return c
	}
}