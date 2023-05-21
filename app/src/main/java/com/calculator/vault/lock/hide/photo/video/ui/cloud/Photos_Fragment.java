package com.calculator.vault.lock.hide.photo.video.ui.cloud;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.calculator.vault.lock.hide.photo.video.R;
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data;
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class Photos_Fragment extends Fragment {

    private RecyclerView photos_recycler;
    ArrayList<Media_Data> googleDriveFiles = new ArrayList<>();
    private WebView web_folder;
    private LinearLayout no_photo_drive;

    public Photos_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        initView(view);
        getImages();
        return view;
    }

    private void initAdapter() {
        if (googleDriveFiles.size() > 0) {
            photos_recycler.setVisibility(View.VISIBLE);
            no_photo_drive.setVisibility(View.GONE);
            PhotosDriveAdapter photos_drive_adapter = new PhotosDriveAdapter(getActivity(), googleDriveFiles, 0);
            photos_recycler.setLayoutManager(new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false));
            photos_recycler.setAdapter(photos_drive_adapter);
        } else {
            photos_recycler.setVisibility(View.GONE);
            no_photo_drive.setVisibility(View.VISIBLE);
        }
    }

    private void initView(View view) {
        photos_recycler = (RecyclerView) view.findViewById(R.id.photos_recycler);
        no_photo_drive = (LinearLayout) view.findViewById(R.id.no_photo_drive);
    }

    private void getImages() {
        googleDriveFiles.clear();
        File folder = new File(Constant.photos_path);
        if (folder.isDirectory()) {
            File[] allFiles = folder.listFiles();
            if (allFiles != null && allFiles.length != 0) {
                for (int i = 0; i < allFiles.length; i++) {
                    Media_Data media_data = new Media_Data();
                    media_data.setName(allFiles[i].getName());
                    media_data.setPath(allFiles[i].getAbsolutePath());
                    media_data.setFolder(allFiles[i].getParent());
                    File file = new File(allFiles[i].getAbsolutePath());
                    media_data.setLength(String.valueOf(file.length()));
                    Date lastModDate = new Date(file.lastModified());
                    media_data.setModifieddate(String.valueOf(lastModDate));
                    media_data.setAddeddate(String.valueOf(lastModDate));
                    googleDriveFiles.add(media_data);
                }
            }
            Collections.reverse(googleDriveFiles);
            initAdapter();
        }
    }
}
