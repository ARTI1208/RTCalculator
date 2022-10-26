package ru.art2000.extensions.collections

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ArrayLiveListTest {

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

//            override fun onItemsReplaced(previousList: List<Int>, replacedItems: Map<Int, Int>) {
//                replacedCalled = true
//            }
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
                Assert.assertEquals(0, previousList.size)
                Assert.assertEquals(1, insertedIndices.size)
                Assert.assertEquals(0, insertedIndices[0])
            }
        })

        list.add(7)

        Assert.assertEquals(1, list.size)
        Assert.assertEquals(7, list[0])
        Assert.assertTrue(anyCalled)
        Assert.assertTrue(insertCalled)
        Assert.assertFalse(removedCalled)
        Assert.assertFalse(replacedCalled)
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

                Assert.assertEquals(5, previousList.size)
                Assert.assertEquals(1, previousList[0])
                Assert.assertEquals(2, previousList[1])
                Assert.assertEquals(3, previousList[2])
                Assert.assertEquals(4, previousList[3])
                Assert.assertEquals(5, previousList[4])

                Assert.assertEquals(1, insertedIndices.size)
                Assert.assertEquals(2, insertedIndices[0])
            }
        })

        list.add(2, 7)

        Assert.assertEquals(6, list.size)
        Assert.assertEquals(1, list[0])
        Assert.assertEquals(2, list[1])
        Assert.assertEquals(7, list[2])
        Assert.assertEquals(3, list[3])
        Assert.assertEquals(4, list[4])
        Assert.assertEquals(5, list[5])
        Assert.assertTrue(anyCalled)
        Assert.assertTrue(insertCalled)
        Assert.assertFalse(removedCalled)
        Assert.assertFalse(replacedCalled)
    }

    @Test
    fun checkAddAll() {

        list.observeForever(object : LiveList.LiveListObserver<Int>() {
            override fun onItemsInserted(
                previousList: List<Int>,
                insertedIndices: List<Int>
            ) {
                Assert.assertEquals(0, previousList.size)
                Assert.assertEquals(3, insertedIndices.size)
                Assert.assertEquals(0, insertedIndices[0])
                Assert.assertEquals(1, insertedIndices[1])
                Assert.assertEquals(2, insertedIndices[2])
            }
        })

        list.addAll(listOf(7, 42, 69))

        Assert.assertEquals(3, list.size)
        Assert.assertEquals(7, list[0])
        Assert.assertEquals(42, list[1])
        Assert.assertEquals(69, list[2])
        Assert.assertTrue(anyCalled)
        Assert.assertTrue(insertCalled)
        Assert.assertFalse(removedCalled)
        Assert.assertFalse(replacedCalled)
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

                Assert.assertEquals(3, previousList.size)
                Assert.assertEquals(7, previousList[0])
                Assert.assertEquals(42, previousList[1])
                Assert.assertEquals(69, previousList[2])

                Assert.assertEquals(4, insertedIndices.size)
                Assert.assertEquals(1, insertedIndices[0])
                Assert.assertEquals(2, insertedIndices[1])
                Assert.assertEquals(3, insertedIndices[2])
                Assert.assertEquals(4, insertedIndices[3])
            }
        })

        list.addAll(1, listOf(13, 42, 7, 88))

        Assert.assertEquals(7, list.size)
        Assert.assertEquals(7, list[0])
        Assert.assertEquals(13, list[1])
        Assert.assertEquals(42, list[2])
        Assert.assertEquals(7, list[3])
        Assert.assertEquals(88, list[4])
        Assert.assertEquals(42, list[5])
        Assert.assertEquals(69, list[6])
        Assert.assertTrue(anyCalled)
        Assert.assertTrue(insertCalled)
        Assert.assertFalse(removedCalled)
        Assert.assertFalse(replacedCalled)
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

                Assert.assertEquals(3, previousList.size)
                Assert.assertEquals(7, previousList[0])
                Assert.assertEquals(42, previousList[1])
                Assert.assertEquals(69, previousList[2])

                Assert.assertEquals(2, insertedIndices.size)
                Assert.assertEquals(3, insertedIndices[0])
                Assert.assertEquals(4, insertedIndices[1])
            }
        })

        list.addAllNew(listOf(13, 42, 7, 88))

        Assert.assertEquals(5, list.size)
        Assert.assertEquals(7, list[0])
        Assert.assertEquals(42, list[1])
        Assert.assertEquals(69, list[2])
        Assert.assertEquals(13, list[3])
        Assert.assertEquals(88, list[4])
        Assert.assertTrue(anyCalled)
        Assert.assertTrue(insertCalled)
        Assert.assertFalse(removedCalled)
        Assert.assertFalse(replacedCalled)
    }

    @Test
    fun checkRemove() {

        list.addAll(listOf(11, 21, 32, 42, 53))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {

                Assert.assertEquals(5, previousList.size)
                Assert.assertEquals(11, previousList[0])
                Assert.assertEquals(21, previousList[1])
                Assert.assertEquals(32, previousList[2])
                Assert.assertEquals(42, previousList[3])
                Assert.assertEquals(53, previousList[4])

                Assert.assertEquals(1, removedIndices.size)
                Assert.assertEquals(3, removedIndices[0])
            }
        })

        list.remove(42)

        Assert.assertEquals(4, list.size)
        Assert.assertEquals(11, list[0])
        Assert.assertEquals(21, list[1])
        Assert.assertEquals(32, list[2])
        Assert.assertEquals(53, list[3])
        Assert.assertTrue(anyCalled)
        Assert.assertFalse(insertCalled)
        Assert.assertTrue(removedCalled)
        Assert.assertFalse(replacedCalled)
    }

    @Test
    fun checkRemoveAt() {

        list.addAll(listOf(1, 2, 3, 4, 5))
        clearCalled()
        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {

                Assert.assertEquals(5, previousList.size)
                Assert.assertEquals(1, previousList[0])
                Assert.assertEquals(2, previousList[1])
                Assert.assertEquals(3, previousList[2])
                Assert.assertEquals(4, previousList[3])
                Assert.assertEquals(5, previousList[4])

                Assert.assertEquals(1, removedIndices.size)
                Assert.assertEquals(1, removedIndices[0])
            }
        })

        list.removeAt(1)

        Assert.assertEquals(4, list.size)
        Assert.assertEquals(1, list[0])
        Assert.assertEquals(3, list[1])
        Assert.assertEquals(4, list[2])
        Assert.assertEquals(5, list[3])
        Assert.assertTrue(anyCalled)
        Assert.assertFalse(insertCalled)
        Assert.assertTrue(removedCalled)
        Assert.assertFalse(replacedCalled)
    }

    @Test
    fun checkRemoveAll() {

        list.addAll(listOf(1, 2, 3, 4, 5, 2, 5))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {

                Assert.assertEquals(7, previousList.size)
                Assert.assertEquals(1, previousList[0])
                Assert.assertEquals(2, previousList[1])
                Assert.assertEquals(3, previousList[2])
                Assert.assertEquals(4, previousList[3])
                Assert.assertEquals(5, previousList[4])
                Assert.assertEquals(2, previousList[5])
                Assert.assertEquals(5, previousList[6])

                Assert.assertEquals(4, removedIndices.size)
                Assert.assertEquals(1, removedIndices[0])
                Assert.assertEquals(4, removedIndices[1])
                Assert.assertEquals(5, removedIndices[2])
                Assert.assertEquals(6, removedIndices[3])
            }
        })

        list.removeAll(listOf(2, 5))

        Assert.assertEquals(3, list.size)
        Assert.assertEquals(1, list[0])
        Assert.assertEquals(3, list[1])
        Assert.assertEquals(4, list[2])
        Assert.assertTrue(anyCalled)
        Assert.assertFalse(insertCalled)
        Assert.assertTrue(removedCalled)
        Assert.assertFalse(replacedCalled)
    }

    @Test
    fun checkRemoveAllIndices() {

        list.addAll(listOf(1, 2, 3, 4, 5))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {

                Assert.assertEquals(5, previousList.size)
                Assert.assertEquals(1, previousList[0])
                Assert.assertEquals(2, previousList[1])
                Assert.assertEquals(3, previousList[2])
                Assert.assertEquals(4, previousList[3])
                Assert.assertEquals(5, previousList[4])

                Assert.assertEquals(2, removedIndices.size)
                Assert.assertEquals(2, removedIndices[0])
                Assert.assertEquals(3, removedIndices[1])
            }
        })

        list.removeAll(2, 2)

        Assert.assertEquals(3, list.size)
        Assert.assertEquals(1, list[0])
        Assert.assertEquals(2, list[1])
        Assert.assertEquals(5, list[2])
        Assert.assertTrue(anyCalled)
        Assert.assertFalse(insertCalled)
        Assert.assertTrue(removedCalled)
        Assert.assertFalse(replacedCalled)
    }

    @Test
    fun checkClear() {

        list.addAll(listOf(1, 2, 3, 4, 5))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {

                Assert.assertEquals(5, previousList.size)
                Assert.assertEquals(1, previousList[0])
                Assert.assertEquals(2, previousList[1])
                Assert.assertEquals(3, previousList[2])
                Assert.assertEquals(4, previousList[3])
                Assert.assertEquals(5, previousList[4])

                Assert.assertEquals(5, removedIndices.size)
                Assert.assertEquals(0, removedIndices[0])
                Assert.assertEquals(1, removedIndices[1])
                Assert.assertEquals(2, removedIndices[2])
                Assert.assertEquals(3, removedIndices[3])
                Assert.assertEquals(4, removedIndices[4])
            }
        })

        list.clear()

        Assert.assertEquals(0, list.size)
        Assert.assertTrue(anyCalled)
        Assert.assertFalse(insertCalled)
        Assert.assertTrue(removedCalled)
        Assert.assertFalse(replacedCalled)
    }

    @Test
    fun checkSet() {

        list.addAll(listOf(1, 2, 3, 4, 5))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

//            override fun onItemsReplaced(previousList: List<Int>, replacedItems: Map<Int, Int>) {
//                Assert.assertEquals(5, previousList.size)
//                Assert.assertEquals(1, previousList[0])
//                Assert.assertEquals(2, previousList[1])
//                Assert.assertEquals(3, previousList[2])
//                Assert.assertEquals(4, previousList[3])
//                Assert.assertEquals(5, previousList[4])
//
//                Assert.assertEquals(1, replacedItems.size)
//                val entry = replacedItems.entries.first()
//                Assert.assertEquals(1, entry.key)
//                Assert.assertEquals(2, entry.value)
//            }

            override fun onItemsReplaced(previousList: List<Int>, replacedIndices: List<Int>) {
                Assert.assertEquals(5, previousList.size)
                Assert.assertEquals(1, previousList[0])
                Assert.assertEquals(2, previousList[1])
                Assert.assertEquals(3, previousList[2])
                Assert.assertEquals(4, previousList[3])
                Assert.assertEquals(5, previousList[4])

                Assert.assertEquals(1, replacedIndices.size)
                Assert.assertEquals(2, replacedIndices[0])
            }
        })

        list[2] = 99

        Assert.assertEquals(5, list.size)
        Assert.assertEquals(1, list[0])
        Assert.assertEquals(2, list[1])
        Assert.assertEquals(99, list[2])
        Assert.assertEquals(4, list[3])
        Assert.assertEquals(5, list[4])
        Assert.assertTrue(anyCalled)
        Assert.assertFalse(insertCalled)
        Assert.assertFalse(removedCalled)
        Assert.assertTrue(replacedCalled)
    }

    @Test
    fun checkSetAll() {

        list.addAll(listOf(11, 22, 33, 44, 55))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onAnyChanged(previousList: List<Int>) {
                Assert.assertEquals(5, previousList.size)
                Assert.assertEquals(11, previousList[0])
                Assert.assertEquals(22, previousList[1])
                Assert.assertEquals(33, previousList[2])
                Assert.assertEquals(44, previousList[3])
                Assert.assertEquals(55, previousList[4])
            }

            override fun onItemsInserted(
                previousList: List<Int>,
                liveList: LiveList<Int>,
                insertedIndices: List<Int>
            ) {

                Assert.assertEquals(4, insertedIndices.size)
                Assert.assertEquals(0, insertedIndices[0])
                Assert.assertEquals(1, insertedIndices[1])
                Assert.assertEquals(3, insertedIndices[2])
                Assert.assertEquals(4, insertedIndices[3])
            }

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {

                Assert.assertEquals(4, removedIndices.size)
                Assert.assertEquals(0, removedIndices[0])
                Assert.assertEquals(1, removedIndices[1])
                Assert.assertEquals(2, removedIndices[2])
                Assert.assertEquals(3, removedIndices[3])
            }

            override fun onItemsReplaced(
                previousList: List<Int>,
                liveList: LiveList<Int>,
                replacedIndices: List<Int>
            ) {

            }
        })

        list.setAll(listOf(66, 77, 55, 88, 55))

        Assert.assertEquals(5, list.size)
        Assert.assertEquals(66, list[0])
        Assert.assertEquals(77, list[1])
        Assert.assertEquals(55, list[2])
        Assert.assertEquals(88, list[3])
        Assert.assertEquals(55, list[4])
        Assert.assertTrue(anyCalled)
        Assert.assertTrue(insertCalled)
        Assert.assertTrue(removedCalled)
        Assert.assertFalse(replacedCalled)
    }

    @Test
    fun checkReplaceAll() {

        list.addAll(listOf(1, 2, 3, 4, 5, 4))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsReplaced(previousList: List<Int>, replacedIndices: List<Int>) {
                Assert.assertEquals(6, previousList.size)
                Assert.assertEquals(1, previousList[0])
                Assert.assertEquals(2, previousList[1])
                Assert.assertEquals(3, previousList[2])
                Assert.assertEquals(4, previousList[3])
                Assert.assertEquals(5, previousList[4])
                Assert.assertEquals(4, previousList[5])

                Assert.assertEquals(3, replacedIndices.size)
                Assert.assertEquals(3, replacedIndices[0])
                Assert.assertEquals(5, replacedIndices[1])
                Assert.assertEquals(1, replacedIndices[2])
            }
        })

        list.replaceAll(
            buildMap {
                this[4] = 44
                this[2] = 24
            }
        )

        Assert.assertEquals(6, list.size)
        Assert.assertEquals(1, list[0])
        Assert.assertEquals(24, list[1])
        Assert.assertEquals(3, list[2])
        Assert.assertEquals(44, list[3])
        Assert.assertEquals(5, list[4])
        Assert.assertEquals(44, list[5])
        Assert.assertTrue(anyCalled)
        Assert.assertFalse(insertCalled)
        Assert.assertFalse(removedCalled)
        Assert.assertTrue(replacedCalled)
    }

    @Test
    fun checkReplaceAllSwap() {

        list.addAll(listOf(1, 2, 3, 4, 5, 4))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsReplaced(previousList: List<Int>, replacedIndices: List<Int>) {
                Assert.assertEquals(6, previousList.size)
                Assert.assertEquals(1, previousList[0])
                Assert.assertEquals(2, previousList[1])
                Assert.assertEquals(3, previousList[2])
                Assert.assertEquals(4, previousList[3])
                Assert.assertEquals(5, previousList[4])
                Assert.assertEquals(4, previousList[5])

                Assert.assertEquals(3, replacedIndices.size)
                Assert.assertEquals(3, replacedIndices[0])
                Assert.assertEquals(5, replacedIndices[1])
                Assert.assertEquals(1, replacedIndices[2])
            }
        })

        list.replaceAll(
            buildMap {
                this[4] = 2
                this[2] = 4
            }
        )

        Assert.assertEquals(6, list.size)
        Assert.assertEquals(1, list[0])
        Assert.assertEquals(4, list[1])
        Assert.assertEquals(3, list[2])
        Assert.assertEquals(2, list[3])
        Assert.assertEquals(5, list[4])
        Assert.assertEquals(2, list[5])
        Assert.assertTrue(anyCalled)
        Assert.assertFalse(insertCalled)
        Assert.assertFalse(removedCalled)
        Assert.assertTrue(replacedCalled)
    }

    @Test
    fun checkReplaceAllOp() {

        list.addAll(listOf(1, 2, 3, 4, 5, 4))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsReplaced(previousList: List<Int>, replacedIndices: List<Int>) {
                Assert.assertEquals(6, previousList.size)
                Assert.assertEquals(1, previousList[0])
                Assert.assertEquals(2, previousList[1])
                Assert.assertEquals(3, previousList[2])
                Assert.assertEquals(4, previousList[3])
                Assert.assertEquals(5, previousList[4])
                Assert.assertEquals(4, previousList[5])

                Assert.assertEquals(6, replacedIndices.size)
                Assert.assertEquals(0, replacedIndices[0])
                Assert.assertEquals(1, replacedIndices[1])
                Assert.assertEquals(2, replacedIndices[2])
                Assert.assertEquals(3, replacedIndices[3])
                Assert.assertEquals(4, replacedIndices[4])
                Assert.assertEquals(5, replacedIndices[5])
            }
        })

        list.replaceAll {
            it + 1
        }

        Assert.assertEquals(6, list.size)
        Assert.assertEquals(2, list[0])
        Assert.assertEquals(3, list[1])
        Assert.assertEquals(4, list[2])
        Assert.assertEquals(5, list[3])
        Assert.assertEquals(6, list[4])
        Assert.assertEquals(5, list[5])
        Assert.assertTrue(anyCalled)
        Assert.assertFalse(insertCalled)
        Assert.assertFalse(removedCalled)
        Assert.assertTrue(replacedCalled)
    }

    @Test
    fun checkRetainAll() {

        list.addAll(listOf(1, 2, 3, 4, 5, 4))
        clearCalled()

        list.observeForever(object : LiveList.LiveListObserver<Int>() {

            override fun onItemsRemoved(previousList: List<Int>, removedIndices: List<Int>) {

                Assert.assertEquals(6, previousList.size)
                Assert.assertEquals(1, previousList[0])
                Assert.assertEquals(2, previousList[1])
                Assert.assertEquals(3, previousList[2])
                Assert.assertEquals(4, previousList[3])
                Assert.assertEquals(5, previousList[4])
                Assert.assertEquals(4, previousList[5])

                Assert.assertEquals(2, removedIndices.size)
                Assert.assertEquals(0, removedIndices[0])
                Assert.assertEquals(4, removedIndices[1])
            }
        })

        list.retainAll(listOf(2, 3, 4))

        Assert.assertEquals(4, list.size)
        Assert.assertEquals(2, list[0])
        Assert.assertEquals(3, list[1])
        Assert.assertEquals(4, list[2])
        Assert.assertEquals(4, list[3])
        Assert.assertTrue(anyCalled)
        Assert.assertFalse(insertCalled)
        Assert.assertTrue(removedCalled)
        Assert.assertFalse(replacedCalled)
    }
}