# Lens Generation for Kotlin

---
<p align="center">
<img src="https://img.shields.io/maven-central/v/io.github.mr0xf00/lens-generator">
</p>

An annotation processor that generates [lens](https://arrow-kt.io/docs/optics/lens/) for Kotlin classes. 

Lens make updating immutable state very easy by allowing you to write :
```kotlin
newState = state.setValue(AppState.users[action.index].address.street, action.newStreet)
```

instead of 

```kotlin
val newAddress = state.users[action.index].address.copy(street = action.newStreet)
val newUser = state.users[action.index].copy(address = newAddress)
val newUsers = state.users.toMutableList().apply { this[action.index] = newUser }
newState = state.copy(users = newUsers)
```

## Getting Started
#### 1. Download
```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}
//...
dependecies {
    implementation("io.github.mr0xf00:lens-generator:0.1.0")
    ksp("io.github.mr0xf00:lens-generator:0.1.0")
    //for KMP prefer
    //add("ksp<Target>", "io.github.mr0xf00:lens-generator:0.1.0") 
}
```
**KMP is supported but only jvm and js targets are available for now.**
#### 2. Annotate your classes/interfaces
Apply ```@GenerateLens``` your classes.

**Only data classes and sealed classes/interfaces are supported for now.**
#### 3. Use the generated lens
```kotlin
val newInstance = MyClass.propertyName.nestedProperty.set(instance, value)
val value = MyClass.propertyName.get(instance)
```

See [sample app](sample) for example usage as well as [SealedTest.kt](sample/src/test/java/com/mr0xf00/lensgenerator/SealedTest.kt) 
for sealed hierarchies and [GenericTest.kt](sample/src/test/java/com/mr0xf00/lensgenerator/GenericTest.kt) for generic classes.

## Collections
The sample app uses `PersistentList` from [kotlinx.collections.immutable](https://github.com/Kotlin/kotlinx.collections.immutable).

See [Lens.kt](sample/src/main/java/com/mr0xf00/lensgenerator/functional/Lens.kt) for an example on how to write your own lens to support immutable collection modification.
