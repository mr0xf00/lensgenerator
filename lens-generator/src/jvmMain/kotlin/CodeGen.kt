package com.mr0xf00.lensgenerator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.ksp.writeTo

internal fun Record.writeLens(codeGenerator: CodeGenerator, visibility: KModifier) {
    val fileBuilder = FileSpec.builder(pkg.asString(), "LensGen_${reflectable.readableName()}")
        .addFileComment("Auto generated file")
        .addImport(LENS_PKG, "Lens", "lens", "plus")

    propertiesBlock(
        receiver = reflectable.data.className.nestedClass("Companion"),
        target = fileBuilder, visibility = visibility
    )

    lensExtensions(visibility).forEach(fileBuilder::addProperty)

    if (reflectable is ReflectableInterface)
        fileBuilder.addFunction(reflectable.copyFunction())

    fileBuilder.build()
        .writeTo(
            codeGenerator, true, reflectable.inputFiles().distinct()
        )
}

private fun Record.propertiesBlock(
    receiver: TypeName, target: FileSpec.Builder,
    visibility: KModifier
) = reflectable.data.properties.forEach { pr ->
    val parentType: TypeName = reflectable.data.parametrizedType

    val lensTypeName = ClassName(LENS_PKG, "Lens")
        .plusParameter(parentType)
        .plusParameter(pr.typeName)

    val prName = pr.name

    val initializer = CodeBlock.builder().addStatement(
        "lens(getter = { it.$prName }, setter = { copy(${prName} = it) })",
        reflectable.prKey(pr),
    ).build()

    if (reflectable.data.hasTypeParams) {
        target.addFunction(
            FunSpec.builder(pr.name).returns(lensTypeName)
                .addModifiers(visibility)
                .receiver(receiver)
                .addCode(
                    listOf(CodeBlock.of("return"), initializer).joinToCode(" ")
                )
                .addTypeVariables(reflectable.data.typeParams)
                .build()
        )
    } else {
        target.addProperty(
            PropertySpec.builder(pr.name, lensTypeName).apply {
                receiver(receiver)
                addModifiers(visibility)
                delegate(
                    CodeBlock.builder()
                        .beginControlFlow("lazy")
                        .add(initializer)
                        .endControlFlow()
                        .build()
                )
            }.build()
        )
    }
}

private fun ReflectableInterface.copyFunction(): FunSpec {
    val fn = FunSpec.builder("copy").addModifiers(KModifier.PRIVATE).receiver(data.className)
    for (pr in data.properties) fn.addParameter(
        ParameterSpec.builder(pr.name, pr.typeName)
            .defaultValue("this.${pr.name}")
            .build()
    )
    val body = CodeBlock.builder().apply {
        add("return ")
        beginControlFlow("when(this)")
        subclasses.forEach { subclass ->
            val args = data.properties.joinToString { "${it.name} = ${it.name}" }
            addStatement("is %T -> copy($args)", subclass.data.className)
        }
//        addStatement("else -> TODO()")
        endControlFlow()
    }.build()
    fn.addCode(body)
    return fn.build()
}

internal fun Record.lensExtensions(visibility: KModifier): List<PropertySpec> {
    val p = TypeVariableName("Parent")
    val data = reflectable.data
    return data.properties.map { pr ->
        val receiverType =
            ClassName(LENS_PKG, "Lens")
                .plusParameter(p)
                .plusParameter(data.parametrizedType)
        val type =
            ClassName(LENS_PKG, "Lens")
                .plusParameter(p)
                .plusParameter(pr.typeName)
        PropertySpec.builder(pr.name, type)
            .addModifiers(visibility)
            .addTypeVariable(p)
            .addTypeVariables(data.typeParams)
            .receiver(receiverType).getter(
                FunSpec.getterBuilder()
                    .addCode(
                        "return this + %T.${pr.name}${if (data.hasTypeParams) "()" else ""}",
                        data.className
                    )
                    .build()
            ).build()
    }
}

private fun Reflectable.inputFiles(): List<KSFile> {
    val other = when (this) {
        is ReflectableInterface -> this.subclasses.flatMap { it.inputFiles() }
        else -> emptyList()
    }
    return listOfNotNull(data.dec.containingFile) + other
}

internal fun ClassName.addAll(parameters: Iterable<TypeVariableName>): TypeName {
    val iterator = parameters.iterator()
    if (!iterator.hasNext()) return this
    var result = plusParameter(iterator.next())
    while (iterator.hasNext()) {
        result = result.plusParameter(iterator.next())
    }
    return result
}
