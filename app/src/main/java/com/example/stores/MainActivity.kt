package com.example.stores

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /*mBinding.btnSave.setOnClickListener {
            val storeEntity=StoreEntity(name = mBinding.etName.text.toString().trim())

            Thread{

            }.start()

            mAdapter.add(storeEntity)
        }*/

        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }

        setupRecyclerView()

    }

    /**
     * Launch the fragment
     */
    private fun launchEditFragment(args: Bundle? = null) {

        val fragment = EditStoreFragment()

        if(args != null) fragment.arguments = args

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        //add the fragment to te view (in this case, mainContainer)
        fragmentTransaction.add(R.id.mainContainer, fragment)
        fragmentTransaction.addToBackStack(null) //back to the last activity, if not it will close the app
        fragmentTransaction.commit()

        hideFab()


    }


    /**
     * Set up the recyclerView to be ready for use it
     */
    private fun setupRecyclerView(){


        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this,resources.getInteger(R.integer.main_columns))
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
    override fun OnClickListener(storeId: Long) {

        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)

        launchEditFragment(args)
    }

    /**
     * Override the click on favorite button
     */
    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite

        val queue = LinkedBlockingQueue<StoreEntity>()

        Thread{
             StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        updateStore(queue.take())
    }


    /**
     * Override the onDelete method
     */
    override fun onDeleteStore(storeEntity: StoreEntity) {

        val items =resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dailog_options_title)
            .setItems(items) { _, i ->

                when (i) {
                    0 -> confirmDelete(storeEntity)

                    1 -> callStore(storeEntity.phone)

                    2 -> goToWebSite(storeEntity.website)

                }

            }
            .show()

    }


    /**
     * Open a browser with the indicate url
     */
    private fun goToWebSite(website: String) {

        if (website.isEmpty()) {

            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_SHORT).show()

        } else if (URLUtil.isValidUrl(website)) {

            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_SHORT).show()

        }  else {

            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)

            }

            startIntent(websiteIntent)

        }
    }


    /**
     * Open the call app, with the store's phone number
     */
    private fun callStore(phone: String){

        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }

        startIntent(callIntent)

    }


    /**
     * Open a intent
     * //with android 30 o greater, need to add queries to manifest.xml
     */
    private fun startIntent(intent: Intent) {


        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
        else {
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_SHORT).show()
        }

    }


    /**
     * Show an alertDialog, asking for delete or cancel
     */
    private fun confirmDelete(storeEntity: StoreEntity) {

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->

                val queue = LinkedBlockingQueue<StoreEntity>()

                thread {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                    queue.add(storeEntity)
                }.start()

                mAdapter.delete(queue.take())

            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()

    }



    /**
     * MAINAUX
     */

    override fun hideFab(isVisible: Boolean) {

        if(isVisible) mBinding.fab.show()
        else mBinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {

        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {

        mAdapter.update(storeEntity)
    }
}
























