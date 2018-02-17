package com.tnt.easymap.demos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ZoomControls;

import com.android.gis.API;
import com.android.gis.MapView;
import com.android.gis.Workspace;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private String mapDataPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "EasyMap/Data/京津冀/Jingjin.sxwu";
    private String licPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "EasyMap/lic"+ File.separator;
    private MapView mMapView = null;
    private ZoomControls mZoomControls = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        API.EG_SetWorkPath(licPath);
        loadView();
        openMap();
    }

    /**
     * 加载地图
     */
    public void loadView(){
        mMapView = (MapView)findViewById(R.id.map_view);
        mZoomControls = (ZoomControls)findViewById(R.id.zoom_control);
        mZoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomIn();
            }
        });
        mZoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomOut();
            }
        });

    }

    /**
     * 打开地图
     */
    public void openMap(){
        new MapOpenTask(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mapDataPath);
    }

    /**
     * 放大
     */
    public void zoomIn(){
        mMapView.SetScale(mMapView.GetScale()*2);
        mMapView.Refresh();
    }

    /**
     * 缩小
     */
    public void zoomOut(){
        mMapView.SetScale(mMapView.GetScale()/2);
        mMapView.Refresh();
    }

    /**
     * 打开地图数据
     */
    class MapOpenTask extends AsyncTask<String, Integer, Boolean>{
        ContentLoadingProgressBar progressBar = null;
        public MapOpenTask(Context context){
            progressBar = new ContentLoadingProgressBar(context);
            progressBar.setContentDescription("正在加载数据...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            progressBar.hide();
            mMapView.viewEntire();
            mMapView.Refresh(true);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean bRt = false;
            Workspace workspace = new Workspace();
            if(workspace.open(strings[0], "")){
                mMapView.AttachWorkspace(workspace);
                int mapCount = workspace.getMapCount();
                if(mapCount > 0){
                    bRt = mMapView.Open(workspace.getMapNameAt(0));
                }
            }
            return bRt;
        }
    }
}
