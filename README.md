# Artist [![Build Status](https://travis-ci.org/uber/artist.svg?branch=master)](https://travis-ci.org/uber/artist)


As Android apps grow, providing common features and consistent functionality across Views becomes challenging. Typically, this results in copy-pasting features across views, monolithic classes, or complicated inheritance trees. Artist is a highly-extensible platform for creating and maintaining an app’s base set of Android views.

## Overview

Artist is a Gradle plugin written in Kotlin that generates a base set of Android `View`s. Artist-generated views are created using a stencil and trait system. Each view type is declared with a single stencil, which is comprised of a set of traits. All of this comes together to create an easily maintainable system of stencils and traits.

*Stencils*: A `Stencil` defines a View class to be generated. Each `Stencil` has some properties that can be configured and declares a set of traits they exhibit.

*Traits*: A `Trait` defines the new functionality that should be added to a view. It is a hook into the `Stencil`’s codegen process that is called during each `Stencil`’s generation. It is responsible for generating the code that implements `Trait`'s functionality. This could be used to do things like add automatic view analytics to every view or add first-party support for RxBinding APIs (clicks, attach events, visibility changes, etc.) on all your views. 

A simple `Trait` that adds visibility helper methods would look like:

```kotlin
@AutoService(JavaTrait::class)
class VisibilityTrait : JavaTrait {
  override fun generateFor(type: Builder, initMethod: MethodSpec.Builder, rClass: ClassName, baseType: String) {
    arrayOf("visible", "invisible", "gone")
        .forEach { type.addMethod(createVisibilityConvenienceMethod(it)) }
  }

  private fun createVisibilityConvenienceMethod(type: String): MethodSpec {
    return MethodSpec.methodBuilder("is${type.capitalize()}")
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.BOOLEAN)
        .addStatement("return getVisibility() == \$T.${type.toUpperCase()}", TypeNames.Android.View)
        .build()
  }
}
```

A simple `ViewStencil` to generate a `Switch` with visibility helper methods would look like:

```kotlin
class SwitchStencil : JavaViewStencil(
    extendedType = "android.support.v7.widget.SwitchCompat",
    constructorCount = 3,
    defaultAttrRes = "switchStyle",
    addedTraits = VisibilityTrait::class.java) {
  
  override fun name() = "MySwitch"
}
```

Finally leaving you with a generated view like this:

```java
public class MySwitch extends SwitchCompat {
  // Constructors
  
  // protected init method - provided in every stencil
  
  public boolean isVisible() {
    return getVisibility() == View.VISIBLE;
  }
  
  public boolean isGone() {
    return getVisibility() == View.GONE;
  }
  
  public boolean isInvisible() {
    return getVisibility() == View.INVISIBLE;
  }
}
```

This may look like a lot of boilerplate for simple helpers, but it scales quite well when you want to have these methods on _all_ your base views.

## Motivation

#### Common Façade

Everything is behind the façade of commonly named classes, basically "[YOUR_PREFIX]ViewName". This allows you to push as much functionality as you want behind them whilst not changing the front facing entry point. Things we can push behind them include new functionality, other base classes, framework bug fixes, etc.

#### Sane, simple maintainability

The stencil and trait system ensures that base views are defined in one place and that extra functionality is divided up into single-focus traits.

#### Reactive Semantics

