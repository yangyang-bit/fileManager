package com.example.filemanager5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.filemanager5";
    TextView pathTv;
    ImageButton backBtn;
    ListView fileLv;
    File currentParent;
    File[] currentFiles;
    File currentFile;
    File root;
    String dialogText="";
    Boolean  showSuffix=false;//是否显示后缀，为true显示，false不显示
    int returnChoice=0;//返回键状态标志，0为正常路径返回，1为搜索情况下返回
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pathTv =this.findViewById(R.id.id_tv_filepath);
        backBtn =this.findViewById(R.id.id_btn_back);
        fileLv = this.findViewById(R.id.id_lv_file);
        //判断是否装载sd卡
        boolean isLoadSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if (isLoadSDCard) {
            //获取根目录
//            root = Environment.getExternalStorageDirectory();
            root=getExternalCacheDir();
            currentParent = root;
            //获取当前文件夹下所有文件
            if(currentParent.exists()){
                currentFiles = currentParent.listFiles();
                //加载
                if (currentFiles!=null && currentFiles.length>0){
                    inflateListView(currentFiles);}
                else{Toast.makeText(this, "文件夹为空", Toast.LENGTH_SHORT).show();}}
            else{Toast.makeText(this, "路径不存在", Toast.LENGTH_SHORT).show();}
        } else {
            Toast.makeText(this, "无SD卡", Toast.LENGTH_SHORT).show();
        }
//        //设置监听事件
        setListener();
//        onContextItemSelected();
//        boolean success = (new File("/storage/emulated/0/text")).mkdir();
//        if (!success) {
//            Log.i("directory not created", "directory not created");
//        } else {
//            Log.i("directory created", "directory created");
//

