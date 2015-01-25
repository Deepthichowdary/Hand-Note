package com.example.talla_000.handnote;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import android.app.ActionBar;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;


public class MyActivity extends ActionBarActivity {
    //View v;
    private DrawingView dv;
    private static Paint mPaint;
    private SQLiteDatabase db;
    private Bitmap mBitmap;
    private static Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Context context;
    private NotesDBHelper dbHelper;
    private Notes mNotes;
    private NotesDAO mNotesDAO;
    private ShareActionProvider mShareActionProvider;
    private File mypath;
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private ArrayList<Path> paths = new ArrayList<Path>();
    private final static float SMALL_STROKE_WIDTH=6.0f;
    private final static float MEDIUM_STROKE_WIDTH=12.0f;
    private final static float BIG_STROKE_WIDTH=40.0f;
    private final static float FILL_STROKE_WIDTH=40.0f;
    private final static float DEFAULT_STROKE_WIDTH=3.0f;
    private final static float SMALL_ERASER_WIDTH=40.0f;
    private final static float MEDIUM_ERASER_WIDTH=80.0f;
    private final static float BIG_ERASER_WIDTH=160.0f;
    private final static float DEFAULT_ERASER_WIDTH=50.0f;
    private static int LOAD_IMAGE_RESULTS = 1;
    private static boolean ERASE_FLAG=false;
    private static String NOTES_NAME="MyNote";


