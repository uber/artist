import com.squareup.kotlinpoet.ClassName

class AliasTypeNames {

  class Rx {
    companion object {

      // As a consequence of RxBinding2 migrating to RxBinding3 and replacing its static calls
      // with equivalent extension functions, its important that we do not interrupt any existing
      // code that was created with RxBinding2 in mind, namely the function names. This can mean
      // that function names share the same name as the extension function which is being used in
      // lieu of the original static function.

      //Ideally, we would come up with a better way of listing these classes so as to keep this DRY.
      val RecyclerViewScrollEvent = ClassName("com.jakewharton.rxbinding3.recyclerview", "RecyclerViewScrollEvent")
      val RxView = ClassName("com.jakewharton.rxbinding3.view", "RxView")
      val RxCompoundButton = ClassName("com.jakewharton.rxbinding3.widget", "RxCompoundButton")
      val RxNestedScrollView = ClassName("com.jakewharton.rxbinding3.core", "RxNestedScrollView")
      val RxRecyclerView = ClassName("com.jakewharton.rxbinding3.recyclerview", "RxRecyclerView")
      val RxSearchView = ClassName("com.jakewharton.rxbinding3.appcompat", "RxSearchView")
      val RxSeekBar = ClassName("com.jakewharton.rxbinding3.widget", "RxSeekBar")
      val SeekBarChangeEvent = ClassName("com.jakewharton.rxbinding3.widget", "SeekBarChangeEvent")
      val SeekBarProgressChangeEvent = ClassName("com.jakewharton.rxbinding3.widget", "SeekBarProgressChangeEvent")
      val SeekBarStartChangeEvent = ClassName("com.jakewharton.rxbinding3.widget", "SeekBarStartChangeEvent")
      val RxSwipeRefreshLayout = ClassName("com.jakewharton.rxbinding3.swiperefreshlayout", "RxSwipeRefreshLayout")
      val RxTabLayout = ClassName("com.jakewharton.rxbinding3.material", "RxTabLayout")
      val RxTextView = ClassName("com.jakewharton.rxbinding3.widget", "RxTextView")
      val RxToolbar = ClassName("com.jakewharton.rxbinding3.widget", "RxToolbar")
      val RxViewPager = ClassName("com.jakewharton.rxbinding3.viewpager", "RxViewPager")
      val RxViewAttachEvent = ClassName("com.jakewharton.rxbinding3.view", "ViewAttachEvent")
      val RxViewAttachAttachedEvent = ClassName("com.jakewharton.rxbinding3.view", "ViewAttachAttachedEvent")
      val RxViewAttachDetachedEvent = ClassName("com.jakewharton.rxbinding3.view", "ViewAttachDetachedEvent")
      val SearchViewQueryTextEvent = ClassName("com.jakewharton.rxbinding3.widget", "SearchViewQueryTextEvent")
      val ViewScrollChangeEvent = ClassName("com.jakewharton.rxbinding3.view", "ViewScrollChangeEvent")

      data class ExtensionFunctionAlias(
          val className: ClassName,
          val methodName: String
      )

      val list = listOf<ExtensionFunctionAlias>(
          ExtensionFunctionAlias(RxToolbar, "itemClicks"),
          ExtensionFunctionAlias(RxView, "layoutChanges"),
          ExtensionFunctionAlias(RxView, "clicks"),
          ExtensionFunctionAlias(RxView, "attachEvents"),
          ExtensionFunctionAlias(RxView, "longClicks"),
          ExtensionFunctionAlias(RxToolbar, "navigationClicks"),
          ExtensionFunctionAlias(RxView, "scrollChangeEvents"),
          ExtensionFunctionAlias(RxNestedScrollView, "scrollChangeEvents"),
          ExtensionFunctionAlias(RxCompoundButton, "checkedChanges"),
          ExtensionFunctionAlias(RxSwipeRefreshLayout, "refreshes"),
          ExtensionFunctionAlias(RxSeekBar, "changeEvents"),
          ExtensionFunctionAlias(RxTabLayout, "selections"),
          ExtensionFunctionAlias(RxSearchView, "queryTextChangeEvents"),
          ExtensionFunctionAlias(RxTabLayout, "selections")

      )

      val map2_to_alias = list.map { it to it.className.simpleName.toLowerCase() + "_" + it
          .methodName }.toMap()
    }
  }
}
