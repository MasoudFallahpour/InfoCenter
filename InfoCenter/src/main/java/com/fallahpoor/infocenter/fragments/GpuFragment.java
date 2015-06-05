/*
    Copyright (C) 2014-2015 Masood Fallahpoor

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

package com.fallahpoor.infocenter.fragments;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter;
import com.fallahpoor.infocenter.adapters.ListItem;
import com.fallahpoor.infocenter.adapters.OrdinaryListItem;

import java.util.ArrayList;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * GpuFragment displays some information about the GPU of the device including
 * its manufacturer, model, and OpenGL version.
 *
 * @author Masood Fallahpoor
 */
public class GpuFragment extends Fragment {

    private ListView mListView;
    private GLSurfaceView mGlSurfaceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_gpu, container, false);

        mGlSurfaceView = (GLSurfaceView) view.findViewById(R.id.glSurfaceView);
        mGlSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGlSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mGlSurfaceView.setRenderer(new DummyRenderer());
        mGlSurfaceView.setZOrderOnTop(true);

        mListView = (ListView) view.findViewById(R.id.listView);

        return view;

    }

    @Override
    public void onResume() {

        super.onResume();
        mGlSurfaceView.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();
        mGlSurfaceView.onPause();

    }

    private String getLocalizedVendor(String vendor) {

        String localizedVendor;

        switch (vendor.toLowerCase(Locale.US)) {
            case "qualcomm":
                localizedVendor = getString(R.string.gpu_sub_item_qualcomm);
                break;
            default:
                localizedVendor = vendor;
        }

        return localizedVendor;

    }

    /*
     * A dummy Renderer class that obtains some information about the GPU
     * of the device.
     */
    private class DummyRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            final ArrayList<ListItem> items = new ArrayList<>();

            gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

            gl.glClearColor(0, 0, 0, 0);
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);

            items.add(new OrdinaryListItem(getString(R.string.gpu_item_vendor),
                    getLocalizedVendor(gl.glGetString(GL10.GL_VENDOR))));
            items.add(new OrdinaryListItem(getString(R.string.gpu_item_model),
                    gl.glGetString(GL10.GL_RENDERER)));
            items.add(new OrdinaryListItem(getString(R.string.gpu_item_opengl_version),
                    gl.glGetString(GL10.GL_VERSION)));

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mListView.setAdapter(new CustomArrayAdapter(getActivity(), items));
                }
            });

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
        }

        @Override
        public void onDrawFrame(GL10 gl) {
        }

    } // end class DummyRenderer

} // end class GpuFragment
