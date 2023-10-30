package com.ead.project.dreamer.data.utils

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.nativead.NativeAd
import io.ktor.util.reflect.instanceOf

class AdOrder(
    private var items : MutableList<Any>,
    private var ads : List<NativeAd>
) {

    private val nativeAdClass = NativeAd::class.java

    fun setup(list : List<Any>,anyList : MutableLiveData<List<Any>>) {
        if (list.isEmpty()) return

        if (list.all { nativeAdClass.isInstance(it) }) {
            setAdList(list.filterIsInstance<NativeAd>())
            anyList.postValue(combineItems())
        }
        else {
            setItemsList(list)
            if(ads.isNotEmpty()) {
                anyList.postValue(combineItems())
            }
            else {
                anyList.postValue(list)
            }
        }
    }

    private fun combineItems() : List<Any> {
        return try {
            if (this@AdOrder.items.isNotEmpty()) {
                val offset: Int = this@AdOrder.items.size.div(ads.size) + 1
                var index = 0

                if (!this@AdOrder.items.any { nativeAdClass.isInstance(it) }) {
                    for (ad in ads) {
                        this@AdOrder.items.add(index, ad)
                        index += offset
                    }
                }
            }
            this@AdOrder.items
        } catch (e : Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun setItemsList(list : List<Any>) {
        this@AdOrder.items = list.toMutableList()
    }

    private fun setAdList(list: List<NativeAd>) {
        this@AdOrder.ads = list
    }

    companion object {
        fun isFirstItemAd(list : List<Any>) : Boolean {
            val firstItem = list.firstOrNull()?:return false
            return firstItem.instanceOf(NativeAd::class)
        }
    }
}