Artist-generated views can have [RxBinding](https://github.com/JakeWharton/RxBinding) APIs as first class citizens in their public APIs. In a increasingly reactive world, this gracefully bridges common UI listener interactions to RxJava streams. This can optionally be brought in via the `artist-traits-rx` module.

#### Intelligence

Artist-generated views have deep internal knowledge of their internal state and interactions. This gives you flexibility to do a number of interesting, contextual actions under the hood.

*Automatic Instrumentation*: Artist-generated views know when they're being attached, changed to visible, clicked, etc. This allows you to do automatic instrumentation of impressions and taps in views when they occur, provided the developer has provided an ID. You can also detect and signal a developer if an ID is missing where there should be one.

*Accessibility*: This intelligence gives you enough insight into the state of the view hierarchy to make accessibility a first class citizen in the daily development cycle. Artist-generated views can intelligently infer if there are content description errors associated with them, and signal them to developers in the apps.

For more examples of things you can do with Artist, check out the [Recipes](https://github.com/uber/artist/wiki/Recipes) wiki page.

## Usage

#### Create the Provider module
- Create a new plain Java/Kotlin module (non-Android)
- Add Artist dependencies (API, Traits, Traits-Rx)

#### Implement the Stencil Provider
- Create a class that implements `JavaViewStencilProvider`
- Annotate your class with `@AutoService(JavaViewStencilProvider::class)`

#### Implement Custom Traits (Optional)
- If you have custom traits, then create classes that implement `JavaTrait`
- Annotate those classes with `@AutoService(JavaTrait::class)`

#### Add Provider module to Plugin Classpath
_Option #1_

If your provider module is in it's own project, then you can add the JAR to the buildscript classpath in your main project's root `build.gradle` like:

```groovy
buildscript {
  dependencies {
    classpath <include for your jar>
  }
}
```

_Option #2_

Otherwise, if your provider module is in your primary project, then in order for Artist to find the classes on the plugin classpath during code generation, we must leverage Gradle's `buildSrc`. We use this project within your project to build the classes that will be added to the plugin classpath. This will run before your primary project is built.

- Create a dir at root of project named `buildSrc`
- Navigate to `buildSrc` and add a relative symlink to the provider module `cd $PROJECT_ROOT/buildSrc; ln -s ../path/to/provider/module/root custom-artist-providers`
- Create a `settings.gradle` in `buildSrc` and add `include :custom-artist-providers`
- Update the `build.gradle` for the `buildSrc` project to ensure that the `custom-artist-providers` module is added the buildScript classpath so it is available to the Artist plugin:

```groovy
subprojects { subproject ->
    if (subproject.buildFile.exists()) {
        repositories {
            jcenter()
            google()
        }

        rootProject.dependencies {
            runtime project(path)
        }
    }
    subproject.afterEvaluate {
        // Disable useless tasks in buildSrc
        if (subproject.plugins.hasPlugin("kotlin")) {
            subproject.tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).all {
                kotlinOptions.suppressWarnings = true
            }
        }

        subproject.tasks.findAll {
            it.name.toLowerCase().contains("test") ||
                it.name.toLowerCase().contains("lint") ||
                it.name.toLowerCase().contains("checkstyle") }.each {
            it.enabled = false
        }
    }
}
```

#### Use the Generated Views
The [generated views](https://github.com/uber/artist/tree/master/sample/demo/java) will be added to the library's source files. They can then be consumed as regular views. To add even more consistency, you can write a lint rule or ErrorProne check to ensure that all `View` subclasses use your Artist-generated views.

## Further examples

The set of `JavaViewStencil`s that Artist should process are provided via the `JavaViewStencilProvider`. The [sample's ViewStencilProvider](https://github.com/uber/artist/blob/master/sample/providers/src/main/java/com/uber/artist/myproviders/SampleViewStencilProvider.java) would configure Artist to generate [these Views](https://github.com/uber/artist/tree/master/sample/library/build/generated/source/artist/release/com/uber/artist/mylibrary).

## Download

Artist Plugin [![Maven Central](https://img.shields.io/maven-central/v/com.uber.artist/artist.svg)](https://mvnrepository.com/artifact/com.uber.artist/artist)
```gradle
classpath 'com.uber.artist:artist:0.4.7'
```

Artist API [![Maven Central](https://img.shields.io/maven-central/v/com.uber.artist/artist-api.svg)](https://mvnrepository.com/artifact/com.uber.artist/artist-api)
```gradle
classpath 'com.uber.artist:artist-api:0.4.7'
```

Artist Traits [![Maven Central](https://img.shields.io/maven-central/v/com.uber.artist/artist-traits.svg)](https://mvnrepository.com/artifact/com.uber.artist/artist-traits)
```gradle
classpath 'com.uber.artist:artist-traits:0.4.7'
```

Artist Rx Traits [![Maven Central](https://img.shields.io/maven-central/v/com.uber.artist/artist-traits-rx.svg)](https://mvnrepository.com/artifact/com.uber.artist/artist-traits-rx)
```gradle
classpath 'com.uber.artist:artist-traits-rx:0.4.7'
```

## License

```
Copyright (C) 2017 Uber Technologies

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
