/*
    Copyright (C) 2014-2016 Masood Fallahpoor

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
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.AsyncTask
import android.os.Bundle
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
import de.halfbit.pinnedsection.PinnedSectionListView
import kotlinx.android.synthetic.main.fragment_camera.*
import java.util.*

/**
 * CameraFragment displays some properties of the camera(s) of the device,
 * if any.
 */
// CHECK Is it possible for a device to have front camera but not rear camera?
// TODO Update the code to use the new Camera API introduced in Android Lollipop.
class CameraFragment : Fragment() {

    private var utils: Utils? = null
    private var getCameraParamsTask: GetCameraParamsTask? = null

    private// how many cameras do we have in here?!
    val listItems: ArrayList<ListItem>
        get() {

            val items = ArrayList<ListItem>()

            when (Camera.getNumberOfCameras()) {
                1 -> items.addAll(getCameraParams(CameraInfo.CAMERA_FACING_BACK))
                2 -> {
                    items.add(HeaderListItem(getString(R.string.cam_item_back_camera)))
                    items.addAll(getCameraParams(CameraInfo.CAMERA_FACING_BACK))
                    items.add(HeaderListItem(getString(R.string.cam_item_front_camera)))
                    items.addAll(getCameraParams(CameraInfo.CAMERA_FACING_FRONT))
                }
            }

            return items

        }

    private val itemsArray: Array<String>
        get() = arrayOf(
            getString(R.string.cam_item_camera_quality),
            getString(R.string.cam_item_picture_size),
            getString(R.string.cam_item_picture_format),
            getString(R.string.cam_item_supported_video_sizes),
            getString(R.string.cam_item_focal_length),
            getString(R.string.cam_item_antibanding),
            getString(R.string.cam_item_auto_exposure_lock),
            getString(R.string.cam_item_auto_white_balance_lock),
            getString(R.string.cam_item_color_effect),
            getString(R.string.cam_item_flash),
            getString(R.string.cam_item_scene_selection),
            getString(R.string.cam_item_zoom),
            getString(R.string.cam_item_video_snapshot)
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        utils = Utils(activity!!)

        (listView as PinnedSectionListView).setShadowVisible(false)

        textView.setText(R.string.cam_no_camera)

        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            populateListView()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        }

