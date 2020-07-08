package com.likethesalad.android.buddy.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.utils.ConcatIterator
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.bytebuddy.dynamic.ClassFileLocator
import org.junit.Before
import org.junit.Test
import java.io.File

class SourceForMultipleFoldersTest : BaseMockable() {

    @MockK
    lateinit var folderIteratorFactory: FolderIteratorFactory

    @MockK
    lateinit var byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator

    private lateinit var sourceForMultipleFolders: SourceForMultipleFolders

    private val folder1 = mockk<File>()
    private val folder2 = mockk<File>()
    private val folders = setOf<File>(folder1, folder2)

    @Before
    fun setUp() {
        sourceForMultipleFolders = SourceForMultipleFolders(
            folderIteratorFactory, byteBuddyClassesInstantiator, folders
        )
    }

    @Test
    fun `Check manifest is null`() {
        Truth.assertThat(sourceForMultipleFolders.manifest).isNull()
    }

    @Test
    fun `Check read origin is self`() {
        Truth.assertThat(sourceForMultipleFolders.read()).isEqualTo(sourceForMultipleFolders)
    }

    @Test
    fun `Check iterator comes from folders's concatenated`() {
        val iterator1 = mockk<FolderIterator>()
        val iterator2 = mockk<FolderIterator>()
        every { folderIteratorFactory.create(folder1) }.returns(iterator1)
        every { folderIteratorFactory.create(folder2) }.returns(iterator2)

        val result = sourceForMultipleFolders.iterator()

        Truth.assertThat(result.javaClass).isEqualTo(ConcatIterator::class.java)
        Truth.assertThat((result as ConcatIterator).iterators).containsExactly(
            iterator1, iterator2
        )
    }

    @Test
    fun `Check class locator is compound of all folders locators`() {
        val locator1 = mockk<ClassFileLocator>()
        val locator2 = mockk<ClassFileLocator>()
        val allLocator = mockk<ClassFileLocator>()
        every {
            byteBuddyClassesInstantiator.makeFolderClassFileLocator(folder1)
        }.returns(locator1)
        every {
            byteBuddyClassesInstantiator.makeFolderClassFileLocator(folder2)
        }.returns(locator2)
        every {
            byteBuddyClassesInstantiator.makeCompoundClassFileLocator(listOf(locator1, locator2))
        }.returns(allLocator)

        val result = sourceForMultipleFolders.classFileLocator

        Truth.assertThat(result).isEqualTo(allLocator)
        verify {
            byteBuddyClassesInstantiator.makeCompoundClassFileLocator(listOf(locator1, locator2))
        }
    }
}