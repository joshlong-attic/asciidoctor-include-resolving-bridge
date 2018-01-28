package com.example.asciidoctorpreprocessor

import org.apache.commons.logging.LogFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.junit4.SpringRunner
import java.io.InputStreamReader

/**
 * @author [Josh Long](mailto:josh@joshlong.com)
 */
@SpringBootTest
@RunWith(SpringRunner::class)
class IncludePreprocessorTest {

	private val log = LogFactory.getLog(javaClass)

	val sample = InputStreamReader(ClassPathResource("/sample.adoc").inputStream).readText()

	@Autowired
	val includePreprocessor: IncludePreprocessor? = null

	@Test
	fun testIncludeResolution() {
		val resolvedUrl = this.includePreprocessor!!.resolveIncludesIn(sample)
		this.log.info("resolved content: $resolvedUrl")
	}
}