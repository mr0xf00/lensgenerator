package com.mr0xf00.lensgenerator

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver

internal data class Record(val pkg: KSName, val reflectable: Reflectable)

internal data class ReflectableInterface(
    override val data: Reflectable.Data,
    val subclasses: MutableList<Reflectable> = mutableListOf(),
) : Reflectable

internal data class ReflectableClass(
    override val data: Reflectable.Data,
    val parents: List<KSName>,
) : Reflectable

internal sealed interface Reflectable {
    val data: Data

    data class Data(
        val dec: KSClassDeclaration,
        val properties: List<ReflectableProperty>
    ) {
        val className = dec.toClassName()
        val typeParams = dec.typeParameters.toTypeParameterResolver().parametersMap.values
            .map { TypeVariableName(it.name, it.bounds) }
        val parametrizedType = className.addAll(typeParams)
        val pkgName = dec.packageName.asString()
        val hasTypeParams = typeParams.isNotEmpty()
    }
}

internal data class ReflectableProperty(
    private val ksName: KSName,
    val dec: KSPropertyDeclaration,
    val parentDec: KSClassDeclaration,
) {
    val name: String = ksName.asString()
    val typeName = dec.type.toTypeName(parentDec.typeParameters.toTypeParameterResolver())
}

internal fun Reflectable.readableName(): String {
    return data.className.simpleNames.joinToString("")
}

internal fun Reflectable.prKey(pr: ReflectableProperty): String {
    return "${readableName()}::${pr.name}"
}

internal fun Reflectable.Data.indexOfParam(param: TypeVariableName): Int? {
    return typeParams.indexOfFirst { it.name == param.name }.takeUnless { it == -1 }
}

