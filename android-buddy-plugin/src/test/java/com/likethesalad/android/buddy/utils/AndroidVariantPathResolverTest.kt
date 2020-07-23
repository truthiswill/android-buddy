package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import org.junit.Test

class AndroidVariantPathResolverTest {

    @Test
    fun `Get top-bottom path for not flavored variant`() {
        val variantName = "debug"
        val buildTypeName = "debug"
        val instance = createInstance(variantName, buildTypeName, emptyList())

        val result = instance.getTopBottomPath()

        Truth.assertThat(result).containsExactly("debug")
    }

    @Test
    fun `Get top-bottom path for single flavored variant`() {
        val variantName = "demoRelease"
        val buildTypeName = "release"
        val flavors = listOf("demo")
        val instance = createInstance(variantName, buildTypeName, flavors)

        val result = instance.getTopBottomPath()

        Truth.assertThat(result).containsExactly("demo", "release", "demoRelease").inOrder()
    }

    @Test
    fun `Get top-bottom path for 2 flavored variant`() {
        val variantName = "demoStableDebug"
        val buildTypeName = "debug"
        val flavors = listOf("demo", "stable")
        val instance = createInstance(variantName, buildTypeName, flavors)

        val result = instance.getTopBottomPath()

        Truth.assertThat(result)
            .containsExactly("stable", "demo", "demoStable", "debug", "demoStableDebug")
            .inOrder()
    }

    @Test
    fun `Get top-bottom path for 3 flavored variant`() {
        val variantName = "fullStableAnimeRelease"
        val buildTypeName = "release"
        val flavors = listOf("full", "stable", "anime")
        val instance = createInstance(variantName, buildTypeName, flavors)

        val result = instance.getTopBottomPath()

        Truth.assertThat(result)
            .containsExactly(
                "anime", "stable", "full",
                "fullStableAnime", "release", "fullStableAnimeRelease"
            ).inOrder()
    }

    private fun createInstance(
        variantName: String,
        buildTypeName: String,
        flavors: List<String>
    ): AndroidVariantPathResolver {
        return AndroidVariantPathResolver(variantName, buildTypeName, flavors)
    }
}