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

class ProfileFragment : Fragment() {

    lateinit var binding : FragmentProfileBinding

    private lateinit var selectedImageUri : Uri
    private lateinit var imgPath : String

    lateinit var startForResult : ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileInfo = activity?.getSharedPreferences("member", Activity.MODE_PRIVATE)?.getString(MainActivity.ID, null)

        try{
            val jsonObject = JSONObject(profileInfo)
            binding.profileID.text = jsonObject.optString("ID")
            binding.profileMail.text = jsonObject.optString("Email")
        }catch (e : JSONException){
            e.printStackTrace()
        }

        binding.profileText.text = "${binding.profileID.text}님 반갑습니다."

        val imgAddress = activity?.getSharedPreferences("${MainActivity.ID} profile", Activity.MODE_PRIVATE)?.getString(MainActivity.ID,"")
        if(imgAddress.equals("")){
            binding.profileImage.setImageResource(R.drawable.ic_baseline_person_24)
        } else{
            Glide.with(this)
                .load(imgAddress)
                .into(binding.profileImage)
        }

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == Activity.RESULT_OK){
                selectedImageUri = it.data?.data!!

                imgPath = getRealPathFromURI(selectedImageUri)!!

                Glide.with(this)
                    .load(imgPath)
                    .into(binding.profileImage)

                activity?.getSharedPreferences(
                    "${MainActivity.ID} profile", Activity.MODE_PRIVATE)
                    ?.edit()
                    ?.putString(MainActivity.ID, imgPath)
                    ?.apply()
            }
        }

        binding.profileImage.setOnClickListener {
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

        binding.profileImage.setOnLongClickListener {
            AlertDialog.Builder(requireContext()).setTitle("사진 제거").setMessage("프로필 사진을 제거하시겠습니까?")
                .setPositiveButton("YES") { dialog, which ->
                binding.profileImage.setImageResource(R.drawable.ic_baseline_person_24)
                activity?.getSharedPreferences("${MainActivity.ID} profile", Activity.MODE_PRIVATE)
                    ?.edit()
                    ?.putString(MainActivity.ID, "")
                    ?.apply()
            }.setNegativeButton("NO"){diglog, which -> }.create().show()

            return@setOnLongClickListener true
        }
    }

    private fun getRealPathFromURI(uri : Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)    //-data

        val cursor = activity?.let {
            CursorLoader(
                it.baseContext,
                uri,
                proj,
                null,
                null,
                null
            ).loadInBackground()
        }

        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()

        val result = columnIndex?.let { cursor.getString(it) }
        cursor?.close()

        return result
    }
}