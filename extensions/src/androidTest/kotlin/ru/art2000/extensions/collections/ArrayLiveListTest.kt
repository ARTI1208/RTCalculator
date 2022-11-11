@file:Suppress("unused")

package ru.art2000.extensions.collections

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class ArrayLiveListTest : AnnotationSpec() {

    private var list = ArrayLiveList<Int>()

    private var anyCalled = false
    private var insertCalled = false
    private var removedCalled = false
    private var replacedCalled = false

    private fun clearCalled() {
        anyCalled = false
        insertCalled = false
        removedCalled = false
        replacedCalled = false
    }

    @Before
    fun before() {
        clearCalled()

        list = ArrayLiveList()
        list.observeForever(object : LiveList.LiveListObserver<Int>() {
            override fun onAnyChanged(previousList: List<Int>) {
                anyCalled = true
            }

            override fun onItemsInserted(
                previousList: List<Int>,
                insertedIndices: List<Int>
            ) {
                insertCalled = true
            }

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {
                removedCalled = true
            }

            override fun onItemsReplaced(previousList: List<Int>, replacedIndices: List<Int>) {
                replacedCalled = true
            }
        })
    }

    @Test
    fun checkAdd() {

        list.observeForever(object : LiveList.LiveListObserver<Int>() {
            override fun onItemsInserted(
                previousList: List<Int>,
                insertedIndices: List<Int>
            ) {
                previousList shouldHaveSize 0
                insertedIndices.shouldContainExactly(0)
            }
        })

        list.add(7)

        list.shouldContainExactly(7)
        anyCalled shouldBe true
        insertCalled shouldBe true
        removedCalled shouldBe false
        replacedCalled shouldBe false
    }

    @Test
    fun checkAddIndex() {

        list.addAll(listOf(1, 2, 3, 4, 5))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {
            override fun onItemsInserted(
                previousList: List<Int>,
                insertedIndices: List<Int>
            ) {
                previousList.shouldContainExactly(1, 2, 3, 4, 5)
                insertedIndices.shouldContainExactly(2)
            }
        })

        list.add(2, 7)

        list.shouldContainExactly(1, 2, 7, 3, 4, 5)
        anyCalled shouldBe true
        insertCalled shouldBe true
        removedCalled shouldBe false
        replacedCalled shouldBe false
    }

    @Test
    fun checkAddAll() {

        list.observeForever(object : LiveList.LiveListObserver<Int>() {
            override fun onItemsInserted(
                previousList: List<Int>,
                insertedIndices: List<Int>
            ) {
                previousList shouldHaveSize 0
                insertedIndices.shouldContainExactly(0, 1, 2)
            }
        })

        list.addAll(listOf(7, 42, 69))

        list.shouldContainExactly(7, 42, 69)
        anyCalled shouldBe true
        insertCalled shouldBe true
        removedCalled shouldBe false
        replacedCalled shouldBe false
    }

    @Test
    fun checkAddAllIndex() {

        list.addAll(listOf(7, 42, 69))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {
            override fun onItemsInserted(
                previousList: List<Int>,
                insertedIndices: List<Int>
            ) {
                previousList.shouldContainExactly(7, 42, 69)
                insertedIndices.shouldContainExactly(1, 2, 3, 4)
            }
        })

        list.addAll(1, listOf(13, 42, 7, 88))

        list.shouldContainExactly(7, 13, 42, 7, 88, 42, 69)
        anyCalled shouldBe true
        insertCalled shouldBe true
        removedCalled shouldBe false
        replacedCalled shouldBe false
    }

    @Test
    fun checkAddAllNew() {

        list.addAll(listOf(7, 42, 69))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {
            override fun onItemsInserted(
                previousList: List<Int>,
                insertedIndices: List<Int>
            ) {
                previousList.shouldContainExactly(7, 42, 69)
                insertedIndices.shouldContainExactly(3, 4)
            }
        })

        list.addAllNew(listOf(13, 42, 7, 88))

        list.shouldContainExactly(7, 42, 69, 13, 88)
        anyCalled shouldBe true
        insertCalled shouldBe true
        removedCalled shouldBe false
        replacedCalled shouldBe false
    }

    @Test
    fun checkRemove() {

        list.addAll(listOf(11, 21, 32, 42, 53))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {
                previousList.shouldContainExactly(11, 21, 32, 42, 53)
                removedIndices.shouldContainExactly(3)
            }
        })

        list.remove(42)

        list.shouldContainExactly(11, 21, 32, 53)
        anyCalled shouldBe true
        insertCalled shouldBe false
        removedCalled shouldBe true
        replacedCalled shouldBe false
    }

    @Test
    fun checkRemoveAt() {

        list.addAll(listOf(1, 2, 3, 4, 5))
        clearCalled()
        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {
                previousList.shouldContainExactly(1, 2, 3, 4, 5)
                removedIndices.shouldContainExactly(1)
            }
        })

        list.removeAt(1)

        list.shouldContainExactly(1, 3, 4, 5)
        anyCalled shouldBe true
        insertCalled shouldBe false
        removedCalled shouldBe true
        replacedCalled shouldBe false
    }

    @Test
    fun checkRemoveAll() {

        list.addAll(listOf(1, 2, 3, 4, 5, 2, 5))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {
                previousList.shouldContainExactly(1, 2, 3, 4, 5, 2, 5)
                removedIndices.shouldContainExactly(1, 4, 5, 6)
            }
        })

        list.removeAll(listOf(2, 5))

        list.shouldContainExactly(1, 3, 4)
        anyCalled shouldBe true
        insertCalled shouldBe false
        removedCalled shouldBe true
        replacedCalled shouldBe false
    }

    @Test
    fun checkRemoveAllIndices() {

        list.addAll(listOf(1, 2, 3, 4, 5))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {
                previousList.shouldContainExactly(1, 2, 3, 4, 5)
                removedIndices.shouldContainExactly(2, 3)
            }
        })

        list.removeAll(2, 2)

        list.shouldContainExactly(1, 2, 5)
        anyCalled shouldBe true
        insertCalled shouldBe false
        removedCalled shouldBe true
        replacedCalled shouldBe false
    }

    @Test
    fun checkClear() {

        list.addAll(listOf(1, 2, 3, 4, 5))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {
                previousList.shouldContainExactly(1, 2, 3, 4, 5)
                removedIndices.shouldContainExactly(0, 1, 2, 3, 4)
            }
        })

        list.clear()

        list shouldHaveSize 0
        anyCalled shouldBe true
        insertCalled shouldBe false
        removedCalled shouldBe true
        replacedCalled shouldBe false
    }

    @Test
    fun checkSet() {

        list.addAll(listOf(1, 2, 3, 4, 5))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsReplaced(previousList: List<Int>, replacedIndices: List<Int>) {
                previousList.shouldContainExactly(1, 2, 3, 4, 5)
                replacedIndices.shouldContainExactly(2)
            }
        })

        list[2] = 99

        list.shouldContainExactly(1, 2, 99, 4, 5)
        anyCalled shouldBe true
        insertCalled shouldBe false
        removedCalled shouldBe false
        replacedCalled shouldBe true
    }

    @Test
    fun checkSetAll() {

        list.addAll(listOf(11, 22, 33, 44, 55))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onAnyChanged(previousList: List<Int>) {
                previousList.shouldContainExactly(11, 22, 33, 44, 55)
            }

            override fun onItemsInserted(
                previousList: List<Int>,
                liveList: LiveList<Int>,
                insertedIndices: List<Int>
            ) {
                insertedIndices.shouldContainExactly(0, 1, 3, 4)
            }

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {
                removedIndices.shouldContainExactly(0, 1, 2, 3)
            }
        })

        list.setAll(listOf(66, 77, 55, 88, 55))

        list.shouldContainExactly(66, 77, 55, 88, 55)
        anyCalled shouldBe true
        insertCalled shouldBe true
        removedCalled shouldBe true
        replacedCalled shouldBe false
    }

    @Test
    fun checkReplaceAll() {

        list.addAll(listOf(1, 2, 3, 4, 5, 4))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsReplaced(previousList: List<Int>, replacedIndices: List<Int>) {
                previousList.shouldContainExactly(1, 2, 3, 4, 5, 4)
                replacedIndices.shouldContainExactly(3, 5, 1)
            }
        })

        list.replaceAll(
            buildMap {
                this[4] = 44
                this[2] = 24
            }
        )

        list.shouldContainExactly(1, 24, 3, 44, 5, 44)
        anyCalled shouldBe true
        insertCalled shouldBe false
        removedCalled shouldBe false
        replacedCalled shouldBe true
    }

    @Test
    fun checkReplaceAllSwap() {

        list.addAll(listOf(1, 2, 3, 4, 5, 4))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsReplaced(previousList: List<Int>, replacedIndices: List<Int>) {
                previousList.shouldContainExactly(1, 2, 3, 4, 5, 4)
                replacedIndices.shouldContainExactly(3, 5, 1)
            }
        })

        list.replaceAll(
            buildMap {
                this[4] = 2
                this[2] = 4
            }
        )

        list.shouldContainExactly(1, 4, 3, 2, 5, 2)
        anyCalled shouldBe true
        insertCalled shouldBe false
        removedCalled shouldBe false
        replacedCalled shouldBe true
    }

    @Test
    fun checkRetainAll() {

        list.addAll(listOf(1, 2, 3, 4, 5, 4))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {
                previousList.shouldContainExactly(1, 2, 3, 4, 5, 4)
                removedIndices.shouldContainExactly(0, 4)
            }
        })

        list.retainAll(listOf(2, 3, 4))

        list.shouldContainExactly(2, 3, 4, 4)
        anyCalled shouldBe true
        insertCalled shouldBe false
        removedCalled shouldBe true
        replacedCalled shouldBe false
    }
}