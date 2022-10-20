package com.mr0xf00.lensgenerator

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.Modifier

internal fun KSClassDeclaration.isInterface(): Boolean {
    return (classKind == ClassKind.INTERFACE || classKind == ClassKind.CLASS)
            && Modifier.SEALED in modifiers
}

internal fun KSClassDeclaration.isDataClass(): Boolean {
    return classKind == ClassKind.CLASS && Modifier.DATA in modifiers
}

internal fun KSClassDeclaration.interfaceProperties(): List<ReflectableProperty> {
    return getDeclaredProperties().filter { it.isAbstract() }
        .map { ReflectableProperty(it.simpleName, it, this) }.toList()
}

internal fun KSClassDeclaration.dataClassProperties(): List<ReflectableProperty> {
    val constructorParams =
        primaryConstructor!!.parameters.map { it.name!!.asString() }.distinct()
    return getDeclaredProperties().filter { it.simpleName.asString() in constructorParams }
        .map { ReflectableProperty(it.simpleName, it, this) }.toList()
}

internal fun KSClassDeclaration.hasCompanion(): Boolean {
    return declarations.filterIsInstance<KSClassDeclaration>().any { it.isCompanionObject }
}

internal fun KSClassDeclaration.parents(): List<KSName> {
    return getAllSuperTypes().map { it.declaration.qualifiedName }.filterNotNull().toList()
}
