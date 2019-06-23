/*
    Copyright (C) 2014-2018 Masood Fallahpoor

    This file is part of Info Center.

    Info Center is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Info Center is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Info Center. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fallahpoor.infocenter.fragments

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.Utils
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter
import com.fallahpoor.infocenter.adapters.HeaderListItem
import com.fallahpoor.infocenter.adapters.ListItem
import com.fallahpoor.infocenter.adapters.OrdinaryListItem
import kotlinx.android.synthetic.main.fragment_storage.*
import java.io.File
import java.util.*

/**
 * StorageFragment displays some information about the size of internal and
 * external storage of the device.
 */
class StorageFragment : Fragment() {

    private var utils: Utils? = null
    private var isApiAtLeast18: Boolean = false

    private val listItems: ArrayList<ListItem>
        get() {

            val freeSpace: String = getString(R.string.sto_item_free_space)
            val totalSpace: String = getString(R.string.sto_item_total_space)

            return ArrayList<ListItem>().apply {
                add(HeaderListItem(getString(R.string.sto_item_int_storage)))
                add(OrdinaryListItem(totalSpace, internalTotal))
                add(OrdinaryListItem(freeSpace, internalFree))
                add(HeaderListItem(getString(R.string.sto_item_ext_storage)))
                add(OrdinaryListItem(totalSpace, externalTotal))
                add(OrdinaryListItem(freeSpace, externalFree))
            }

        }

    // Returns the total size of internal storage
    private val internalTotal: String
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        get() {

            val size: Long
            val internalStatFs = StatFs(Environment.getDataDirectory().absolutePath)

            size = if (isApiAtLeast18) {
                internalStatFs.totalBytes
            } else {
                internalStatFs.blockCount.toLong() * internalStatFs.blockSize.toLong()
            }

            return utils!!.getFormattedSize(size)

        }

    // Returns the free size of internal storage
    private val internalFree: String
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        get() {

            val size: Long
            val internalStatFs = StatFs(Environment.getDataDirectory().absolutePath)

            size = if (isApiAtLeast18) {
                internalStatFs.availableBytes
            } else {
                internalStatFs.availableBlocks.toLong() * internalStatFs.blockSize.toLong()
            }

            return utils!!.getFormattedSize(size)

        }

    // Returns the total size of external storage
    private val externalTotal: String
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        get() {

            val size: Long
            val extStorageStatFs: StatFs
            val extStoragePath = externalStoragePath

            if (extStoragePath == null || !File(extStoragePath).exists()) {
                return getString(R.string.sto_sub_item_ext_storage_not_available)
            }

            extStorageStatFs = StatFs(File(extStoragePath).absolutePath)

            size = if (isApiAtLeast18) {
                extStorageStatFs.totalBytes
            } else {
                extStorageStatFs.blockCount.toLong() * extStorageStatFs.blockSize.toLong()
            }

            return utils!!.getFormattedSize(size)

        }

    // Returns the free size of external storage
    private val externalFree: String
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        get() {

            val size: Long
            val extStorageStatFs: StatFs
            val extStoragePath = externalStoragePath

            if (extStoragePath == null || !File(extStoragePath).exists()) {
                return getString(R.string.sto_sub_item_ext_storage_not_available)
            }

            extStorageStatFs = StatFs(File(extStoragePath).absolutePath)

            size = if (isApiAtLeast18) {
                extStorageStatFs.availableBytes
            } else {
                extStorageStatFs.availableBlocks.toLong() * extStorageStatFs.blockSize.toLong()
            }

            return utils!!.getFormattedSize(size)

        }

    private val externalStoragePath: String?
        get() = if (!Environment.isExternalStorageEmulated() && Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED,
                ignoreCase = true
            )
        ) {
            Environment.getExternalStorageDirectory().absolutePath
        } else {
            System.getenv("SECONDARY_STORAGE")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_storage, container, false)

        utils = Utils(activity!!)
        isApiAtLeast18 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
        listView.setShadowVisible(false)
        textView.setText(R.string.sto_sub_item_ext_storage_not_available)

        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            populateListView()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_EXTERNAL_STORAGE
            )
        }

        return view

    }

    private fun populateListView() {
        listView.adapter = CustomArrayAdapter(activity, listItems)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateListView()
            } else {
                listView.emptyView = textView
            }
        }
    }

    private companion object {
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1004
    }

}