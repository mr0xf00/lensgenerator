package com.mr0xf00.lensgenerator

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.mr0xf00.lensgenerator.lens.Lens
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.writeTo

internal val LENS_PKG = Lens::class.asClassName().packageName

internal class Processor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val visibility: KModifier
) : SymbolProcessor {

    private fun fail(msg: String, node: KSClassDeclaration): Nothing {
        logger.error(msg, node)
        error("Encountered error. Can't continue")
    }

    private val needCompanion = mutableListOf<KSClassDeclaration>()

    private val recs = mutableMapOf<KSName, Record>()

    override fun finish() {
        validateCompanionObjects()
        findSubclasses()
        recs.values.forEach { it.writeLens(codeGenerator, visibility) }
    }

    private fun validateCompanionObjects() {
        if (needCompanion.isEmpty()) return
        needCompanion.groupBy { it.containingFile }.forEach { (file, declarations) ->
            logger.error(
                "Following types must have a companion object : ${
                    declarations.joinToString { it.toClassName().simpleNames.joinToString(".") }
                }",
                file
            )
        }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val invalid = mutableListOf<KSAnnotated>()
        resolver.getSymbolsWithAnnotation(GenerateLens::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .forEach {
                if (!it.validate()) invalid += it
                else onClass(it)
            }
        return invalid
    }

    private fun findSubclasses() = recs.values.map { it.reflectable }
        .filterIsInstance<ReflectableClass>()
        .forEach { r ->
            r.parents.forEach { parent ->
                (recs[parent]?.reflectable as? ReflectableInterface)
                    ?.subclasses?.add(r)
            }
        }

    private fun onClass(declaration: KSClassDeclaration) {
        if (!declaration.hasCompanion()) {
            needCompanion += declaration
        }
        recs[declaration.qualifiedName!!] = Record(
            declaration.packageName, when {
                declaration.isInterface() -> ReflectableInterface(
                    Reflectable.Data(declaration, declaration.interfaceProperties())
                )
                declaration.isDataClass() -> ReflectableClass(
                    Reflectable.Data(declaration, declaration.dataClassProperties()),
                    declaration.parents()
                )
                else -> fail(
                    "Can't handle type ${declaration.simpleName.asString()}",
                    declaration
                )
            }
        )
    }
}