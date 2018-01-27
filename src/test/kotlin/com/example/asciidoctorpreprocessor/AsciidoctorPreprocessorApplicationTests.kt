package com.example.asciidoctorpreprocessor

import org.apache.commons.logging.LogFactory
import org.asciidoctor.Asciidoctor
import org.asciidoctor.ast.Document
import org.asciidoctor.ast.DocumentRuby
import org.asciidoctor.extension.IncludeProcessor
import org.asciidoctor.extension.PreprocessorReader
import org.jruby.RubyObject
import org.junit.Test
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.io.FileSystemResource
import org.springframework.web.client.getForEntity
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

// take an Asciidoctor input, manually resolve
// the includes, and emit the pre-processed file.
class AsciidoctorPreprocessorApplicationTests {

	private val log = LogFactory.getLog(javaClass)

	private val input: String = BufferedReader(
			InputStreamReader(FileSystemResource("/home/jlong/Desktop/misc/sample.adoc").inputStream))
			.use { it.readLines() }
			.joinToString(System.lineSeparator())

	@Test
	fun load() {
		val asciidoctor: Asciidoctor = Asciidoctor.Factory.create()
				.apply {
					this.javaExtensionRegistry().includeProcessor(SimpleIncludeProcessor())
				}
		val document: Document = asciidoctor.load(this.input, mapOf("safe" to "unsafe", "parse" to "false"))
		document.sections.forEach { roAny: Any ->
			if (roAny is RubyObject) {
				val ro = roAny as RubyObject
				log.info(ro)
			}
		}
	}
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SimpleIncludeProcessor : IncludeProcessor() {

	private val template = RestTemplateBuilder().build()
	private val cloudNativeJavaBookRoot = File("${System.getenv()["HOME"]}/cloud-native-java-book/")
	private val log = LogFactory.getLog(javaClass)

	override fun process(document: DocumentRuby,
	                     reader: PreprocessorReader, target: String, attributes: MutableMap<String, Any>) {

		val content = this.handlerFor(target)(target)
		reader.push_include(content, target, target, 1, attributes)
		this.log.debug("process($document , $reader , $target , $attributes) ")
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

	override fun handles(target: String?): Boolean = true

}