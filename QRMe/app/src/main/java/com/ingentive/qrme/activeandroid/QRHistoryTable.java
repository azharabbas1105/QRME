package com.ingentive.qrme.activeandroid;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by PC on 22-08-2016.
 */
@Table(name = "QRHistoryTable")
public class QRHistoryTable extends Model {
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Column(name = "video_url")
    public String videoUrl;

    @Column(name = "video_path")
    public String videoPath;

    @Column(name = "image_path")
    public String imagePath;

    public QRHistoryTable(){
        super();
        this.videoUrl="";
        this.videoPath="";
        this.imagePath="";

    }
}
