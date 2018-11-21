package com.android.zynda.filemanager;


import java.io.File;

enum DirectoryType{
    Doc,
    Download,
    Music,
    Video,
    Picture,
    Apps,

}

public class FileData {

    protected String path;

    public String fileName;
    public int imgId;
    public File file;

    FileData(File file){
        this.file=file;
        this.fileName=file.getName();
        if(file.isDirectory()){
            DirectoryType dt=getDirType(file);
            if(dt==DirectoryType.Doc){
                this.imgId=R.drawable.folder_documents;
            }else if(dt==DirectoryType.Download){
                this.imgId=R.drawable.folder_desktop;
            }else if(dt==DirectoryType.Apps){
                this.imgId=R.drawable.folder_apps;
            }else if(dt==DirectoryType.Video){
                this.imgId=R.drawable.folder_video;
            }else if(dt==DirectoryType.Music){
                this.imgId=R.drawable.folder_music;
            }else if(dt==DirectoryType.Picture){
                this.imgId=R.drawable.folder_pictures;
            }else{
                this.imgId=R.drawable.folder;
            }

        }else{
            this.imgId=R.drawable.file_empty;
        }

    }


    protected DirectoryType getDirType(File file){
        String dir="/storage/emulated/0/";
        if(file.getPath().equals(dir+"Documents")){
            return DirectoryType.Doc;
        }else if(file.getPath().equals(dir+"Download")){
            return DirectoryType.Download;
        }else if(file.getPath().equals(dir+"Movies")){
            return DirectoryType.Video;
        }else if(file.getPath().equals(dir+"Music")){
            return DirectoryType.Music;
        }else if(file.getPath().equals(dir+"DCIM")){
            return DirectoryType.Picture;
        }else if(file.getPath().equals(dir+"Android")){
            return DirectoryType.Apps;
        }
        return null;
    }

}
