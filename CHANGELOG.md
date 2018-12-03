Changelog
=========

Version 0.3.0
-------------

_2018-12-03_

* **Breaking change:** Project migrated to [AndroidX](https://developer.android.com/jetpack/androidx/). See the [class and package mappings](https://developer.android.com/jetpack/androidx/migrate) for help migrating

Version 0.2.2
-------------

_2018-11-07_

* **Note:** This is the final version that uses the non-AndroidX Support Library
* Annotate underlying `setOnClickListener(listener)` method param as `@Nullable` to match AOSP
* Dependency updates including using Support Library, Kotlin, RxJava, and RxBinding

Version 0.2.1
-------------

_2018-07-01_

* Separated core artist functionality into a module

Version 0.2.0
-------------

_2018-04-23_

* **Breaking change:** Removed `TraitProvider` in favor of annotating with `AutoService(Trait.class)`
* `ViewStencilProvider` and `ArtistRxConfig` implementations can also be annotated with `AutoService`

Version 0.1.0
-------------

_2017-12-11_

* Initial release
