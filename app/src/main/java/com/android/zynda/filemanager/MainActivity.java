package com.android.zynda.filemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{


    protected FileItemAdapter adapter;
    protected File curFile;

    protected Map<String,String> mineMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMineMap();
        List<FileData> fileDatas=new ArrayList<FileData>();
        adapter=new FileItemAdapter(MainActivity.this,R.layout.file_item,fileDatas);
        GridView gv=findViewById(R.id.gridView);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);

        //gotoPath(Environment.getDataDirectory().getAbsolutePath()+"/");
        getPermission();
        Button btnBack=findViewById(R.id.btnBack);
        final MainActivity self=this;
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.goBack();
            }
        });
    }

//    protected  void demoRun(){
//        List<FileData> files=new ArrayList<FileData>();
//        files.add(new FileData("下载",R.drawable.folder_desktop));
//        files.add(new FileData("安装包",R.drawable.folder_apps));
//        files.add(new FileData("文档",R.drawable.folder_documents));
//        files.add(new FileData("音乐",R.drawable.folder_music));
//        files.add(new FileData("照片",R.drawable.folder_pictures));
//
//        FileItemAdapter adt=new FileItemAdapter(MainActivity.this,R.layout.file_item,files);
//        GridView gv=findViewById(R.id.gridView);
//        gv.setAdapter(adt);
//
//    }

    protected void gotoPath(String path){
        adapter.clear();
        File f=new File(path);
        curFile=f;
        TextView tv=findViewById(R.id.textView);
        tv.setText(f.getName());
        File[] files = f.listFiles();
        if(files!=null){
            for (File file : files) {
                if(!file.isHidden())adapter.add(new FileData(file));
            }
            GridView gv=findViewById(R.id.gridView);
            //gv.getAdapter().;//deferNotifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileData fd= (FileData) parent.getAdapter().getItem(position);
        File file=fd.file;

        //Uri uri=Uri.fromFile(file);
        if(file.isDirectory()){
            gotoPath(file.getPath());
        }else try {
            Uri uri = FileProvider.getUriForFile(this, "com.android.zynda.filemanager" + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String type = getMIMEType(file.getAbsolutePath());
            if(type!=null)intent.setDataAndType(uri, type);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("openFile",e.getMessage());
            Toast.makeText(getBaseContext(),"无法打开该文件",Toast.LENGTH_SHORT);
        }
    }

    void getPermission()
    {
        int permissionCheck1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    124);
        }else{
            start();
        }
    }

    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if (requestCode == 124) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {

                start();
                //gotoPath(Environment.getDataDirectory().getAbsolutePath());
            } else {
                Toast.makeText(getApplicationContext(),getString(R.string.not_permiss_info),Toast.LENGTH_SHORT);
            }
        }
    }

    protected void start(){
        gotoPath("/storage/emulated/0");
    }

    protected void goBack(){
        if(curFile!=null&&curFile.isDirectory()){
            gotoPath(curFile.getParent());
        }
    }

    public String getMIMEType(String filePath) {
        String type = "*/*";
        String fName = filePath;

        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }

        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") {
            return type;
        }

        String tmp=mineMap.get(end);
        if(tmp!=null)type=tmp;
        else type=null;
        return type;
    }

    protected void initMineMap(){
        mineMap=new HashMap<>();
        mineMap.put(".3gp", "video/3gpp");
        mineMap.put(".3gpp", "video/3gpp");
        mineMap.put(".aac", "audio/x-mpeg");
        mineMap.put(".amr", "audio/x-mpeg");
        mineMap.put(".apk", "application/vnd.android.package-archive");
        mineMap.put(".avi", "video/x-msvideo");
        mineMap.put(".aab", "application/x-authoware-bin");
        mineMap.put(".aam", "application/x-authoware-map");
        mineMap.put(".aas", "application/x-authoware-seg");
        mineMap.put(".ai", "application/postscript");
        mineMap.put(".aif", "audio/x-aiff");
        mineMap.put(".aifc", "audio/x-aiff");
        mineMap.put(".aiff", "audio/x-aiff");
        mineMap.put(".als", "audio/x-alpha5");
        mineMap.put(".amc", "application/x-mpeg");
        mineMap.put(".ani", "application/octet-stream");
        mineMap.put(".asc", "text/plain");
        mineMap.put(".asd", "application/astound");
        mineMap.put(".asf", "video/x-ms-asf");
        mineMap.put(".asn", "application/astound");
        mineMap.put(".asp", "application/x-asap");
        mineMap.put(".asx", " video/x-ms-asf");
        mineMap.put(".au", "audio/basic");
        mineMap.put(".avb", "application/octet-stream");
        mineMap.put(".awb", "audio/amr-wb");
        mineMap.put(".bcpio", "application/x-bcpio");
        mineMap.put(".bld", "application/bld");
        mineMap.put(".bld2", "application/bld2");
        mineMap.put(".bpk", "application/octet-stream");
        mineMap.put(".bz2", "application/x-bzip2");
        mineMap.put(".bin", "application/octet-stream");
        mineMap.put(".bmp", "image/bmp");
        mineMap.put(".c", "text/plain");
        mineMap.put(".class", "application/octet-stream");
        mineMap.put(".conf", "text/plain");
        mineMap.put(".cpp", "text/plain");
        mineMap.put(".cal", "image/x-cals");
        mineMap.put(".ccn", "application/x-cnc");
        mineMap.put(".cco", "application/x-cocoa");
        mineMap.put(".cdf", "application/x-netcdf");
        mineMap.put(".cgi", "magnus-internal/cgi");
        mineMap.put(".chat", "application/x-chat");
        mineMap.put(".clp", "application/x-msclip");
        mineMap.put(".cmx", "application/x-cmx");
        mineMap.put(".co", "application/x-cult3d-object");
        mineMap.put(".cod", "image/cis-cod");
        mineMap.put(".cpio", "application/x-cpio");
        mineMap.put(".cpt", "application/mac-compactpro");
        mineMap.put(".crd", "application/x-mscardfile");
        mineMap.put(".csh", "application/x-csh");
        mineMap.put(".csm", "chemical/x-csml");
        mineMap.put(".csml", "chemical/x-csml");
        mineMap.put(".css", "text/css");
        mineMap.put(".cur", "application/octet-stream");
        mineMap.put(".doc", "application/msword");
        mineMap.put(".dcm", "x-lml/x-evm");
        mineMap.put(".dcr", "application/x-director");
        mineMap.put(".dcx", "image/x-dcx");
        mineMap.put(".dhtml", "text/html");
        mineMap.put(".dir", "application/x-director");
        mineMap.put(".dll", "application/octet-stream");
        mineMap.put(".dmg", "application/octet-stream");
        mineMap.put(".dms", "application/octet-stream");
        mineMap.put(".dot", "application/x-dot");
        mineMap.put(".dvi", "application/x-dvi");
        mineMap.put(".dwf", "drawing/x-dwf");
        mineMap.put(".dwg", "application/x-autocad");
        mineMap.put(".dxf", "application/x-autocad");
        mineMap.put(".dxr", "application/x-director");
        mineMap.put(".ebk", "application/x-expandedbook");
        mineMap.put(".emb", "chemical/x-embl-dl-nucleotide");
        mineMap.put(".embl", "chemical/x-embl-dl-nucleotide");
        mineMap.put(".eps", "application/postscript");
        mineMap.put(".epub", "application/epub+zip");
        mineMap.put(".eri", "image/x-eri");
        mineMap.put(".es", "audio/echospeech");
        mineMap.put(".esl", "audio/echospeech");
        mineMap.put(".etc", "application/x-earthtime");
        mineMap.put(".etx", "text/x-setext");
        mineMap.put(".evm", "x-lml/x-evm");
        mineMap.put(".evy", "application/x-envoy");
        mineMap.put(".exe", "application/octet-stream");
        mineMap.put(".fh4", "image/x-freehand");
        mineMap.put(".fh5", "image/x-freehand");
        mineMap.put(".fhc", "image/x-freehand");
        mineMap.put(".fif", "image/fif");
        mineMap.put(".fm", "application/x-maker");
        mineMap.put(".fpx", "image/x-fpx");
        mineMap.put(".fvi", "video/isivideo");
        mineMap.put(".flv", "video/x-msvideo");
        mineMap.put(".gau", "chemical/x-gaussian-input");
        mineMap.put(".gca", "application/x-gca-compressed");
        mineMap.put(".gdb", "x-lml/x-gdb");
        mineMap.put(".gif", "image/gif");
        mineMap.put(".gps", "application/x-gps");
        mineMap.put(".gtar", "application/x-gtar");
        mineMap.put(".gz", "application/x-gzip");
        mineMap.put(".gif", "image/gif");
        mineMap.put(".gtar", "application/x-gtar");
        mineMap.put(".gz", "application/x-gzip");
        mineMap.put(".h", "text/plain");
        mineMap.put(".hdf", "application/x-hdf");
        mineMap.put(".hdm", "text/x-hdml");
        mineMap.put(".hdml", "text/x-hdml");
        mineMap.put(".htm", "text/html");
        mineMap.put(".html", "text/html");
        mineMap.put(".hlp", "application/winhlp");
        mineMap.put(".hqx", "application/mac-binhex40");
        mineMap.put(".hts", "text/html");
        mineMap.put(".ice", "x-conference/x-cooltalk");
        mineMap.put(".ico", "application/octet-stream");
        mineMap.put(".ief", "image/ief");
        mineMap.put(".ifm", "image/gif");
        mineMap.put(".ifs", "image/ifs");
        mineMap.put(".imy", "audio/melody");
        mineMap.put(".ins", "application/x-net-install");
        mineMap.put(".ips", "application/x-ipscript");
        mineMap.put(".ipx", "application/x-ipix");
        mineMap.put(".it", "audio/x-mod");
        mineMap.put(".itz", "audio/x-mod");
        mineMap.put(".ivr", "i-world/i-vrml");
        mineMap.put(".j2k", "image/j2k");
        mineMap.put(".jad", "text/vnd.sun.j2me.app-descriptor");
        mineMap.put(".jam", "application/x-jam");
        mineMap.put(".jnlp", "application/x-java-jnlp-file");
        mineMap.put(".jpe", "image/jpeg");
        mineMap.put(".jpz", "image/jpeg");
        mineMap.put(".jwc", "application/jwc");
        mineMap.put(".jar", "application/java-archive");
        mineMap.put(".java", "text/plain");
        mineMap.put(".jpeg", "image/jpeg");
        mineMap.put(".jpg", "image/jpeg");
        mineMap.put(".js", "application/x-javascript");
        mineMap.put(".kjx", "application/x-kjx");
        mineMap.put(".lak", "x-lml/x-lak");
        mineMap.put(".latex", "application/x-latex");
        mineMap.put(".lcc", "application/fastman");
        mineMap.put(".lcl", "application/x-digitalloca");
        mineMap.put(".lcr", "application/x-digitalloca");
        mineMap.put(".lgh", "application/lgh");
        mineMap.put(".lha", "application/octet-stream");
        mineMap.put(".lml", "x-lml/x-lml");
        mineMap.put(".lmlpack", "x-lml/x-lmlpack");
        mineMap.put(".log", "text/plain");
        mineMap.put(".lsf", "video/x-ms-asf");
        mineMap.put(".lsx", "video/x-ms-asf");
        mineMap.put(".lzh", "application/x-lzh ");
        mineMap.put(".m13", "application/x-msmediaview");
        mineMap.put(".m14", "application/x-msmediaview");
        mineMap.put(".m15", "audio/x-mod");
        mineMap.put(".m3u", "audio/x-mpegurl");
        mineMap.put(".m3url", "audio/x-mpegurl");
        mineMap.put(".ma1", "audio/ma1");
        mineMap.put(".ma2", "audio/ma2");
        mineMap.put(".ma3", "audio/ma3");
        mineMap.put(".ma5", "audio/ma5");
        mineMap.put(".man", "application/x-troff-man");
        mineMap.put(".map", "magnus-internal/imagemap");
        mineMap.put(".mbd", "application/mbedlet");
        mineMap.put(".mct", "application/x-mascot");
        mineMap.put(".mdb", "application/x-msaccess");
        mineMap.put(".mdz", "audio/x-mod");
        mineMap.put(".me", "application/x-troff-me");
        mineMap.put(".mel", "text/x-vmel");
        mineMap.put(".mi", "application/x-mif");
        mineMap.put(".mid", "audio/midi");
        mineMap.put(".midi", "audio/midi");
        mineMap.put(".m4a", "audio/mp4a-latm");
        mineMap.put(".m4b", "audio/mp4a-latm");
        mineMap.put(".m4p", "audio/mp4a-latm");
        mineMap.put(".m4u", "video/vnd.mpegurl");
        mineMap.put(".m4v", "video/x-m4v");
        mineMap.put(".mov", "video/quicktime");
        mineMap.put(".mp2", "audio/x-mpeg");
        mineMap.put(".mp3", "audio/x-mpeg");
        mineMap.put(".mp4", "video/mp4");
        mineMap.put(".mpc", "application/vnd.mpohun.certificate");
        mineMap.put(".mpe", "video/mpeg");
        mineMap.put(".mpeg", "video/mpeg");
        mineMap.put(".mpg", "video/mpeg");
        mineMap.put(".mpg4", "video/mp4");
        mineMap.put(".mpga", "audio/mpeg");
        mineMap.put(".msg", "application/vnd.ms-outlook");
        mineMap.put(".mif", "application/x-mif");
        mineMap.put(".mil", "image/x-cals");
        mineMap.put(".mio", "audio/x-mio");
        mineMap.put(".mmf", "application/x-skt-lbs");
        mineMap.put(".mng", "video/x-mng");
        mineMap.put(".mny", "application/x-msmoney");
        mineMap.put(".moc", "application/x-mocha");
        mineMap.put(".mocha", "application/x-mocha");
        mineMap.put(".mod", "audio/x-mod");
        mineMap.put(".mof", "application/x-yumekara");
        mineMap.put(".mol", "chemical/x-mdl-molfile");
        mineMap.put(".mop", "chemical/x-mopac-input");
        mineMap.put(".movie", "video/x-sgi-movie");
        mineMap.put(".mpn", "application/vnd.mophun.application");
        mineMap.put(".mpp", "application/vnd.ms-project");
        mineMap.put(".mps", "application/x-mapserver");
        mineMap.put(".mrl", "text/x-mrml");
        mineMap.put(".mrm", "application/x-mrm");
        mineMap.put(".ms", "application/x-troff-ms");
        mineMap.put(".mts", "application/metastream");
        mineMap.put(".mtx", "application/metastream");
        mineMap.put(".mtz", "application/metastream");
        mineMap.put(".mzv", "application/metastream");
        mineMap.put(".nar", "application/zip");
        mineMap.put(".nbmp", "image/nbmp");
        mineMap.put(".nc", "application/x-netcdf");
        mineMap.put(".ndb", "x-lml/x-ndb");
        mineMap.put(".nds", "application/x-nintendo-ds-rom");
        mineMap.put(".ndwn", "application/ndwn");
        mineMap.put(".nif", "application/x-nif");
        mineMap.put(".nmz", "application/x-scream");
        mineMap.put(".nokia-op-logo", "image/vnd.nok-oplogo-color");
        mineMap.put(".npx", "application/x-netfpx");
        mineMap.put(".nsnd", "audio/nsnd");
        mineMap.put(".nva", "application/x-neva1");
        mineMap.put(".oda", "application/oda");
        mineMap.put(".oom", "application/x-atlasMate-plugin");
        mineMap.put(".ogg", "audio/ogg");
        mineMap.put(".pac", "audio/x-pac");
        mineMap.put(".pae", "audio/x-epac");
        mineMap.put(".pan", "application/x-pan");
        mineMap.put(".pbm", "image/x-portable-bitmap");
        mineMap.put(".pcx", "image/x-pcx");
        mineMap.put(".pda", "image/x-pda");
        mineMap.put(".pdb", "chemical/x-pdb");
        mineMap.put(".pdf", "application/pdf");
        mineMap.put(".pfr", "application/font-tdpfr");
        mineMap.put(".pgm", "image/x-portable-graymap");
        mineMap.put(".pict", "image/x-pict");
        mineMap.put(".pm", "application/x-perl");
        mineMap.put(".pmd", "application/x-pmd");
        mineMap.put(".png", "image/png");
        mineMap.put(".pnm", "image/x-portable-anymap");
        mineMap.put(".pnz", "image/png");
        mineMap.put(".pot", "application/vnd.ms-powerpoint");
        mineMap.put(".ppm", "image/x-portable-pixmap");
        mineMap.put(".pps", "application/vnd.ms-powerpoint");
        mineMap.put(".ppt", "application/vnd.ms-powerpoint");
        mineMap.put(".pqf", "application/x-cprplayer");
        mineMap.put(".pqi", "application/cprplayer");
        mineMap.put(".prc", "application/x-prc");
        mineMap.put(".proxy", "application/x-ns-proxy-autoconfig");
        mineMap.put(".prop", "text/plain");
        mineMap.put(".ps", "application/postscript");
        mineMap.put(".ptlk", "application/listenup");
        mineMap.put(".pub", "application/x-mspublisher");
        mineMap.put(".pvx", "video/x-pv-pvx");
        mineMap.put(".qcp", "audio/vnd.qcelp");
        mineMap.put(".qt", "video/quicktime");
        mineMap.put(".qti", "image/x-quicktime");
        mineMap.put(".qtif", "image/x-quicktime");
        mineMap.put(".r3t", "text/vnd.rn-realtext3d");
        mineMap.put(".ra", "audio/x-pn-realaudio");
        mineMap.put(".ram", "audio/x-pn-realaudio");
        mineMap.put(".ras", "image/x-cmu-raster");
        mineMap.put(".rdf", "application/rdf+xml");
        mineMap.put(".rf", "image/vnd.rn-realflash");
        mineMap.put(".rgb", "image/x-rgb");
        mineMap.put(".rlf", "application/x-richlink");
        mineMap.put(".rm", "audio/x-pn-realaudio");
        mineMap.put(".rmf", "audio/x-rmf");
        mineMap.put(".rmm", "audio/x-pn-realaudio");
        mineMap.put(".rnx", "application/vnd.rn-realplayer");
        mineMap.put(".roff", "application/x-troff");
        mineMap.put(".rp", "image/vnd.rn-realpix");
        mineMap.put(".rpm", "audio/x-pn-realaudio-plugin");
        mineMap.put(".rt", "text/vnd.rn-realtext");
        mineMap.put(".rte", "x-lml/x-gps");
        mineMap.put(".rtf", "application/rtf");
        mineMap.put(".rtg", "application/metastream");
        mineMap.put(".rtx", "text/richtext");
        mineMap.put(".rv", "video/vnd.rn-realvideo");
        mineMap.put(".rwc", "application/x-rogerwilco");
        mineMap.put(".rar", "application/x-rar-compressed");
        mineMap.put(".rc", "text/plain");
        mineMap.put(".rmvb", "audio/x-pn-realaudio");
        mineMap.put(".s3m", "audio/x-mod");
        mineMap.put(".s3z", "audio/x-mod");
        mineMap.put(".sca", "application/x-supercard");
        mineMap.put(".scd", "application/x-msschedule");
        mineMap.put(".sdf", "application/e-score");
        mineMap.put(".sea", "application/x-stuffit");
        mineMap.put(".sgm", "text/x-sgml");
        mineMap.put(".sgml", "text/x-sgml");
        mineMap.put(".shar", "application/x-shar");
        mineMap.put(".shtml", "magnus-internal/parsed-html");
        mineMap.put(".shw", "application/presentations");
        mineMap.put(".si6", "image/si6");
        mineMap.put(".si7", "image/vnd.stiwap.sis");
        mineMap.put(".si9", "image/vnd.lgtwap.sis");
        mineMap.put(".sis", "application/vnd.symbian.install");
        mineMap.put(".sit", "application/x-stuffit");
        mineMap.put(".skd", "application/x-koan");
        mineMap.put(".skm", "application/x-koan");
        mineMap.put(".skp", "application/x-koan");
        mineMap.put(".skt", "application/x-koan");
        mineMap.put(".slc", "application/x-salsa");
        mineMap.put(".smd", "audio/x-smd");
        mineMap.put(".smi", "application/smil");
        mineMap.put(".smil", "application/smil");
        mineMap.put(".smp", "application/studiom");
        mineMap.put(".smz", "audio/x-smd");
        mineMap.put(".sh", "application/x-sh");
        mineMap.put(".snd", "audio/basic");
        mineMap.put(".spc", "text/x-speech");
        mineMap.put(".spl", "application/futuresplash");
        mineMap.put(".spr", "application/x-sprite");
        mineMap.put(".sprite", "application/x-sprite");
        mineMap.put(".sdp", "application/sdp");
        mineMap.put(".spt", "application/x-spt");
        mineMap.put(".src", "application/x-wais-source");
        mineMap.put(".stk", "application/hyperstudio");
        mineMap.put(".stm", "audio/x-mod");
        mineMap.put(".sv4cpio", "application/x-sv4cpio");
        mineMap.put(".sv4crc", "application/x-sv4crc");
        mineMap.put(".svf", "image/vnd");
        mineMap.put(".svg", "image/svg-xml");
        mineMap.put(".svh", "image/svh");
        mineMap.put(".svr", "x-world/x-svr");
        mineMap.put(".swf", "application/x-shockwave-flash");
        mineMap.put(".swfl", "application/x-shockwave-flash");
        mineMap.put(".t", "application/x-troff");
        mineMap.put(".tad", "application/octet-stream");
        mineMap.put(".talk", "text/x-speech");
        mineMap.put(".tar", "application/x-tar");
        mineMap.put(".taz", "application/x-tar");
        mineMap.put(".tbp", "application/x-timbuktu");
        mineMap.put(".tbt", "application/x-timbuktu");
        mineMap.put(".tcl", "application/x-tcl");
        mineMap.put(".tex", "application/x-tex");
        mineMap.put(".texi", "application/x-texinfo");
        mineMap.put(".texinfo", "application/x-texinfo");
        mineMap.put(".tgz", "application/x-tar");
        mineMap.put(".thm", "application/vnd.eri.thm");
        mineMap.put(".tif", "image/tiff");
        mineMap.put(".tiff", "image/tiff");
        mineMap.put(".tki", "application/x-tkined");
        mineMap.put(".tkined", "application/x-tkined");
        mineMap.put(".toc", "application/toc");
        mineMap.put(".toy", "image/toy");
        mineMap.put(".tr", "application/x-troff");
        mineMap.put(".trk", "x-lml/x-gps");
        mineMap.put(".trm", "application/x-msterminal");
        mineMap.put(".tsi", "audio/tsplayer");
        mineMap.put(".tsp", "application/dsptype");
        mineMap.put(".tsv", "text/tab-separated-values");
        mineMap.put(".ttf", "application/octet-stream");
        mineMap.put(".ttz", "application/t-time");
        mineMap.put(".txt", "text/plain");
        mineMap.put(".ult", "audio/x-mod");
        mineMap.put(".ustar", "application/x-ustar");
        mineMap.put(".uu", "application/x-uuencode");
        mineMap.put(".uue", "application/x-uuencode");
        mineMap.put(".vcd", "application/x-cdlink");
        mineMap.put(".vcf", "text/x-vcard");
        mineMap.put(".vdo", "video/vdo");
        mineMap.put(".vib", "audio/vib");
        mineMap.put(".viv", "video/vivo");
        mineMap.put(".vivo", "video/vivo");
        mineMap.put(".vmd", "application/vocaltec-media-desc");
        mineMap.put(".vmf", "application/vocaltec-media-file");
        mineMap.put(".vmi", "application/x-dreamcast-vms-info");
        mineMap.put(".vms", "application/x-dreamcast-vms");
        mineMap.put(".vox", "audio/voxware");
        mineMap.put(".vqe", "audio/x-twinvq-plugin");
        mineMap.put(".vqf", "audio/x-twinvq");
        mineMap.put(".vql", "audio/x-twinvq");
        mineMap.put(".vre", "x-world/x-vream");
        mineMap.put(".vrml", "x-world/x-vrml");
        mineMap.put(".vrt", "x-world/x-vrt");
        mineMap.put(".vrw", "x-world/x-vream");
        mineMap.put(".vts", "workbook/formulaone");
        mineMap.put(".wax", "audio/x-ms-wax");
        mineMap.put(".wbmp", "image/vnd.wap.wbmp");
        mineMap.put(".web", "application/vnd.xara");
        mineMap.put(".wav", "audio/x-wav");
        mineMap.put(".wma", "audio/x-ms-wma");
        mineMap.put(".wmv", "audio/x-ms-wmv");
        mineMap.put(".wi", "image/wavelet");
        mineMap.put(".wis", "application/x-InstallShield");
        mineMap.put(".wm", "video/x-ms-wm");
        mineMap.put(".wmd", "application/x-ms-wmd");
        mineMap.put(".wmf", "application/x-msmetafile");
        mineMap.put(".wml", "text/vnd.wap.wml");
        mineMap.put(".wmlc", "application/vnd.wap.wmlc");
        mineMap.put(".wmls", "text/vnd.wap.wmlscript");
        mineMap.put(".wmlsc", "application/vnd.wap.wmlscriptc");
        mineMap.put(".wmlscript", "text/vnd.wap.wmlscript");
        mineMap.put(".wmv", "video/x-ms-wmv");
        mineMap.put(".wmx", "video/x-ms-wmx");
        mineMap.put(".wmz", "application/x-ms-wmz");
        mineMap.put(".wpng", "image/x-up-wpng");
        mineMap.put(".wps", "application/vnd.ms-works");
        mineMap.put(".wpt", "x-lml/x-gps");
        mineMap.put(".wri", "application/x-mswrite");
        mineMap.put(".wrl", "x-world/x-vrml");
        mineMap.put(".wrz", "x-world/x-vrml");
        mineMap.put(".ws", "text/vnd.wap.wmlscript");
        mineMap.put(".wsc", "application/vnd.wap.wmlscriptc");
        mineMap.put(".wv", "video/wavelet");
        mineMap.put(".wvx", "video/x-ms-wvx");
        mineMap.put(".wxl", "application/x-wxl");
        mineMap.put(".x-gzip", "application/x-gzip");
        mineMap.put(".xar", "application/vnd.xara");
        mineMap.put(".xbm", "image/x-xbitmap");
        mineMap.put(".xdm", "application/x-xdma");
        mineMap.put(".xdma", "application/x-xdma");
        mineMap.put(".xdw", "application/vnd.fujixerox.docuworks");
        mineMap.put(".xht", "application/xhtml+xml");
        mineMap.put(".xhtm", "application/xhtml+xml");
        mineMap.put(".xhtml", "application/xhtml+xml");
        mineMap.put(".xla", "application/vnd.ms-excel");
        mineMap.put(".xlc", "application/vnd.ms-excel");
        mineMap.put(".xll", "application/x-excel");
        mineMap.put(".xlm", "application/vnd.ms-excel");
        mineMap.put(".xls", "application/vnd.ms-excel");
        mineMap.put(".xlt", "application/vnd.ms-excel");
        mineMap.put(".xlw", "application/vnd.ms-excel");
        mineMap.put(".xm", "audio/x-mod");
        mineMap.put(".xml", "text/xml");
        mineMap.put(".xmz", "audio/x-mod");
        mineMap.put(".xpi", "application/x-xpinstall");
        mineMap.put(".xpm", "image/x-xpixmap");
        mineMap.put(".xsit", "text/xml");
        mineMap.put(".xsl", "text/xml");
        mineMap.put(".xul", "text/xul");
        mineMap.put(".xwd", "image/x-xwindowdump");
        mineMap.put(".xyz", "chemical/x-pdb");
        mineMap.put(".yz1", "application/x-yz1");
        mineMap.put(".z", "application/x-compress");
        mineMap.put(".zac", "application/x-zaurus-zac");
        mineMap.put(".zip", "application/zip");
    }



}
