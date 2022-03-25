package com.example.schedule

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.example.schedule.databinding.FragmentProfileBinding
import org.json.JSONException
import org.json.JSONObject

class ProfileFragment_convert : Fragment() {

    var _binding : FragmentProfileBinding? = null
    val binding:FragmentProfileBinding
        get() = _binding ?: throw IllegalStateException()

    private lateinit var selectedImageUri : Uri
    private lateinit var imgPath : String

    lateinit var startForResult : ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentProfileBinding.inflate(inflater, container, false).apply {
        _binding = this
    }.root

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        val profileInfo = activity?.getSharedPreferences("member", Activity.MODE_PRIVATE)?.getString(MainActivity.ID, null)

        try{
            val jsonObject = JSONObject(profileInfo)
            profileID.text = jsonObject.optString("ID")
            profileMail.text = jsonObject.optString("Email")
        }catch (e : JSONException){
            e.printStackTrace()
        }

        profileText.text = "${profileID.text}님 반갑습니다."

        val profilePreference = requireContext().getPreference("${MainActivity.ID} profile")

        profilePreference
            .getString(MainActivity.ID,null)
            ?.let { imgAddress ->
                when {
                    imgAddress.isEmpty() -> {
                        profileImage.setImageResource(R.drawable.ic_baseline_person_24)
                    }
                    else -> {
                        Glide.with(this@ProfileFragment_convert)
                            .load(imgAddress)
                            .into(profileImage)
                    }
                }
            }

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when(result.resultCode) {
                Activity.RESULT_OK -> {
                    result.data
                        ?.data
                        ?.let { imageUri ->
                            getRealPathFromURI(imageUri)
                        }
                        ?.let { path ->
                            Glide.with(this@ProfileFragment_convert)
                                .load(path)
                                .into(profileImage)

                            profilePreference
                                .edit()
                                .putString(MainActivity.ID, path)
                                .apply()
                        }
                        ?: run {
                            // Null 일 때 어떻게 할 건지
                        }
                }
                else -> {
                    // Null 일 때 어떻게 할 건지
                }
            }
        }

        profileImage.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ActivityCompat.requestPermissions(requireActivity(),permission,3)
                }
                else{
                    val photoPickerIntent = Intent(Intent.ACTION_PICK).setType("image/*")
                    startForResult.launch(photoPickerIntent)
                }
            } else{
                val photoPickerIntent = Intent(Intent.ACTION_PICK).setType("image/*")
                startForResult.launch(photoPickerIntent)
            }
        }

        profileImage.setOnLongClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("사진 제거")
                .setMessage("프로필 사진을 제거하시겠습니까?")
                .setPositiveButton("YES") { dialog, which ->
                    profileImage.setImageResource(R.drawable.ic_baseline_person_24)
                    activity?.getSharedPreferences("${MainActivity.ID} profile", Activity.MODE_PRIVATE)
                        ?.edit()
                        ?.putString(MainActivity.ID, "")
                        ?.apply()
                }
                .setNegativeButton("NO"){ diglog, which ->

                }
                .create()
                .show()

            return@setOnLongClickListener true
        }
    }

    private fun getRealPathFromURI(uri : Uri): String? {
        return CursorLoader(
            requireContext(),
            uri,
            arrayOf(MediaStore.Images.Media.DATA),
            null,
            null,
            null
        )
            .loadInBackground()
            ?.run {
                val columnIndex = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                moveToFirst()
                getString(columnIndex).also {
                    close()
                }
            }
    }
}