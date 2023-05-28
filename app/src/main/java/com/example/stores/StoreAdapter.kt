package com.example.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stores.databinding.ItemStoreBinding

/**
 * The custom adapter for the Stores recyclerView, that list all the stores in DB
 */
class StoreAdapter(private var stores: MutableList<StoreEntity>, private var listener: OnClickListener)
    : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    private  lateinit var mContext: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val binding = ItemStoreBinding.bind(view)

        fun setListener(storeEntity: StoreEntity){
            binding.root.setOnClickListener { listener.OnClickListener(storeEntity) }
            binding.cbFavorite.setOnClickListener { listener.onFavoriteStore(storeEntity)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = stores.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val store = stores.get(position)

        with(holder){
            setListener(store)
            binding.tvName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite
        }
    }

    fun add(storeEntity: StoreEntity) {

        stores.add(storeEntity)
        notifyDataSetChanged()

    }

    /**
     * update the stores list
     */
    fun setStores(stores: MutableList<StoreEntity>) {

        this.stores = stores
        notifyDataSetChanged()
    }

    /**
     * update a single store
     */
    fun update(storeEntity: StoreEntity) {

        val index = stores.indexOf(storeEntity)

        if(index != -1) {
            stores[index] = storeEntity
            notifyItemChanged(index)
        }

    }
}


































