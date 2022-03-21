package com.example.schedule

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private var itemList = mutableListOf<MainData>()

    val liveDataItemList = MutableLiveData<MutableList<MainData>>()           // 변경/관찰가능한 LiveData

    fun getItemList() : MutableList<MainData>{
        return itemList
    }

    fun setItemList(newItemList : MutableList<MainData>){
        itemList = newItemList
        liveDataItemList.value = itemList
    }

    fun addItem(item: MainData){
        itemList.add(item)
        liveDataItemList.value = itemList
    }

    fun removeAll(){
        itemList.clear()
    }

    fun sizeItem() : Int{
        return itemList.size
    }
}