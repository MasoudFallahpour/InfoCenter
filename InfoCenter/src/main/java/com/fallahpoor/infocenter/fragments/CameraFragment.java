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

package com.fallahpoor.infocenter.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter;
import com.fallahpoor.infocenter.adapters.HeaderListItem;
import com.fallahpoor.infocenter.adapters.ListItem;
import com.fallahpoor.infocenter.adapters.OrdinaryListItem;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.halfbit.pinnedsection.PinnedSectionListView;

/**
 * CameraFragment displays some properties of the camera(s) of the device,
 * if any.
 *
 * @author Masood Fallahpoor
 */
// CHECK Is it possible for a device to have front camera but not rear camera?
// TODO Update the code to use the new Camera API introduced in Android Lollipop.
@SuppressWarnings("deprecation")
public class CameraFragment extends Fragment {

    private static final int REQUEST_CODE_CAMERA = 1001;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.progressWheel)
    ProgressWheel mProgressWheel;
    @BindView(R.id.textView)
    TextView messageTextView;
    private Utils mUtils;
    private GetCameraParamsTask mGetCameraParamsTask;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_camera, container,
                false);
        unbinder = ButterKnife.bind(this, view);

        mUtils = new Utils(getActivity());

        ((PinnedSectionListView) mListView).setShadowVisible(false);

        messageTextView.setText(R.string.cam_no_camera);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            populateListView();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        }

        return view;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateListView();
            } else {
                mListView.setEmptyView(messageTextView);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (mGetCameraParamsTask != null) {
            mGetCameraParamsTask.cancel(true);
            mGetCameraParamsTask = null;
        }

        if (mProgressWheel != null && mProgressWheel.isSpinning()) {
            mProgressWheel.stopSpinning();
        }

    }

    private void populateListView() {

        if (Camera.getNumberOfCameras() > 0) {
            mGetCameraParamsTask = new GetCameraParamsTask();
            mGetCameraParamsTask.execute();
        } else {
            mListView.setEmptyView(messageTextView);
        }

    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> items = new ArrayList<>();

        switch (Camera.getNumberOfCameras()) {
            case 1:
                items.addAll(getCameraParams(CameraInfo.CAMERA_FACING_BACK));
                break;
            case 2:
                items.add(new HeaderListItem(getString(R.string.cam_item_back_camera)));
                items.addAll(getCameraParams(CameraInfo.CAMERA_FACING_BACK));
                items.add(new HeaderListItem(getString(R.string.cam_item_front_camera)));
                items.addAll(getCameraParams(CameraInfo.CAMERA_FACING_FRONT));
                break;
            default:
                // how many cameras do we have in here?!
        }

        return items;

    }

    private Camera getCameraInstance(int cameraId) {

        Camera camera = null;

        try {
            camera = Camera.open(cameraId);
        } catch (Exception ignored) {
        }

        return camera;

    }

    private ArrayList<ListItem> getCameraParams(int cameraFacing) {

        ArrayList<ListItem> camParams = new ArrayList<>();
        Camera.Parameters cameraParams;
        Camera camera;
        String[] items;
        ArrayList<String> subItems;

        camera = getCameraInstance(cameraFacing);

        if (camera != null) {
            cameraParams = camera.getParameters();
            releaseCamera(camera);

            items = getItemsArray();
            subItems = getParameters(cameraParams);

            for (int i = 0; i < items.length; i++) {
                camParams.add(new OrdinaryListItem(items[i],
                        subItems.get(i)));
            }

        } else { // camera is busy or for some other reason camera isn't available
            if (cameraFacing == CameraInfo.CAMERA_FACING_BACK) {
                camParams.add(new OrdinaryListItem(
                        getString(R.string.cam_sub_item_back_camera_info_unavailable), ""));
            } else {
                camParams.add(new OrdinaryListItem(
                        getString(R.string.cam_sub_item_front_camera_info_unavailable), ""));
            }

        }

        return camParams;

    } // end method getCameraParams

    private String[] getItemsArray() {

        return new String[]{
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
        };

    }

    private ArrayList<String> getParameters(Camera.Parameters cameraParams) {

        ArrayList<String> params = new ArrayList<>();
        String supported = getString(R.string.supported);
        String unsupported = getString(R.string.unsupported);

        params.add(getMegaPixels(cameraParams));
        params.add(getPictureSize(cameraParams));
        params.add(getPictureFormat(cameraParams));
        params.add(getSupportedVideoSizes(cameraParams));
        params.add(getFocalLength(cameraParams));
        params.add(cameraParams.getAntibanding() != null ? supported : unsupported);
        params.add(cameraParams.isAutoExposureLockSupported() ? supported : unsupported);
        params.add(cameraParams.isAutoWhiteBalanceLockSupported() ? supported : unsupported);
        params.add(cameraParams.getColorEffect() != null ? supported : unsupported);
        params.add(cameraParams.getFlashMode() != null ? supported : unsupported);
        params.add(cameraParams.getSceneMode() != null ? supported : unsupported);
        params.add(cameraParams.isZoomSupported() ? supported : unsupported);
        params.add(cameraParams.isVideoSnapshotSupported() ? supported : unsupported);

        return params;

    } // end method getParameters

    private String getMegaPixels(Camera.Parameters cameraParams) {

        List<Size> pictureSizes = cameraParams.
                getSupportedPictureSizes();
        String strMegaPixels = getString(R.string.unknown);
        double dblMegaPixels;
        int maxHeight = Integer.MIN_VALUE;
        int maxWidth = Integer.MIN_VALUE;

        for (Size pictureSize : pictureSizes) {
            if (pictureSize.width > maxWidth) {
                maxWidth = pictureSize.width;
            }
            if (pictureSize.height > maxHeight) {
                maxHeight = pictureSize.height;
            }
        }

        if (maxWidth != Integer.MIN_VALUE && maxHeight !=
                Integer.MIN_VALUE) {
            dblMegaPixels = (double) (maxWidth * maxHeight) / 1000000;
            strMegaPixels = String.format(mUtils.getLocale(), "%.1f %s",
                    dblMegaPixels, getString(R.string.cam_sub_item_mp));
        }

        return strMegaPixels;

    } // end method getMegaPixels

    private String getPictureSize(Camera.Parameters cameraParams) {

        int width = cameraParams.getPictureSize().width;
        int height = cameraParams.getPictureSize().height;

        return String.format(mUtils.getLocale(), "%d x %d", width, height);

    }

    private String getPictureFormat(Camera.Parameters cameraParams) {

        int intFormat = cameraParams.getPictureFormat();
        String format;

        switch (intFormat) {
            case ImageFormat.JPEG:
                format = "JPEG";
                break;
            case ImageFormat.RGB_565:
                format = "RGB";
                break;
            default:
                format = getString(R.string.unknown);
        }

        return format;

    }

    private String getSupportedVideoSizes(Camera.Parameters cameraParams) {

        int i;
        StringBuilder videoSizes = new StringBuilder();
        List<Size> supportedVideoSizes = cameraParams.getSupportedVideoSizes();

        if (supportedVideoSizes == null) {
            return getString(R.string.unknown);
        }

        for (i = 0; i < supportedVideoSizes.size() - 1; i++) {
            videoSizes.append(String.format(mUtils.getLocale(), "%d x %d, ",
                    supportedVideoSizes.get(i).width,
                    supportedVideoSizes.get(i).height));
        }

        videoSizes.append(String.format(mUtils.getLocale(), "%d x %d ",
                supportedVideoSizes.get(i).width, supportedVideoSizes.get(i).height));

        return videoSizes.toString();

    }

    private String getFocalLength(Camera.Parameters cameraParams) {

        String focalLength;

        try {
            focalLength = String.format(mUtils.getLocale(), "%.2f %s",
                    cameraParams.getFocalLength(),
                    getString(R.string.cam_sub_item_mm));
        } catch (IllegalFormatException ex) {
            focalLength = getString(R.string.unknown);
        }

        return focalLength;

    }

    private void releaseCamera(Camera camera) {
        if (camera != null) {
            camera.release();
        }
    }

    private class GetCameraParamsTask extends AsyncTask<Void, Void, ArrayList<ListItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressWheel.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<ListItem> doInBackground(Void... params) {
            return getListItems();
        }

        @Override
        protected void onPostExecute(ArrayList<ListItem> result) {

            super.onPostExecute(result);

            mListView.setAdapter(new CustomArrayAdapter(getActivity(), result));

            mGetCameraParamsTask = null;
            mProgressWheel.setVisibility(View.INVISIBLE);
            mProgressWheel.stopSpinning();

        }

    } // end inner class GetCameraParamsTask

} // end class CameraFragment