    @Override
    protected void onPause(){
        super.onPause();
        if(mBitmap!=null){
            String path=saveToInternalSorage(mBitmap);
            Notes n=new Notes();
            n.setName(NOTES_NAME);
            n.setContent(path);
            //n.setContent(array);
            mNotesDAO.createNotes(n);
            //   addEntry("MyNote",array);
        }


    }
    @Override
    protected void onStop(){
        super.onStop();

    }
    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        mypath=new File(directory,"notes.png");
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotesDAO=new NotesDAO(this);
        if (paths.size() > 0) {
            undonePaths.add(paths
                    .remove(paths.size() - 1));
            dv.invalidate();
        }
        //if (savedInstanceState != null) mBitmap = savedInstanceState.getParcelable("bitmap");
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        dv = new DrawingView(this);
        dv.setDrawingCacheEnabled(true);
        //dv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        dv.setBackgroundColor(Color.WHITE);
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            // Now we need to set the GUI ImageView data with data read from the picked file.
            //image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            try {
                //File f = new File(imagePath, "notes.jpg");
                    mBitmap = BitmapFactory.decodeFile(imagePath);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);

                    builder.setMessage(R.string.Del_conf_message);
                    builder.setTitle(R.string.title_confirm);

                    LayoutInflater factory = LayoutInflater.from(MyActivity.this);
                    final View view = factory.inflate(R.layout.custom_dialog,null);

                    ImageView i = (ImageView) view.findViewById(R.id.imageView1);
                    i.setImageBitmap(mBitmap);
                    builder.setNeutralButton("Here!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                            builder.setCancelable(true);
                        }
                    });
                    builder.setView(view);
                    builder.show();

                mBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    mCanvas = new Canvas(mBitmap);
                    invalidateOptionsMenu();
                    dv.invalidate();


            } catch(Exception e){
                e.printStackTrace();
                (Toast.makeText(getApplicationContext(),R.string.file_load_error,Toast.LENGTH_LONG)).show();
            }

            // At the end remember to close the cursor or you will end with the RuntimeException!
            cursor.close();
        }
    }

   /* public boolean openGallery(MenuItem menuItem) {
        *//*Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse(
                "content://media/internal/images/media"));*//*
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, LOAD_IMAGE_RESULTS);
        return true;
    }*/
    public boolean deleteDrawing(MenuItem menuItem) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
        builder.setMessage(R.string.Del_conf_message);
        builder.setTitle(R.string.title_confirm);

        // Add the buttons
        builder.setPositiveButton(R.string.Button_Positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dv.setBackgroundColor(Color.WHITE);
                mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                if(mNotesDAO!=null){
                    System.out.print(context.getDatabasePath(dbHelper.getDatabaseName()));
                    // mNotesDAO.deleteNotes("notes");
                    getApplicationContext().deleteFile("note.png");
                    (Toast.makeText(getApplicationContext(),R.string.del_success,Toast.LENGTH_LONG)).show();
                    mNotesDAO=new NotesDAO(getApplicationContext());
                    invalidateOptionsMenu();
                }
                else{
                    (Toast.makeText(getApplicationContext(),R.string.del_fail,Toast.LENGTH_LONG)).show();
                }

            }
        });
        builder.setNegativeButton(R.string.Button_Negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                builder.setCancelable(true);
            }
        });


        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }
    public boolean setEraseFlag(MenuItem menuItem){
        ERASE_FLAG=true;
        //PENCIL_FLAG=false;
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        //mPaint.setColor(Color.WHITE);
        switch (menuItem.getItemId()) {
            case R.id.action_erase_small:
                mPaint.setStrokeWidth(SMALL_ERASER_WIDTH);
                return true;
            case R.id.action_erase_medium:
                mPaint.setStrokeWidth(MEDIUM_ERASER_WIDTH);
                return true;
            case R.id.action_erase_big:
                mPaint.setStrokeWidth(BIG_ERASER_WIDTH);
                return true;
            case R.id.action_erase_fill:
                clearDrawing();
               return true;
            /*default:
                mPaint.setStrokeWidth(DEFAULT_ERASER_WIDTH);
                return true;*/
        }
        return true;
    }
    public void clearDrawing(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
        builder.setMessage(R.string.erase_conf_message);
        builder.setTitle(R.string.title_confirm);

        // Add the buttons
        builder.setPositiveButton(R.string.Button_Positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                invalidateOptionsMenu();
            }

        });
        builder.setNegativeButton(R.string.Button_Negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                builder.setCancelable(true);
            }
        });

    }
    public boolean saveToGallery(MenuItem menuItem){
        if(mBitmap!=null) {
            saveToExternalSorage();
            //invalidateOptionsMenu();
            (Toast.makeText(getApplicationContext(),R.string.save_success,Toast.LENGTH_LONG)).show();
        } else{
            (Toast.makeText(getApplicationContext(),R.string.empty_error,Toast.LENGTH_LONG)).show();
        }
        return true;
    }
    public boolean setPencilFlag(MenuItem menuItem){
        //PENCIL_FLAG=true;
        ERASE_FLAG=false;
        mPaint.setXfermode(null);
        switch (menuItem.getItemId()) {
            case R.id.smallPencil:
                mPaint.setStrokeWidth(SMALL_STROKE_WIDTH);
                return true;
            case R.id.mediumPencil:
                mPaint.setStrokeWidth(MEDIUM_STROKE_WIDTH);
                return true;
            case R.id.bigPencil:
                mPaint.setStrokeWidth(BIG_STROKE_WIDTH);
                return true;
            case R.id.fill:
                dv.setBackgroundColor(mPaint.getColor());
                if(mPaint.getColor()!=Color.WHITE){
                    mPaint.setColor(Color.WHITE);
                } else{
                    mPaint.setColor(Color.BLACK);
                }
                return true;
            default:
                mPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
                return true;
        }

    }
    public boolean setPaintColor(MenuItem menuItem){
        if(ERASE_FLAG){ mPaint.setStrokeWidth(MEDIUM_STROKE_WIDTH);}
        //PENCIL_FLAG=true;
        ERASE_FLAG=false;
        mPaint.setXfermode(null);
        switch (menuItem.getItemId()) {
            case R.id.red:
                mPaint.setColor(Color.RED);
                return true;
            case R.id.green:
                mPaint.setColor(Color.GREEN);
                return true;
            case R.id.black:
                mPaint.setColor(Color.BLACK);
                return true;
            case R.id.Megenta:
                mPaint.setColor(Color.MAGENTA);
                return true;
            case R.id.Blue:
                mPaint.setColor(Color.BLUE);
                return true;
            case R.id.White:
                mPaint.setColor(Color.WHITE);
                return true;
            case R.id.Pink:
                mPaint.setColor(getResources().getColor(R.color.color_pink));
                return true;
            case R.id.Orange:
                mPaint.setColor(getResources().getColor(R.color.color_orange));
                return true;
            case R.id.purple:
                mPaint.setColor(getResources().getColor(R.color.color_purple));
                return true;
            case R.id.Yellow:
                mPaint.setColor(Color.YELLOW);
                return true;
            case R.id.Brown:
                mPaint.setColor(getResources().getColor(R.color.color_brown));
                return true;

        }


        //mPaint.setStrokeWidth(DESTROKE_WIDTH);
        //mPaint.setColor(Color.BLACK);
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();

        if(dv==null){
            dv =new DrawingView(context);
        }
        dbHelper= new NotesDBHelper(this);
        db = dbHelper.getWritableDatabase();
        if(mNotesDAO!=null) {
            mNotes = mNotesDAO.getNotes(NOTES_NAME);
            // byte[] image = cursor.getBlob(1);
            if(mNotes!=null&& mNotes.getContent()!=null) {
                //mBitmap = BitmapFactory.decodeByteArray(mNotes.getContent(), 0, mNotes.getLength());
                loadImageFromStorage(mNotes.getContent());
                Bitmap mutableBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                mBitmap=mutableBitmap;
                mCanvas = new Canvas(mBitmap);
                invalidateOptionsMenu();
            }
        } else{
            mNotesDAO=new NotesDAO(this);
        }

    }
    private void  loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "notes.png");
            mBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imgPicker);
            //img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


    }
    public boolean onClickUndo(MenuItem menuItem){
        if (paths.size()>0)
        {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidateOptionsMenu();
        }
        return true;

    }

    public boolean onClickRedo(MenuItem menuItem){
        if (undonePaths.size()>0)
        {
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidateOptionsMenu();
        }
        return true;

    }
    public class DrawingView extends View {
        public int width;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path(); // path used to draw
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.WHITE);
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
            // draw a circle when user touch and move.
        }


        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);


            if(mBitmap!=null){
                //Bitmap workingBitmap = Bitmap.createBitmap(chosenFrame);
                Bitmap mutableBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                mBitmap=mutableBitmap;
                mCanvas = new Canvas(mBitmap);
                return;
            }
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);


        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            dv.setDrawingCacheEnabled(true);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            if(!ERASE_FLAG) {
                for (Path p : paths){
                    canvas.drawPath(p, mPaint);
                }
                canvas.drawPath(mPath, mPaint); // draw the path
            }

            canvas.drawPath(circlePath, circlePaint); //draw the circle
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            undonePaths.clear();
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if(dv.getDrawingCacheBackgroundColor()!=mPaint.getColor()) {
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                    mPath.lineTo(mX, mY);
                    circlePath.reset();
                    circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
                    mCanvas.drawPath(mPath, mPaint);
                    paths.add(mPath);
                }
            }
            else{
                (Toast.makeText(getApplicationContext(),"Select other color different from background!!",Toast.LENGTH_LONG)).show();
            }
        }

        private void touch_up() {
            //mPath.lineTo(mX, mY);
            circlePath.reset();
           /* if(!ERASE_FLAG){
                mPaint.setXfermode(null);
            }
           */ /*else if(PENCIL_FLAG) {
            */    // commit the path to our offscreen
            //mCanvas.drawPath(mPath, mPaint);
            //}
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) { // touch events
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate(); // invalidate to refresh
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.my, menu);
       mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_item_share).getActionProvider();

        return true;
    }
    public boolean setShareIntent(MenuItem menuItem){

        getDefaultShareIntent();
        //setShareIntent();
        if(mShareActionProvider!=null) {
            //** Setting a share intent *//*
            mShareActionProvider.setShareIntent(getDefaultShareIntent());
        }

        return true;
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

    }
    public String saveToExternalSorage(){
        File file=null;
        try {
            Bitmap b=null;
            if (dv != null&& dv.getDrawingCache()!=null) {
                b = Bitmap.createBitmap(dv.getDrawingCache());
                //dv.setDrawingCacheEnabled(false);
            }
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/hand_note");
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String fname = "Image_" + timeStamp + "_" + n + ".png";
            file = new File(myDir, fname);
            if (file.exists()) {
                file.delete();
            }

                FileOutputStream out = new FileOutputStream(file);
                if (b != null)
                    b.compress(Bitmap.CompressFormat.JPEG, 90, out);
                else {
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                }
                out.flush();
                out.close();

        } catch (Exception e) {
            e.printStackTrace();
            (Toast.makeText(getApplicationContext(),R.string.action_error,Toast.LENGTH_LONG)).show();
        }
       /* sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
       */
        MediaScannerConnection.scanFile(this,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        //Intentionally left blank not to print anything
                    }
                });
        return file.getAbsolutePath();
    }

    private Intent getDefaultShareIntent(){

        String imagePath=saveToExternalSorage();
        File f=new File(imagePath);
        //File file = new File(imagePath+"/notes.jpg");
        Uri screenshotUri = Uri.fromFile(f);
        //Uri screenshotUri = Uri.parse(imagePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
        return sharingIntent;
    }

}