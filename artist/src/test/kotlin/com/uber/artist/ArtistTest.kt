package com.uber.artist

import com.google.common.io.Files
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.google.testing.compile.JavaFileObjects.forSourceString
import com.google.testing.compile.JavaSourceSubjectFactory.javaSource
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.uber.artist.api.Trait
import com.uber.artist.api.ViewStencil
import org.junit.Test
import javax.lang.model.element.Modifier

class ArtistTest {

    companion object {
        const val TEST_PACKAGE_NAME = "foo.bar"
        val TRAITS = setOf(TestTrait())

        const val IMAGE_VIEW_SOURCE_NO_TRAITS = """package foo.bar;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.lang.SuppressWarnings;

public class MyImageView extends ImageView {
  public MyImageView(Context context) {
    this(context, null);
  }

  public MyImageView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MyImageView(Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
  }

  @CallSuper
  @SuppressWarnings("CheckNullabilityTypes")
  protected void init(
      Context context,
      @Nullable AttributeSet attrs,
      @AttrRes int defStyleAttr,
      @StyleRes int defStyleRes) {}
}
"""

        val IMAGE_VIEW_WITH_TEST_TRAIT = """package foo.bar;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.lang.String;
import java.lang.SuppressWarnings;

public class MyImageView extends ImageView {
  public MyImageView(Context context) {
    this(context, null);
  }

  public MyImageView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MyImageView(Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
  }

  public String testMethod() {
    return "foo";
  }

  @CallSuper
  @SuppressWarnings("CheckNullabilityTypes")
  protected void init(
      Context context,
      @Nullable AttributeSet attrs,
      @AttrRes int defStyleAttr,
      @StyleRes int defStyleRes) {}
}
"""
    }

    @Test
    fun testArtist_withNoTraits_shouldGenerateViews() {
        val outputDir = Files.createTempDir()
        val stencils: Set<ViewStencil> = setOf(
                ViewStencil("android.widget.Button", 3),
                ViewStencil("android.widget.ImageView", 3),
                ViewStencil("android.widget.TextView", 3))

        generateViewsForStencils(stencils, TRAITS, emptySet(), outputDir, TEST_PACKAGE_NAME, TEST_PACKAGE_NAME, null, "My", true)

        val viewOutputDir = outputDir.resolve(TEST_PACKAGE_NAME.replace('.', '/'))
        val viewNames = viewOutputDir.listFiles()
                .map { it.name }
                .toList()
        assertThat(viewNames).containsExactly("MyButton.java", "MyImageView.java", "MyTextView.java")
    }

    @Test
    fun testArtist_withNoTraits_shouldGenerateExpectedSource() {
        val outputDir = Files.createTempDir()
        val stencils: Set<ViewStencil> = setOf(
                ViewStencil("android.widget.ImageView", 3))

        generateViewsForStencils(stencils, TRAITS, emptySet(), outputDir, TEST_PACKAGE_NAME, TEST_PACKAGE_NAME, null, "My", true)

        val viewOutputDir = outputDir.resolve(TEST_PACKAGE_NAME.replace('.', '/'))
        assertWithMessage("$viewOutputDir does not exist").that(viewOutputDir.exists()).isTrue()

        val generatedFileName = viewOutputDir.listFiles().first()
        val generatedViewContent = generatedFileName.readText()
        assertAbout(javaSource())
                .withFailureMessage("$generatedFileName did not compile")
                .that(forSourceString("$TEST_PACKAGE_NAME.MyImageView", generatedViewContent))
                .compilesWithoutError()

        assertWithMessage("$generatedFileName did not match expected file content")
                .that(generatedViewContent)
                .isEqualTo(IMAGE_VIEW_SOURCE_NO_TRAITS)
    }

    @Test
    fun testArtist_withAddedTrait_shouldGenerateExpectedSource() {
        val outputDir = Files.createTempDir()
        val stencils: Set<ViewStencil> = setOf(
                ViewStencil("android.widget.ImageView", 3, addedTraits = TestTrait::class.java))

        generateViewsForStencils(stencils, TRAITS, emptySet(), outputDir, TEST_PACKAGE_NAME, TEST_PACKAGE_NAME, null, "My", true)

        val viewOutputDir = outputDir.resolve(TEST_PACKAGE_NAME.replace('.', '/'))
        assertWithMessage("$viewOutputDir does not exist").that(viewOutputDir.exists()).isTrue()

        val generatedFileName = viewOutputDir.listFiles().first()
        val generatedViewContent = generatedFileName.readText()
        assertAbout(javaSource())
                .withFailureMessage("$generatedFileName did not compile")
                .that(forSourceString("$TEST_PACKAGE_NAME.MyImageView", generatedViewContent))
                .compilesWithoutError()

        assertWithMessage("$generatedFileName did not match expected file content")
                .that(generatedViewContent)
                .isEqualTo(IMAGE_VIEW_WITH_TEST_TRAIT)
    }

    @Test
    fun testArtist_withGlobalTrait_shouldGenerateExpectedSource() {
        val outputDir = Files.createTempDir()
        val globalTraits: Set<Class<out Trait>> = setOf(TestTrait::class.java)
        val stencils: Set<ViewStencil> = setOf(
                ViewStencil("android.widget.ImageView", 3))

        generateViewsForStencils(stencils, TRAITS, globalTraits, outputDir, TEST_PACKAGE_NAME, TEST_PACKAGE_NAME, null, "My", true)

        val viewOutputDir = outputDir.resolve(TEST_PACKAGE_NAME.replace('.', '/'))
        assertWithMessage("$viewOutputDir does not exist").that(viewOutputDir.exists()).isTrue()

        val generatedFileName = viewOutputDir.listFiles().first()
        val generatedViewContent = generatedFileName.readText()
        assertAbout(javaSource())
                .withFailureMessage("$generatedFileName did not compile")
                .that(forSourceString("$TEST_PACKAGE_NAME.MyImageView", generatedViewContent))
                .compilesWithoutError()

        assertWithMessage("$generatedFileName did not match expected file content")
                .that(generatedViewContent)
                .isEqualTo(IMAGE_VIEW_WITH_TEST_TRAIT)
    }

    class TestTrait : Trait {
        override fun generateFor(type: TypeSpec.Builder, initMethod: MethodSpec.Builder, rClass: ClassName, sourceType: String) {
            type.addMethod(MethodSpec.methodBuilder("testMethod")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.get(String::class.java))
                    .addStatement("return \$S", "foo")
                    .build())
        }
    }
}
