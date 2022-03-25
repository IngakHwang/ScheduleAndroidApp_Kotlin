package com.example.schedule

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

// extend function
fun Context.getPreference(prefName:String) = getSharedPreferences(prefName,Context.MODE_PRIVATE)
fun Context.getPreferenceEditor(prefName:String) = getPreference(prefName).edit()

//fun Context.toast(msg:String, time:Int = Toast.LENGTH_SHORT)
//    = Toast.makeText(this,msg,time).show()

fun Fragment.showToast(msg:String, time:Int = Toast.LENGTH_SHORT)
    = Toast.makeText(requireContext(),msg,time).show()

fun NavDirections.move(fragment:Fragment) = with(fragment) {
    findNavController().navigate(this@move)
}