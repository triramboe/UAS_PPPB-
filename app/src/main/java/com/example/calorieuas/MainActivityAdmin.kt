package com.example.calorieuas

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calorieuas.databinding.ActivityMainAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivityAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityMainAdminBinding

    lateinit var mFoodItemDao: FoodItemDao
    lateinit var executorService: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            btnAddMakanan.setOnClickListener{
                showAddFood()
            }
            recyclerAddMakananAdmin.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 || dy < 0) {
                        addMakananAdmin.visibility = View.GONE
                    }
                }
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        addMakananAdmin.visibility = View.VISIBLE
                    }
                }
            })

        }

        executorService = Executors.newSingleThreadExecutor()
        val db = AppDatabase.getDatabase(this)
        mFoodItemDao = db!!.foodItemDao()!!
    }

    override fun onResume() {
        super.onResume()
        syncDataToFirebase()
        getAllNotes()
    }

    private fun getAllNotes() {
        mFoodItemDao.allFoodItem.observe(this){
            foods ->
            val listAdapter = FoodAdapter(foods){
                selectedFood->
                showAddFood(selectedFood)
            }

            binding.recyclerAddMakananAdmin.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(this@MainActivityAdmin)
            }
        }
    }

    private fun showAddFood(foodItem: FoodItem? = null) {
        val foodDial = AdminDialog(this, foodItem)
        foodDial.show(supportFragmentManager, "Makanan")
    }

    private fun syncDataToFirebase() {
        // Cek koneksi internet
        if (isConnectedToInternet()) {
            executorService.execute {
                // Ambil data yang belum di-sync dari Room
                val unsyncedData = mFoodItemDao.getUnsyncedFoodItems()

                // Kirim data ke Firebase
                for (item in unsyncedData) {
                    // Implementasi logika pengiriman data ke Firebase
                    sendDataToFirebase(item)

                    // Setelah berhasil di-sync, tandai data sebagai sudah di-sync
                    mFoodItemDao.updateSyncStatus(item.id, true)
                }
            }
        }
    }


    private suspend fun updateSyncStatusInDatabase(itemId: Long, isSynced: Boolean) {
        withContext(Dispatchers.IO) {
            mFoodItemDao.updateSyncStatus(itemId, isSynced)
        }
    }

    private fun sendDataToFirebase(foodItem: FoodItem) {
        val firestore = FirebaseFirestore.getInstance()
        val foodCollection = firestore.collection("food_items")

        val data = hashMapOf(
            "itemName" to foodItem.itemName,
            "calories" to foodItem.calories,
            "deskripsi" to foodItem.deskripsi
        )

        // Tambahkan data ke Firebase Firestore
        foodCollection.add(data)
            .addOnSuccessListener { documentReference ->
                // Data berhasil ditambahkan
                val firebaseItemId = documentReference.id

                // Update status di Room bahwa data sudah di-sync ke Firebase dengan ID yang baru
                runBlocking {
                    updateSyncStatusInDatabase(foodItem.id, true)
                    mFoodItemDao.updateFirebaseId(foodItem.id, firebaseItemId)
                }
            }
            .addOnFailureListener { e ->
                // Gagal menambahkan data ke Firebase
                Log.e(TAG, "Gagal menambahkan data ke Firebase", e)
            }
    }


    private fun isConnectedToInternet(): Boolean {
        // Implementasi cek koneksi internet
        // ...

        return true // Gantilah dengan implementasi sesuai kebutuhan
    }

}