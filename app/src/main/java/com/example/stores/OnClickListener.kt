package com.example.stores

/**
 * interface that is made to override the OnclickListener, to adapt it to our specific behaviour
 */
interface OnClickListener {

    fun OnClickListener(storeEntity: StoreEntity)

    fun onFavoriteStore(storeEntity: StoreEntity)
}