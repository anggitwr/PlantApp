package com.anggitwr.myplantapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.anggitwr.myplantapp.databinding.ActivityMainBinding
import com.anggitwr.myplantapp.ml.Converted1650Vgg195
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var bitmap: Bitmap

    var imageSize = 224

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        showLoading(false)
        supportActionBar?.hide()

        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGalery.setOnClickListener { startGallery() }

//        showLoading(false)
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
//        showLoading(true)
        try {

            val model =  Converted1650Vgg195.newInstance(applicationContext)

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
            var minConfidence = 0.1f
            var maxConfidence = 0f

            for (i in confidences.indices) {

                if (confidences[i] > maxConfidence) {
                    maxConfidence  = confidences[i]
                    maxPos = i

//                    if (confidences[i] < 50){
//                        binding.tvResultkurang.setText(R.string.hasil_kurang)
//                    }

                    val classes = arrayOf(
                        "Benda lain",
                        "Daun Jambu Biji",
                        "Daun Kari",
                        "Daun Kemangi",
                        "Daun Kunyit",
                        "Daun Mint",
                        "Daun Pepaya",
                        "Daun Sirih",
                        "Daun Sirsak",
                        "Benda lain",
                        "Benda lain",
                        "Benda lain",
                        "Benda lain",
                        "Lidahbuaya",
                        "Benda lain",
                        "Teh Hijau",
                    )

                    binding.tvResult.text = classes[maxPos]
                    var s = ""
                    for (i in 0 until classes.size) {
                        s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100.0f)

                    }

//                    showLoading(false)
//                    binding.tvManfaat.isVisible
                    binding.tvConfidence.setText(s)

                    if (classes[maxPos] == "Benda lain") {
                        binding.tvDetail.setText(R.string.perubahan_hal_lain)
                        binding.tvNamabotani.setText(R.string.nama_botani_hallain)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_hallain)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_hallain)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_hallain)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_hallain)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_hallain)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_hallain)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_hallain)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_hallain)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_hallain)
                        binding.ivBuah.setImageResource(R.drawable.no_image)
                    }
                    if (classes[maxPos] == "Daun Jambu Biji") {
                        binding.tvDetail.setText(R.string.perubahan_jambubiji_detail)
                        binding.tvNamabotani.setText(R.string.nama_botani_jambubiji)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_jambubiji)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_jambubiji)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_jambubiji)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_jambubiji)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_jambubiji)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_jambubiji)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_jambubiji)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_jambubiji)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_jambubiji)
                        binding.ivBuah.setImageResource(R.drawable.jambubiji_2)
                    }
                    if (classes[maxPos] == "Daun Kari") {
                        binding.tvDetail.setText(R.string.perubahan_kari_detail)
                        binding.tvNamabotani.setText(R.string.nama_botani_kari)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_kari)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_kari)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_kari)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_kari)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_kari)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_kari)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_kari)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_kari)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_kari)
                        binding.ivBuah.setImageResource(R.drawable.buah_kari)
                    }
                    if (classes[maxPos] == "Daun Kemangi") {
                        binding.tvDetail.setText(R.string.perubahan_kemangi_detail)
                        binding.tvNamabotani.setText(R.string.nama_botani_kemangi)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_kemangi)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_kemangi)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_kemangi)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_kemangi)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_kemangi)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_kemangi)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_kemangi)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_kemangi)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_kemangi)
                        binding.ivBuah.setImageResource(R.drawable.buah_kemangi)
                    }
                    if (classes[maxPos] == "Daun Kunyit") {
                        binding.tvDetail.setText(R.string.perubahan_kunyit_detail)
                        binding.tvNamabotani.setText(R.string.nama_botani_kunyit)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_kunyit)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_kunyit)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_kunyit)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_kunyit)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_kunyit)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_kunyit)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_kunyit)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_kunyit)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_kunyit)
                        binding.ivBuah.setImageResource(R.drawable.buah_kunyit)
                    }
                    if (classes[maxPos] == "Daun Mint") {
                        binding.tvDetail.setText(R.string.perubahan_mint_detail)
                        binding.tvNamabotani.setText(R.string.nama_botani_mint)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_mint)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_mint)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_mint)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_mint)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_mint)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_mint)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_mint)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_mint)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_mint)
                        binding.ivBuah.setImageResource(R.drawable.mint_2)
                    }
                    if (classes[maxPos] == "Daun Pepaya") {
                        binding.tvDetail.setText(R.string.perubahan_pepaya_detail)
                        binding.tvNamabotani.setText(R.string.nama_botani_pepaya)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_pepaya)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_pepaya)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_pepaya)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_pepaya)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_pepaya)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_pepaya)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_pepaya)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_pepaya)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_pepaya)
                        binding.ivBuah.setImageResource(R.drawable.buah_pepaya)
                    }
                    if (classes[maxPos] == "Daun Sirih") {
                        binding.tvDetail.setText(R.string.perubahan_sirih_detail)
                        binding.tvNamabotani.setText(R.string.nama_botani_sirih)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_sirih)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_sirih)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_sirih)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_sirih)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_sirih)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_sirih)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_sirih)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_sirih)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_sirih)
                        binding.ivBuah.setImageResource(R.drawable.sirih_2)
                    }
                    if (classes[maxPos] == "Daun Sirsak") {
                        binding.tvDetail.setText(R.string.perubahan_sirsak_detail)
                        binding.tvNamabotani.setText(R.string.nama_botani_sirsak)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_sirsak)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_sirsak)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_sirsak)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_sirsak)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_sirsak)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_sirsak)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_sirsak)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_sirsak)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_sirsak)
                        binding.ivBuah.setImageResource(R.drawable.buah_sirsak)
                    }
                    if (classes[maxPos] == "Lidahbuaya") {
                        binding.tvDetail.setText(R.string.perubahan_lidahbuaya_detail)
                        binding.tvNamabotani.setText(R.string.nama_botani_lidahbuaya)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_lidahbuaya)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_lidahbuaya)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_lidahbuaya)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_lidahbuaya)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_lidahbuaya)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_lidahbuaya)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_lidahbuaya)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_lidahbuaya)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_lidahbuaya)
                        binding.ivBuah.setImageResource(R.drawable.lidahbuaya_2)
                    }
                    if (classes[maxPos] == "Teh Hijau") {
                        binding.tvDetail.setText(R.string.perubahan_tehhijau_detail)
                        binding.tvNamabotani.setText(R.string.nama_botani_tehhijau)
                        binding.tvJenistanaman.setText(R.string.karakteristik_jenistumbuhan_tehhijau)
                        binding.tvKetinggian.setText(R.string.karakteristik_ketinggian_tehhijau)
                        binding.tvPanen.setText(R.string.karakteristik_waktupanen_tehhijau)
                        binding.tvWarnaBuah.setText(R.string.karakteristik_warnabuah_tehhijau)
                        binding.tvKlasifikasiMarga.setText(R.string.klasifikasi_marga_tehhijau)
                        binding.tvKlasifikasiKeluarga.setText(R.string.klasifikasi_keluarga_tehhijau)
                        binding.tvKlasifikasiKelas.setText(R.string.klasifikasi_kelas_tehhijau)
                        binding.tvKlasifikasiDivisi.setText(R.string.klasifikasi_divisi_tehhijau)
                        binding.tvCaraPemanfaatan.setText(R.string.cara_pemanfaatan_tehhijau)
                        binding.ivBuah.setImageResource(R.drawable.tehhijau_2)
                    }
//                    confidences[i]
//                } else if (confidences[i] < minConfidence) {
////                    binding.tvResultkurang.setText(R.string.hasil_kurang)
//                    AlertDialog.Builder(this@MainActivity).apply {
//                        setTitle("Warning!!!")
//                        setMessage("akurasi terlalu rendah")
////                        create()
//                        show()
//
//                    }
//                    continue
                }
//                confidences[i]
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
            binding.btnPrediction.setOnClickListener {
//                showLoading(true)
                classification(image)
            }
        }

        else if (requestCode == 100){
            var uri = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
            val dimension = Math.min(bitmap.width, bitmap.height)
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension)
            binding.imageView.setImageBitmap(bitmap)
            bitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, false)
            binding.btnPrediction.setOnClickListener {
//                showLoading(true)
                classification(bitmap)
            }
//            onSupportNavigateUp()
        }
//        else {
//            onBackPressed()
//        }
        super.onActivityResult(requestCode, resultCode, data)
    }
//    private fun showLoading(state: Boolean) {
//        if (state) {
//            binding.progressbar?.visibility = View.VISIBLE
//        } else {
//            binding.progressbar?.visibility = View.INVISIBLE
//        }
//    }
}