        return view

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateListView()
            } else {
                listView.emptyView = textView
            }
        }
    }

    override fun onDestroy() {

        super.onDestroy()

        if (getCameraParamsTask != null) {
            getCameraParamsTask!!.cancel(true)
            getCameraParamsTask = null
        }

        if (progressWheel != null && progressWheel.isSpinning) {
            progressWheel.stopSpinning()
        }

    }

    private fun populateListView() {
        if (Camera.getNumberOfCameras() > 0) {
            getCameraParamsTask = GetCameraParamsTask()
            getCameraParamsTask!!.execute()
        } else {
            listView.emptyView = textView
        }
    }

    private fun getCameraInstance(cameraId: Int): Camera? {

        var camera: Camera? = null

        try {
            camera = Camera.open(cameraId)
        } catch (ignored: Exception) {
        }

        return camera

    }

    private fun getCameraParams(cameraFacing: Int): ArrayList<ListItem> {

        val camParams = ArrayList<ListItem>()
        val cameraParams: Camera.Parameters
        val camera: Camera? = getCameraInstance(cameraFacing)
        val items: Array<String>
        val subItems: ArrayList<String>

        if (camera != null) {
            cameraParams = camera.parameters
            releaseCamera(camera)

            items = itemsArray
            subItems = getParameters(cameraParams)

            for (i in items.indices) {
                camParams.add(OrdinaryListItem(items[i], subItems[i]))
            }

        } else { // camera is busy or for some other reason camera isn't available
            if (cameraFacing == CameraInfo.CAMERA_FACING_BACK) {
                camParams.add(
                    OrdinaryListItem(
                        getString(R.string.cam_sub_item_back_camera_info_unavailable), ""
                    )
                )
            } else {
                camParams.add(
                    OrdinaryListItem(
                        getString(R.string.cam_sub_item_front_camera_info_unavailable), ""
                    )
                )
            }

        }

        return camParams

    } // end method getCameraParams

    private fun getParameters(cameraParams: Camera.Parameters): ArrayList<String> {

        val params = ArrayList<String>()
        val supported = getString(R.string.supported)
        val unsupported = getString(R.string.unsupported)

        params.add(getMegaPixels(cameraParams))
        params.add(getPictureSize(cameraParams))
        params.add(getPictureFormat(cameraParams))
        params.add(getSupportedVideoSizes(cameraParams))
        params.add(getFocalLength(cameraParams))
        params.add(if (cameraParams.antibanding != null) supported else unsupported)
        params.add(if (cameraParams.isAutoExposureLockSupported) supported else unsupported)
        params.add(if (cameraParams.isAutoWhiteBalanceLockSupported) supported else unsupported)
        params.add(if (cameraParams.colorEffect != null) supported else unsupported)
        params.add(if (cameraParams.flashMode != null) supported else unsupported)
        params.add(if (cameraParams.sceneMode != null) supported else unsupported)
        params.add(if (cameraParams.isZoomSupported) supported else unsupported)
        params.add(if (cameraParams.isVideoSnapshotSupported) supported else unsupported)

        return params

    } // end method getParameters

    private fun getMegaPixels(cameraParams: Camera.Parameters): String {

        val pictureSizes = cameraParams.supportedPictureSizes
        var strMegaPixels = getString(R.string.unknown)
        val dblMegaPixels: Double
        var maxHeight = Integer.MIN_VALUE
        var maxWidth = Integer.MIN_VALUE

        for (pictureSize in pictureSizes) {
            if (pictureSize.width > maxWidth) {
                maxWidth = pictureSize.width
            }
            if (pictureSize.height > maxHeight) {
                maxHeight = pictureSize.height
            }
        }

        if (maxWidth != Integer.MIN_VALUE && maxHeight != Integer.MIN_VALUE) {
            dblMegaPixels = (maxWidth * maxHeight).toDouble() / 1000000
            strMegaPixels = String.format(
                utils!!.locale, "%.1f %s",
                dblMegaPixels, getString(R.string.cam_sub_item_mp)
            )
        }

        return strMegaPixels

    } // end method getMegaPixels

    private fun getPictureSize(cameraParams: Camera.Parameters): String {
        val width = cameraParams.pictureSize.width
        val height = cameraParams.pictureSize.height
        return String.format(utils!!.locale, "%d x %d", width, height)
    }

    private fun getPictureFormat(cameraParams: Camera.Parameters): String {

        val intFormat = cameraParams.pictureFormat
        val format: String

        format = when (intFormat) {
            ImageFormat.JPEG -> "JPEG"
            ImageFormat.RGB_565 -> "RGB"
            else -> getString(R.string.unknown)
        }

        return format

    }

    private fun getSupportedVideoSizes(cameraParams: Camera.Parameters): String {

        var i = 0
        val videoSizes = StringBuilder()
        val supportedVideoSizes =
            cameraParams.supportedVideoSizes ?: return getString(R.string.unknown)

        while (i < supportedVideoSizes.size - 1) {
            videoSizes.append(
                String.format(
                    utils!!.locale, "%d x %d, ",
                    supportedVideoSizes[i].width,
                    supportedVideoSizes[i].height
                )
            )
            i++
        }

        videoSizes.append(
            String.format(
                utils!!.locale, "%d x %d ",
                supportedVideoSizes[i].width, supportedVideoSizes[i].height
            )
        )

        return videoSizes.toString()

    }

    private fun getFocalLength(cameraParams: Camera.Parameters): String {

        return try {
            String.format(
                utils!!.locale, "%.2f %s",
                cameraParams.focalLength,
                getString(R.string.cam_sub_item_mm)
            )
        } catch (ex: IllegalFormatException) {
            getString(R.string.unknown)
        }

    }

    private fun releaseCamera(camera: Camera?) {
        camera?.release()
    }

    private inner class GetCameraParamsTask : AsyncTask<Void, Void, ArrayList<ListItem>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressWheel.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Void): ArrayList<ListItem> {
            return listItems
        }

        override fun onPostExecute(result: ArrayList<ListItem>) {

            super.onPostExecute(result)

            listView.adapter = CustomArrayAdapter(activity, result)

            getCameraParamsTask = null
            progressWheel.visibility = View.INVISIBLE
            progressWheel.stopSpinning()

        }

    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 1001
    }

}