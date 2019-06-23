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

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter
import com.fallahpoor.infocenter.adapters.ListItem
import com.fallahpoor.infocenter.adapters.OrdinaryListItem
import kotlinx.android.synthetic.main.fragment_gpu.*
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * GpuFragment displays some information about the GPU of the device including
 * its manufacturer, model, and OpenGL version.
 */
class GpuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_gpu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        glSurfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)
        glSurfaceView.setRenderer(DummyRenderer())
        glSurfaceView.setZOrderOnTop(true)
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    private fun getLocalizedVendor(vendor: String): String {
        return when (vendor.toLowerCase(Locale.US)) {
            "qualcomm" -> getString(R.string.gpu_sub_item_qualcomm)
            else -> vendor
        }
    }

    /*
     * A dummy Renderer class that obtains some information about the GPU
     * of the device.
     */
    private inner class DummyRenderer : GLSurfaceView.Renderer {

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {

            gl.glDisable(GL10.GL_DITHER)
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST)

            gl.glClearColor(0f, 0f, 0f, 0f)
            gl.glEnable(GL10.GL_CULL_FACE)
            gl.glShadeModel(GL10.GL_SMOOTH)
            gl.glEnable(GL10.GL_DEPTH_TEST)

            val items = ArrayList<ListItem>().apply {
                add(
                    OrdinaryListItem(
                        getString(R.string.gpu_item_vendor),
                        getLocalizedVendor(gl.glGetString(GL10.GL_VENDOR))
                    )
                )
                add(
                    OrdinaryListItem(
                        getString(R.string.gpu_item_model),
                        gl.glGetString(GL10.GL_RENDERER)
                    )
                )
                add(
                    OrdinaryListItem(
                        getString(R.string.gpu_item_opengl_version),
                        gl.glGetString(GL10.GL_VERSION)
                    )
                )
            }
            activity!!.runOnUiThread { listView.adapter = CustomArrayAdapter(activity, items) }

        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {}

        override fun onDrawFrame(gl: GL10) {}

    } // end class DummyRenderer

} // end class GpuFragment