//头部菜单
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//创建选项菜单
        menu.add(menu.NONE, 1, 1, "新建文件");
        menu.add(menu.NONE, 2, 2, "新建文件夹");
        menu.add(menu.NONE, 3, 3, "搜索");
        menu.add(menu.NONE, 4, 4, "批量操作");
        menu.add(menu.NONE, 5, 5, "导入文件");
        menu.add(menu.NONE, 6, 6, "显示后缀名（点击切换）");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//菜单选择监听
        switch (item.getItemId()) {
            case 1:

                try {
                    creatTextFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                createDir();
//
                break;
            case 3:
                try {
                    search(currentParent.getPath());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(MainActivity.this, "菜单三被选择了", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(MainActivity.this, "菜单四被选择了", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                break;
            case 6:
                if (showSuffix){
                    showSuffix=false;
//                    Toast.makeText(MainActivity.this, "1hhhhh", Toast.LENGTH_SHORT).show();
                    item.setTitle("不显示后缀");
                }else{
                    showSuffix=true;
//                    Toast.makeText(MainActivity.this, "菜单四被选择了", Toast.LENGTH_SHORT).show();
                    item.setTitle("显示后缀");
                }
                inflateListView(currentFiles);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void showDialog(){
//
//        final EditText inputServer = new EditText(MainActivity.this);
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("输入密码").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                String text = inputServer.getText().toString();
//                dialogText =text;
//
//            }});
//                builder.show();
//
//        }


//创建文本文件

    public void creatTextFile() throws IOException {
        //建一个dailog,用户输入文件名，返回给dialogText
        myDialog myDialogT=new myDialog(this,"请输入文件名");
        dialogText=myDialogT.showDialog();

        //检测文件名是否包含后缀.txt,若无后缀则自动添加
        boolean endText = dialogText.endsWith(".txt");
        if(!endText){
            dialogText=dialogText+".txt";
        }

        //新建txt文件
        File f;
        f = new File(currentParent + "/" + dialogText);
        if (!f.exists()) {
            //若不存在，创建目录，可以在应用启动的时候创建
            if(f.createNewFile()){

                FileOutputStream osw=new FileOutputStream(f,true);

                String s="hello world!";

                osw.write(s.getBytes());

                osw.flush();

                osw.close();
                currentFiles = currentParent.listFiles();
                inflateListView(currentFiles);
                Toast.makeText(MainActivity.this, "成功创建"+f, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this,"failture", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(MainActivity.this,"文件已存在", Toast.LENGTH_SHORT).show();
        }
    }


    //创建一个文件夹
    public void createDir() {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

//            MyDialogT myDialog=new MyDialogT(this) {
//                @Override
//                public void onCreate() {
//                    final EditText inputServer = new EditText(MainActivity.this);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                    builder.setTitle("输入密码").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            String text = inputServer.getText().toString();
//                            dialogText =text;
//
//                        }});
////                    builder.show();
//                }
//            };
//            dialogText= myDialog.showDialog().toString();
//
//            final EditText inputServer = new EditText(MainActivity.this);
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setTitle("输入密码").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
//                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            synchronized(MainActivity.this) {
//                                MainActivity.this.notify();
//                            }
//                            dialog.dismiss();
//
//                        }
//                    });
//            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
////                    synchronized(MainActivity.this) {
////                        MainActivity.this.notify();
////                    }
//                    String text = inputServer.getText().toString();
//                    dialogText=text;
//                }});
//            builder.show();


//
//            new AlertDialog.Builder(this)
//                    .setMessage("Some message...")
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which)  {
////                            synchronized(MainActivity.this) {
////                                MainActivity.this.notify();
////                            }
//                        }
//                    })
//                    .show();
//
//            synchronized(this) {
//                try {
//                    this.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            newfilename="/test1/hhhh/111";
            myDialog myDialogT=new myDialog(this,"请输入文件名");
            dialogText=myDialogT.showDialog();

            File path1 = new File(currentParent+"/"+dialogText);
            if (!path1.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                if(path1.mkdirs()){


                currentFiles = currentParent.listFiles();
                inflateListView(currentFiles);
                    Toast.makeText(MainActivity.this, "成功创建"+path1, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this,"failture", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(MainActivity.this,"文件已存在", Toast.LENGTH_SHORT).show();
            }

        } else {

            Toast.makeText(MainActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
            return;

        }
    }

//删除一个文件夹

    //删除文件夹和文件夹里面的文件
    public void deleteDir() {
        File dir = new File(currentParent+"/"+currentFile.getName());

        Toast.makeText(MainActivity.this,currentFile.getName(), Toast.LENGTH_SHORT).show();
//        if (dir == null || !dir.exists() || !dir.isDirectory()){
////            Toast.makeText(MainActivity.this,"hhhhhhhhhhhhhhhh", Toast.LENGTH_SHORT).show();
//            dir.delete();
//            return;
//        }
        if (dir.isFile()){
            dir.delete();
            currentFiles = currentParent.listFiles();
            inflateListView(currentFiles);
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
//        Toast.makeText(MainActivity.this,"删除成功", Toast.LENGTH_SHORT).show();
        currentFiles = currentParent.listFiles();
        inflateListView(currentFiles);
    }


//打开文本文件
    public void openTextFile(View view,int i) {
        String message="Hello World!!!!!";
        Intent intent = new Intent(this, textFile.class);
        try {
                        File file = new File(currentParent,
                                currentFiles[i].getName());
                        FileInputStream is = new FileInputStream(file);
                        byte[] b = new byte[1024];
                        is.read(b);
                        message = new String(b);
//                        System.out.println("读取成功："+message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
}

    //递归获得文件夹下的文件
    public  void search(String rootPath) throws InterruptedException {
        returnChoice=1;
        myDialog myDialogT=new myDialog(this,"请输入需搜索的关键词");
        dialogText=myDialogT.showDialog();
        List<Map<String, Object>> list = new ArrayList<>();
        searchFiles(currentParent.getPath(),dialogText,list);
//创建适配器
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.item_file_explorer, new String[]{"filename", "icon","date"}, new int[]{R.id.item_name, R.id.item_icon,R.id.item_desc});
        fileLv.setAdapter(adapter);
        pathTv.setText("当前路径:" +currentParent.getAbsolutePath());
//        currentParent=currentParent.listFiles()[0].getAbsoluteFile();//此处用于返回时，返回搜索页面
//        Toast.makeText(MainActivity.this,list.size(), Toast.LENGTH_SHORT).show();


        Log.i("directory not created", ""+list.size());
//        Thread.sleep(2000);//休眠也没有用，listview依然为空
        if(!list.isEmpty()){
//            fileLv.post(new Runnable() {
//                @Override
//                public void run() {
//                    int firstPosition=fileLv.getFirstVisiblePosition();//获得第一个可见的位置
//                    int lastPosition=fileLv.getLastVisiblePosition();
//                    for (int i=0;i<list.size();i++){
////                Log.i("directory not created", ""+i);
//                        Map m=list.get(i);
//                        String ss=m.get("filename").toString();
//                        SpannableString s=new SpannableString(ss);
//                        Pattern p=Pattern.compile(dialogText);
//
//                        Matcher matcher=p.matcher(s);
////                Toast.makeText(MainActivity.this,list.size(), Toast.LENGTH_SHORT).show();
//                        matcher.find();
//                        int start=matcher.start();
//                        int end=matcher.end();
//                        String s1=ss.substring(0,start);
//                        String s2=ss.substring(start,end);
//                        String s3=ss.substring(end);
//                        String Str = s1+" <font color=\"#FF0000\">" + s2+ "</font>"+s3;
//                        Log.i("directory not created", ""+firstPosition+i+lastPosition);
//                        TextView text=fileLv.getChildAt(i+firstPosition+1).findViewById(R.id.item_name);
//                        text.setText(Html.fromHtml(Str));
//                }}
//            });

//
//            for (int i=0;i<list.size();i++){
////                Log.i("directory not created", ""+i);
//                Map m=list.get(i);
//                String ss=m.get("filename").toString();
//                SpannableString s=new SpannableString(ss);
//                Pattern p=Pattern.compile(dialogText);
//
//                Matcher matcher=p.matcher(s);
////                Toast.makeText(MainActivity.this,list.size(), Toast.LENGTH_SHORT).show();
//                matcher.find();
//                int start=matcher.start();
//                int end=matcher.end();
//                String s1=ss.substring(0,start);
//                String s2=ss.substring(start,end);
//                String s3=ss.substring(end);
//                String Str = s1+" <font color=\"#FF0000\">" + s2+ "</font>"+s3;
////                Log.i("directory not created", ""+firstPosition+i+lastPosition);
//                ListView lv=this.findViewById(R.id.id_lv_file);
//                int firstPosition=fileLv.getFirstVisiblePosition();//获得第一个可见的位置，0
//                int lastPosition=fileLv.getLastVisiblePosition();//-1
//                Log.i("getcount", ""+lv.getCount());
//                Log.i("last", ""+lastPosition);
//                Log.i("first", ""+firstPosition);
////                text.setText(Html.fromHtml(Str));
//            }

//            SpannableString s=new SpannableString(list.);
//            Pattern p=Pattern.compile(keyWord);
//        String textStr = "本月已成功邀请 <font color=\"#FF0000\">" + 100 + "</font>人";
//
//        tv.settxt(Html.fromHtml(str));
        }


    }
    public void searchFiles(String rootPath,String keyWord,List<Map<String,Object>> list) {
        File file = new File(rootPath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
//                boolean status = f.getName().contains(keyWord);
                SpannableString s=new SpannableString(f.getName());
                Pattern p=Pattern.compile(keyWord);
                Matcher matcher=p.matcher(s);
                if (matcher.find()){
//                    int start=matcher.start();
//                    int end=matcher.end();
//                    s.setSpan(new ForegroundColorSpan(Color.GREEN), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    Map<String, Object> map = new HashMap<>();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date = new Date(f.lastModified());
                    String time=sdf.format(date);
                    String fn=s.toString();
                    if (!showSuffix && f.isFile()){
                        fn = f.getName().substring(0,f.getName().lastIndexOf("."));//这个是获取除后缀疑问的名称

                    }
                    map.put("filename",fn);
                    map.put("date",time);

                    if (f.isFile()) {
                        map.put("icon", R.mipmap.file);
                    } else {
                        map.put("icon", R.mipmap.folder);
                    }
                    list.add(map);
                }
                if (f.isDirectory()) {
                    searchFiles(f.getAbsolutePath(),keyWord,list);//自己调用自己
                }
                }
            }
        return;
        }



    //监听事件
    private void setListener() {
        fileLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //i为position，l为id

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                returnChoice=0;
                if (currentFiles[i].isFile()) {
                    openTextFile(view,i);
                    return;
                }
                //获取当前点击文件夹当中的所有文件
                File[] temp = currentFiles[i].listFiles();

                    //修改这项被点击的父目录，重新设置适配器内容
                    currentParent = currentFiles[i];
                    currentFiles = temp;
                    inflateListView(currentFiles);

            }
        });
//返回键点击事件
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this,returnChoice, Toast.LENGTH_SHORT).show();
                if (returnChoice==1){
                    currentParent=currentParent.listFiles()[0].getAbsoluteFile();
                    returnChoice=0;
                }
                //判断当前目录是否为sd卡的根目录，如果是根目录，就退出activity，如果不是则获取当前目录的父目录，然后重新加载listView
                if (currentParent.getAbsolutePath().equals(root.getAbsolutePath())) {
                    MainActivity.this.finish();
                } else {
                    currentParent = currentParent.getParentFile();
                    currentFiles = currentParent.listFiles();
                    inflateListView(currentFiles);
                }
            }
        });
    }

//将文件列表渲染到界面
private void inflateListView(File[] currentFiles) {
    List<Map<String, Object>> list = new ArrayList<>();

    for (int i = 0; i < currentFiles.length; i++) {
        Map<String, Object> map = new HashMap<>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(currentFiles[i].lastModified());
        String time=sdf.format(date);
        String fn=currentFiles[i].getName();
        if (!showSuffix && currentFiles[i].isFile()){
           fn = currentFiles[i].getName().substring(0,currentFiles[i].getName().lastIndexOf("."));//这个是获取除后缀疑问的名称

        }
        map.put("filename",fn);
        map.put("date",time);

        if (currentFiles[i].isFile()) {
            map.put("icon", R.mipmap.file);
        } else {
            map.put("icon", R.mipmap.folder);
        }
        list.add(map);
    }


    //创建适配器
    SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.item_file_explorer, new String[]{"filename", "icon","date"}, new int[]{R.id.item_name, R.id.item_icon,R.id.item_desc});
    fileLv.setAdapter(adapter);
    pathTv.setText("当前路径:" + currentParent.getAbsolutePath());


    registerForContextMenu(fileLv);
    fileLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            fileLv.showContextMenu();
            currentFile=currentFiles[position];
            return true;
        }
    });
    fileLv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.item_context, menu);
        }

    });
}
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {

            case R.id.item_copy:
                Toast.makeText(MainActivity.this, "点击复制", Toast.LENGTH_LONG).show();
                return true;
            case R.id.item_cut:
                Toast.makeText(MainActivity.this, "点击剪切", Toast.LENGTH_LONG).show();
                return true;
            case R.id.item_move:
                Toast.makeText(MainActivity.this, "点击移动", Toast.LENGTH_LONG).show();
                return true;
            case R.id.item_delete:
                deleteDir();
                return true;
            default:
                break;
        }
        return false;
    }

}