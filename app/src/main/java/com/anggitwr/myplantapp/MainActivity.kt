package com.anggitwr.myplantapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import com.anggitwr.myplantapp.databinding.ActivityMainBinding
import com.anggitwr.myplantapp.ml.ConvertedModelav472Lite
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var bitmap: Bitmap

    var imageSize = 224

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGalery.setOnClickListener { startGallery() }

    }

    @Suppress("DEPRECATION")
    private fun startCamera(){
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 1)
        } else {
            //Request camera permission if we don't have it.
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    private fun startGallery(){
        val intent = Intent()

        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, 100)

    }

    private fun classification(image : Bitmap?){
        try {
            val model = ConvertedModelav472Lite.newInstance(applicationContext)

            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)

            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            // get 1D array of 200 * 200 pixels in image
            val intValues = IntArray(imageSize * imageSize)
            image!!.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++] // RGB
                    byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 255f))
                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray

            // find the index of the class with the biggest confidence.
            var maxPos = 0
            var minConfidence = 0.5111f
            var maxConfidence = 0f



            for (i in confidences.indices) {

                if (confidences[i] > minConfidence) {
                    maxConfidence  = confidences[i]
                    maxPos = i

                    val classes = arrayOf(
                        "Jambu Biji",
                        "Kari",
                        "Kemangi",
                        "Kunyit",
                        "Mint",
                        "Pepaya",
                        "Sirih",
                        "Sirsak",
                        "Lidah Buaya",
                        "Teh Hijau",
                    )

                    binding.tvResult.text = classes[maxPos]
                    var s = ""
                    for (i in 0 until classes.size) {
                        s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100.0f)

                    }

                    if (classes[maxPos] < "99"){
                        binding.tvResultkurang.setText(R.string.hasil_kurang)
                    }

                    binding.tvConfidence.setText(s)

                    if (classes[maxPos] == "Jambu Biji") {
                        binding.tvDetail.setText(R.string.perubahan_jambubiji_detail)
                    }
                    if (classes[maxPos] == "Kari") {
                        binding.tvDetail.setText(R.string.perubahan_kari_detail)
                    }
                    if (classes[maxPos] == "Kemangi") {
                        binding.tvDetail.setText(R.string.perubahan_kemangi_detail)
                    }
                    if (classes[maxPos] == "Kunyit") {
                        binding.tvDetail.setText(R.string.perubahan_kunyit_detail)
                    }
                    if (classes[maxPos] == "Mint") {
                        binding.tvDetail.setText(R.string.perubahan_mint_detail)
                    }
                    if (classes[maxPos] == "Pepaya") {
                        binding.tvDetail.setText(R.string.perubahan_pepaya_detail)
                    }
                    if (classes[maxPos] == "Sirih") {
                        binding.tvDetail.setText(R.string.perubahan_sirih_detail)
                    }
                    if (classes[maxPos] == "Sirsak") {
                        binding.tvDetail.setText(R.string.perubahan_sirsak_detail)
                    }
                    if (classes[maxPos] == "Lidahbuaya") {
                        binding.tvDetail.setText(R.string.perubahan_lidahbuaya_detail)
                    }
                    if (classes[maxPos] == "Teh Hijau") {
                        binding.tvDetail.setText(R.string.perubahan_tehhijau_detail)
                    }
                    confidences[i]
//                } else if (confidences[i] < minConfidence) {
////                    binding.tvResultkurang.setText(R.string.hasil_kurang)
//                    AlertDialog.Builder(this@MainActivity).apply {
//                        setTitle("Warning!!!")
//                        setMessage("akurasi terlalu rendah")
//                        setPositiveButton("OK"){ _, _ ->
//
//                        }
//
//                        show()
//                    }
//                    continue
                }
                confidences[i]
            }




            // Releases model resources if no longer used.
            model.close()
        } catch (e: Exception){
            binding.tvDetail.setText(R.string.not_identification)}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            var image = data!!.extras!!["data"] as Bitmap?
            val dimension = Math.min(image!!.width, image.height)
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
            binding.imageView.setImageBitmap(image)
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
            binding.btnPrediction.setOnClickListener { classification(image) }
        }

        else if (requestCode == 100){
            var uri = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
            val dimension = Math.min(bitmap.width, bitmap.height)
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension)
            binding.imageView.setImageBitmap(bitmap)
            bitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, false)
            binding.btnPrediction.setOnClickListener { classification(bitmap) }
//            onSupportNavigateUp()
        }
//        else {
//            onBackPressed()
//        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}