package com.example.asciidoctorpreprocessor

import org.apache.commons.logging.LogFactory
import org.asciidoctor.Asciidoctor
import org.asciidoctor.ast.Document
import org.jruby.RubyObject
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader

// take an Asciidoctor input, manually resolve
// the includes, and emit the pre-processed file.
class AsciidoctorPreprocessorApplicationTests {

	private val log = LogFactory.getLog(javaClass)

	private val input = InputStreamReader(ClassPathResource("/sample.adoc").inputStream).readText()

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