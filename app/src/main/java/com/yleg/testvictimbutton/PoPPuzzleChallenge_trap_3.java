package com.yleg.testvictimbutton;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.SecureRandom;
import java.util.ArrayList;

public class PoPPuzzleChallenge_trap_3 extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long starttime;
    AnimatedView animatedView = null;
    public static int x;
    public static int y;
    private int[] correctpath;
    private int[] wrongpath;
    int ballRadius = 15;
    private int currLevel;
    private String currtext1;
    private String currtext2;
    private int shift =0;
    private int currPassedLevel;
    public int startx;
    public int starty;
    private int mindepth=10;
    private int width = 20;
    private int depth;
    private String popText = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        try{
            int dpt = getIntent().getExtras().getInt("PoPDepth",2);
            String popt = getIntent().getExtras().getString("PoPText","");
            popText = popt;
            depth =Math.min(dpt,mindepth);
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }catch (Exception e){

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupPuzzle();
        animatedView = new AnimatedView(this);
        setContentView(animatedView);
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void setupPuzzle(){
        starttime = System.currentTimeMillis();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        shift = screenHeight/8;
        //starty=y=11 + (2*ballRadius);

        starty =screenHeight/4-(ballRadius*2);
        startx =((3*screenWidth)/8)+ballRadius;
        currtext1 = "Place ball in ";
        currtext2 = "green zone";
        correctpath = new int[depth];
        wrongpath = new int[depth];
        currLevel = currPassedLevel = 0;
        SecureRandom random = new SecureRandom();
        for(int i=0;i<depth;i++){
            correctpath[i] = random.nextInt(8) ;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x -= (int) event.values[0]*2.5;
            y += (int) (event.values[1] - 1)*2.5;
        }
    }

    @SuppressLint("AppCompatCustomView")
    public class AnimatedView extends ImageView {
        ArrayList<ShapeDrawable> sectionList = new ArrayList<ShapeDrawable>();
        ArrayList<ShapeDrawable> rectList = new ArrayList<ShapeDrawable>();
        ArrayList<int[]> nodePosList = new ArrayList<int[]>();
        Path fullPath = new Path();
        ShapeDrawable mBall = new ShapeDrawable(new OvalShape());
        ShapeDrawable mTargetGreen = new ShapeDrawable(new RectShape());
        ShapeDrawable mTargetUp = new ShapeDrawable(new RectShape());
        ArrayList<ShapeDrawable> mWrongTargets = new ArrayList<ShapeDrawable>();
        ArrayList<ShapeDrawable> nodeList = new ArrayList<ShapeDrawable>();
        Paint mPaintPath = new Paint();
        public AnimatedView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            drawSections(correctpath[currLevel]);
            drawPaths();
        }

        private void drawSplitInRect(int left, int top, int right, int bottom){
            width = 20;

            ShapeDrawable mDrawableRect = new ShapeDrawable(new RectShape());
            mDrawableRect.getPaint().setColor(Color.BLACK);
            mDrawableRect.setBounds((left + ((right-left)/4))-width, top-width, (left + ((right-left)/4))+width, bottom+width);
            rectList.add(mDrawableRect);

            ShapeDrawable mDrawableRect2 = new ShapeDrawable(new RectShape());
            mDrawableRect2.getPaint().setColor(Color.BLACK);
            mDrawableRect2.setBounds((left + ((3*(right-left))/4))-width , top-width, width + left + ((3*(right-left))/4), bottom + width);
            rectList.add(mDrawableRect2);

            ShapeDrawable mDrawableRect3 = new ShapeDrawable(new RectShape());
            mDrawableRect3.getPaint().setColor(Color.BLACK);
            mDrawableRect3.setBounds((left + ((right-left)/4))-width , top-width, width + left + ((3*(right-left))/4), top + width);
            rectList.add(mDrawableRect3);

        }
        //path for ball to move in
        private void drawPaths() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            int screenWidth = displayMetrics.widthPixels;
            width = 20;
            shift = screenHeight/8;
            mBall.getPaint().setColor(Color.GREEN);
            starty =screenHeight/4-(ballRadius*2);
            startx =((3*screenWidth)/8)+ballRadius;
            mBall.setBounds(startx,starty,startx+ballRadius*2,starty+ballRadius*2 );

            ShapeDrawable mEntryRect = new ShapeDrawable(new RectShape());
            mEntryRect.getPaint().setColor(Color.BLACK);
            mEntryRect.setBounds((screenWidth/2)-width,0,(screenWidth/2)+width,screenHeight/4) ;
            rectList.add(mEntryRect);

            ShapeDrawable mitigation = new ShapeDrawable(new RectShape());
            mitigation.getPaint().setColor(Color.BLACK);
            mitigation.setBounds(((3*screenWidth)/8),screenHeight/4-(width*2),(5*screenWidth/8),screenHeight/4) ;
            rectList.add(mitigation);

            drawSplitInRect(0,0+shift, screenWidth, screenHeight/4+shift);

            drawSplitInRect(0,screenHeight/4+shift, screenWidth/2, screenHeight/2+shift);
            drawSplitInRect(screenWidth/2,screenHeight/4+shift, screenWidth, screenHeight/2+shift);

            drawSplitInRect(0,screenHeight/2+shift, screenWidth/4, (screenHeight)/2+2*shift);
            drawSplitInRect(screenWidth/4,screenHeight/2+shift, screenWidth/2, (screenHeight)/2+2*shift);
            drawSplitInRect(screenWidth/2,screenHeight/2+shift, (3*screenWidth/4), (screenHeight)/2+2*shift);
            drawSplitInRect((3*screenWidth/4),screenHeight/2+shift, screenWidth, (screenHeight)/2+2*shift);
            //drawBottomNodes();
            drawGuide();

        }


        private void drawGuide() {
            fullPath.reset();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            int screenWidth = displayMetrics.widthPixels;
            int shift= screenHeight/8;
            if(currPassedLevel>= currLevel && currLevel<correctpath.length){
                int greenpos = correctpath[currLevel];

                fullPath.moveTo(startx , starty+ballRadius);
                fullPath.lineTo(screenWidth/2 , starty+ballRadius);
                fullPath.lineTo(screenWidth/2 , shift);

                if(greenpos==0){
                    fullPath.lineTo(screenWidth/4 , shift);
                    fullPath.lineTo(screenWidth/4 , screenHeight/4+shift);
                    fullPath.lineTo(screenWidth/8 , screenHeight/4+shift);
                    fullPath.lineTo(screenWidth/8 , screenHeight/2+shift);
                    fullPath.lineTo(screenWidth/8 - screenWidth/16 , screenHeight/2+shift);
                    fullPath.lineTo(screenWidth/8 - screenWidth/16 , screenHeight/2+2*shift);

                }else if(greenpos==1){
                    fullPath.lineTo(screenWidth/4 , shift);
                    fullPath.lineTo(screenWidth/4 , screenHeight/4+shift);
                    fullPath.lineTo(screenWidth/8 , screenHeight/4+shift);
                    fullPath.lineTo(screenWidth/8 , screenHeight/2+shift);
                    fullPath.lineTo(screenWidth/8  + screenWidth/16, screenHeight/2+shift);
                    fullPath.lineTo(screenWidth/8  + screenWidth/16, screenHeight/2+2*shift);

                }else if(greenpos==2){
                    fullPath.lineTo(screenWidth/4 , shift);
                    fullPath.lineTo(screenWidth/4 , screenHeight/4+shift);
                    fullPath.lineTo(screenWidth/4 + screenWidth/8 , screenHeight/4+shift);
                    fullPath.lineTo(screenWidth/4 + screenWidth/8 , screenHeight/2+shift);
                    fullPath.lineTo(screenWidth/4 + screenWidth/8 - screenWidth/16 , screenHeight/2+shift);
                    fullPath.lineTo(screenWidth/4 + screenWidth/8 - screenWidth/16 , screenHeight/2+2*shift);

                }else if(greenpos==3){
                    fullPath.lineTo(screenWidth/4 , shift);
                    fullPath.lineTo(screenWidth/4 , screenHeight/4+shift);
                    fullPath.lineTo(screenWidth/4 + screenWidth/8 , screenHeight/4+shift);
                    fullPath.lineTo(screenWidth/4 + screenWidth/8 , screenHeight/2+shift);
                    fullPath.lineTo(screenWidth/4 + screenWidth/8 + screenWidth/16, screenHeight/2+shift);
                    fullPath.lineTo(screenWidth/4 + screenWidth/8 + screenWidth/16, screenHeight/2+2*shift);

                }else if(greenpos==4){
                    fullPath.lineTo((3*screenWidth)/4 , shift);
                    fullPath.lineTo((3*screenWidth)/4 , screenHeight/4+shift);
                    fullPath.lineTo((3*screenWidth)/4 - screenWidth/8 , screenHeight/4+shift);
                    fullPath.lineTo((3*screenWidth)/4 - screenWidth/8 , screenHeight/2+shift);
                    fullPath.lineTo((3*screenWidth)/4 - screenWidth/8 - screenWidth/16 , screenHeight/2+shift);
                    fullPath.lineTo((3*screenWidth)/4 - screenWidth/8 - screenWidth/16 , screenHeight/2+2*shift);

                }else if(greenpos==5){
                    fullPath.lineTo((3*screenWidth)/4 , shift);
                    fullPath.lineTo((3*screenWidth)/4 , screenHeight/4+shift);
                    fullPath.lineTo((3*screenWidth)/4 - screenWidth/8 , screenHeight/4+shift);
                    fullPath.lineTo((3*screenWidth)/4 - screenWidth/8 , screenHeight/2+shift);
                    fullPath.lineTo((3*screenWidth)/4 - screenWidth/8 + screenWidth/16 , screenHeight/2+shift);
                    fullPath.lineTo((3*screenWidth)/4 - screenWidth/8 + screenWidth/16 , screenHeight/2+2*shift);

                }else if(greenpos==6){
                    fullPath.lineTo((3*screenWidth)/4 , shift);
                    fullPath.lineTo((3*screenWidth)/4 , screenHeight/4+shift);
                    fullPath.lineTo((3*screenWidth)/4 +screenWidth/8, screenHeight/4+shift);
                    fullPath.lineTo((3*screenWidth)/4 +screenWidth/8, screenHeight/2+shift);
                    fullPath.lineTo((3*screenWidth)/4 +screenWidth/8 - screenWidth/16, screenHeight/2+shift);
                    fullPath.lineTo((3*screenWidth)/4 +screenWidth/8 - screenWidth/16, screenHeight/2+2*shift);

                }else if(greenpos==7){
                    fullPath.lineTo((3*screenWidth)/4 , shift);
                    fullPath.lineTo((3*screenWidth)/4 , screenHeight/4+shift);
                    fullPath.lineTo((3*screenWidth)/4 +screenWidth/8 , screenHeight/4+shift);
                    fullPath.lineTo((3*screenWidth)/4 +screenWidth/8 ,screenHeight/2+shift);
                    fullPath.lineTo((3*screenWidth)/4 +screenWidth/8 + screenWidth/16,screenHeight/2+shift);
                    fullPath.lineTo((3*screenWidth)/4 +screenWidth/8 + screenWidth/16,screenHeight/2+2*shift);

                }
                //fullPath.close();

            }


            int max = (depth*2)-(currLevel*2);
            mPaintPath.setStyle(Paint.Style.STROKE);
            mPaintPath.setColor(Color.GREEN);
            mPaintPath.setStrokeWidth(8f);


        }


        //draw Color sections with green at position greenpos
        private void drawSections(int greenpos){
            sectionList.clear();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            int screenWidth = displayMetrics.widthPixels;
            shift = screenHeight/8;
            int rectWidth=screenWidth/8;
            int rectHeight=screenHeight/2 - 3*(shift/2);
            int[] colors = {Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.GRAY, Color.CYAN, Color.BLACK, Color.LTGRAY};

            mTargetUp= new ShapeDrawable(new RectShape());
            mTargetUp.setBounds(0, 0, screenWidth , 10 );
            mTargetUp.getPaint().setColor(Color.GREEN);
            mTargetUp.setAlpha(60);

            int colcount = 0;
            mWrongTargets.clear();
            for(int i = 0; i<8; i++){
                ShapeDrawable mDrawableSection = new ShapeDrawable(new RectShape());

                mDrawableSection.setBounds(rectWidth*i, screenHeight - rectHeight, rectWidth*(i+1) , screenHeight );
                if(i==greenpos){
                    mDrawableSection.getPaint().setColor(Color.GREEN);
                    ShapeDrawable placeholder = new ShapeDrawable(new RectShape());
                    mWrongTargets.add(placeholder);
                    mTargetGreen = mDrawableSection;
                }else{
                    mDrawableSection.getPaint().setColor(colors[colcount]);
                    mWrongTargets.add(mDrawableSection);
                    colcount++;
                }
                mDrawableSection.getPaint().setAlpha(60);

                sectionList.add(mDrawableSection);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {

            ArrayList<ShapeDrawable> containedRects = new ArrayList<>();
            for (ShapeDrawable rc:rectList
            ) {
                rc.draw(canvas);
                if (contains(rc,mBall)){
                    containedRects.add(rc);
                }
            }
            for (ShapeDrawable sd:sectionList
                 ) {
                sd.draw(canvas);
            }
            for (ShapeDrawable nd:nodeList
            ) {
                nd.draw(canvas);
            }
            if(containedRects.size()==1){
                ShapeDrawable rect = containedRects.get(0);
                y = Math.max(y,rect.getBounds().top + ballRadius);
                y = Math.min(y,rect.getBounds().bottom -ballRadius);
                x = Math.max(x,rect.getBounds().left +ballRadius);
                x = Math.min(x,rect.getBounds().right - ballRadius);

            }else if(containedRects.size()==2){
                ShapeDrawable horrect ;
                ShapeDrawable verrect ;
                ShapeDrawable rect1 = containedRects.get(0);
                ShapeDrawable rect2 = containedRects.get(1);
                boolean isMitigation = false;
                if( isHorizontal(rect1)&& isHorizontal(rect2)){
                    horrect = rect1;
                    verrect = rect2;
                    isMitigation = true;
                }else if(isHorizontal(rect1)){
                    horrect = rect1;
                    verrect = rect2;
                    isMitigation = false;
                }else{
                    horrect = rect2;
                    verrect = rect1;
                    isMitigation = false;
                }

                int hortop = horrect.getBounds().top + ballRadius+1;
                int horbottom = horrect.getBounds().bottom -ballRadius-1;
                int horleft = horrect.getBounds().left +ballRadius+1;
                int horright =horrect.getBounds().right - ballRadius-1;
                int vertop = verrect.getBounds().top + ballRadius+1;
                int verbottom = verrect.getBounds().bottom -ballRadius-1;
                int verleft = verrect.getBounds().left +ballRadius+1;
                int verright =verrect.getBounds().right - ballRadius-1;

                if(isMitigation){
                    y = Math.max(y,hortop);
                    y = Math.min(y,horbottom);
                }else {
                    y = Math.max(y,vertop);
                    y = Math.min(y,verbottom);
                }
                if(y<hortop){
                    x = Math.max(x,verleft);
                    x = Math.min(x, verright);
                }else if(y>horbottom){
                    x = Math.max(x,verleft);
                    x = Math.min(x, verright);
                }else {
                    if(isMitigation){
                        x = Math.max(x,verleft);
                        x = Math.min(x, verright);
                    }else {
                        x = Math.max(x,horleft);
                        x = Math.min(x, horright);
                    }

                }
            }

            //make sure the ball remains inside the paths
            ShapeDrawable mTempBall = new ShapeDrawable(new OvalShape());
            mTempBall.setBounds(x-ballRadius, y-ballRadius, x+ballRadius , y+ballRadius);
            boolean isStillInPath = false;
            for (ShapeDrawable rc:rectList
            ) {
                if (contains(rc,mTempBall)){
                    isStillInPath = true;
                }
            }
            if(isStillInPath){
                mBall.setBounds(mTempBall.getBounds());

            }
            mTargetUp.setAlpha(80);
            mBall.draw(canvas);
            mTargetUp.draw(canvas);

            canvas.drawPath(fullPath, mPaintPath);

            Paint paint2=new Paint();
            paint2.setTextSize(60);
            Paint paint3=new Paint();
            paint3.setTextSize(50);
            paint3.setColor(Color.WHITE);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            canvas.drawText(currtext1, 140- paint2.getTextSize(), 100, paint2);
            canvas.drawText(currtext2, (screenWidth/2) + 140- paint2.getTextSize(), 100, paint2);
            canvas.drawText(popText, (screenWidth/4), screenHeight/8 +10, paint3);
            boolean hitWrongTarget = false;
            int wrongTargetId = -1;
            for (int i=0; i<mWrongTargets.size(); i++) {
                if(intersects(mWrongTargets.get(i),mBall)){
                    hitWrongTarget = true;
                    wrongTargetId = i;
                }
            }

            if(intersects(mTargetGreen, mBall)){
                x = startx;
                y=starty;
                mBall.setBounds(startx, starty, startx+ballRadius*2, starty+(2*ballRadius));
                currLevel+=1;
                currPassedLevel+=1;
                if(currLevel<depth){
                    drawSections(correctpath[currLevel]);
                    currtext1 = "Current level: " ;
                    currtext2 = (currPassedLevel+1) + "/"+ depth;
                }else{
                    currtext1 = "You win!";
                    currtext2 = (currPassedLevel+1) + "/"+ depth;
                    long duration = System.currentTimeMillis()-  starttime ;
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("PoPPuzzle",true);
                    returnIntent.putExtra("PoPPuzzleTime",duration);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
                drawGuide();
             //   drawBottomNodes();
            }else if (hitWrongTarget){
                mTargetGreen =  new ShapeDrawable(new RectShape());
                x=startx;
                y=starty;
                mBall.setBounds(startx, starty, startx+ballRadius*2, starty+(2*ballRadius));
                wrongpath[currLevel] = wrongTargetId;
                currLevel+=1;
                if(currLevel<depth){
                    drawSections(10);
                    currtext1 = "Wrong path" ;
                    currtext2 = (currLevel+1) + "/"+ depth ;
                }else{
                    currtext1 = "You Failed!" ;
                    currtext2 = "You Failed!";
                    //currtext2 = (currLevel+1) + "/"+ depth + "(" + currPassedLevel+")";

                    long duration = System.currentTimeMillis()-  starttime ;
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("PoPPuzzle",true);
                    returnIntent.putExtra("PoPPuzzleTime",duration);
                    setResult(Activity.RESULT_CANCELED,returnIntent);
                    finish();
                }
                drawGuide();
              //  drawBottomNodes();
            }else if (intersects(mTargetUp, mBall)&& currLevel>currPassedLevel){

                mTargetGreen =  new ShapeDrawable(new RectShape());
                currLevel=currLevel-1;
                ShapeDrawable oldTarget = mWrongTargets.get(wrongpath[currLevel]);
                x=oldTarget.getBounds().left + ((oldTarget.getBounds().right - oldTarget.getBounds().left)/2);
                y=oldTarget.getBounds().top-100;
                mBall.setBounds(x-ballRadius, y, x, y+(2*ballRadius));
                if(currPassedLevel<currLevel){
                    drawSections(10);
                    currtext1 = "Wrong path" ;
                    currtext2 = "Return UP!";
                    currtext2 = (currLevel+1) + "/"+ depth + "(" + currPassedLevel+")";
                }else{
                    drawSections(correctpath[currLevel]);
                    currtext1 = "Current level: " ;
                    currtext2 = (currPassedLevel+1) + "/"+ depth;
                    //currtext2 = (currLevel+1) + "/"+ depth + "(" + currPassedLevel+")";
                }
                drawGuide();
               // drawBottomNodes();
            }

            invalidate();
        }
    }

    private boolean contains(ShapeDrawable path, ShapeDrawable ball){
        if(path.getBounds().contains(ball.getBounds().left,ball.getBounds().top, ball.getBounds().right,ball.getBounds().bottom)){
            return true;
        }else return false;
    }

    private boolean isHorizontal(ShapeDrawable path){
        if(path.getBounds().right-path.getBounds().left > path.getBounds().bottom-path.getBounds().top){
            return true;
        }else return false;
    }


    private boolean intersects(ShapeDrawable path, ShapeDrawable ball){
        if(path.getBounds().intersects(ball.getBounds().left,ball.getBounds().top, ball.getBounds().right,ball.getBounds().bottom)){
            return true;
        }else return false;
    }
}
