package com.mr0xf00.lensgenerator

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.squareup.kotlinpoet.KModifier

internal class LensProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val visibility = setOf(KModifier.INTERNAL, KModifier.PUBLIC, KModifier.PRIVATE).first {
            (environment.options["lensVisibility"] ?: "public").uppercase() == it.name
        }
        return Processor(environment.codeGenerator, environment.logger, visibility)
    }
}