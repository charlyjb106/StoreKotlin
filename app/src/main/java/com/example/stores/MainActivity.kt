package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnSave.setOnClickListener {
            val storeEntity=StoreEntity(name = mBinding.etName.text.toString().trim())

            Thread{

            }.start()

            mAdapter.add(storeEntity)
        }

        setupRecyclerView()

    }


    /**
     * Set up the recyclerView to be ready for use it
     */
    private fun setupRecyclerView(){

        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this,2)
        getStores()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }


    private fun getStores() {

        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()

        Thread{
            val stores = StoreApplication.database.storeDao().getAllStores()
             queue.add(stores) //queue receive the stores when it ready
        }.start()

        mAdapter.setStores(queue.take()) //when queue is ready, add it to the adapter
    }


    /**
     * OnClickListener override class
     */
    override fun OnClickListener(storeEntity: StoreEntity) {

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite

        val queue = LinkedBlockingQueue<StoreEntity>()

        Thread{
             StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        mAdapter.update(queue.take())
    }
}
