package si.pronic.zarja;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;


/**
 * Main activity
 */
public class MainActivity extends AppCompatActivity
{
    // Delay constants regarding the speed
    public static final int DELAY[] = {800, 500, 200};

    // Number of columns and rows
    public static final int NUM_COLS = 4;
    public static final int NUM_ROWS = 5;

    // Fake constants that really change in app :)
    private int MAX = 0;
    private int PARK_RIGHT_X_POS = -1;
    private int PARK_RIGHT_Y_POS = 0;
    private int PARK_LEFT_X_POS = 4;
    private int PARK_LEFT_Y_POS = 0;
    public Point COORD[] = COORD_RIGHT;


    // App context
    private static Context context;

    // App manager
    private ApplicationManager appMgr;

    // List of installed apps
    private List<AppLaunchInfo> appList;

    // Icon park position
    private Point parkPos = new Point(PARK_RIGHT_X_POS, PARK_RIGHT_Y_POS);

    // X and Y position
    private float posX;
    private float posY;

    // Screen width and height
    private int screenWidth = -1;
    private int screenHeight = -1;

    // Width and height of part containing icon
    private int colPart = -1;
    private int rowPart = -1;

    // States
    private boolean pressed = false;
    private boolean away = false;
    private boolean iconMoving = false;

    // Icon size
    private int iconSize;

    // Vibrator
    private Vibrator vibrator;

    // Pointer pointing to first shown icon/app
    private int pointer = 0;


    /**
     * Creates UI
     * @param savedInstanceState State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        ZarjaProperties.load();

        AI.getInstance();

        MAX = NUM_COLS * NUM_ROWS - 1;
        appMgr = new ApplicationManager(getApplicationContext());
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        colPart = screenWidth / NUM_COLS;
        rowPart = screenHeight / NUM_ROWS;

        switch(metrics.densityDpi)
        {
            case DisplayMetrics.DENSITY_LOW:
                iconSize = 36;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                iconSize = 48;
                break;
            case DisplayMetrics.DENSITY_HIGH:
            case DisplayMetrics.DENSITY_280:
                iconSize = 72;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_360:
            case DisplayMetrics.DENSITY_400:
            case DisplayMetrics.DENSITY_420:
                iconSize = 96;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                iconSize = 144;
                break;
            case DisplayMetrics.DENSITY_560:
                iconSize = 168;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                iconSize = 192;
                break;
        }

        setContentView(R.layout.activity_main);

        if (ZarjaProperties.isLeftHanded())
        {
            parkPos = new Point(PARK_LEFT_X_POS, PARK_LEFT_Y_POS);
            COORD = COORD_LEFT;

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.bgd_left);

            ImageView sonarView = (ImageView) findViewById(R.id.sonarView);
            sonarView.setBaselineAlignBottom(true);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sonarView.getLayoutParams();
            params.alignWithParent = true;
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.ALIGN_BOTTOM);
            sonarView.setLayoutParams(params);
        }
        else
        {
            ImageView sonarView = (ImageView) findViewById(R.id.sonarView);
            sonarView.setBaselineAlignBottom(true);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sonarView.getLayoutParams();
            params.alignWithParent = true;
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.addRule(RelativeLayout.ALIGN_BOTTOM);
            sonarView.setLayoutParams(params);
        }

        init();
    }


    /**
     * Initializer
     */
    private void init()
    {
        initPositionIcons();

        final TextView txtView = (TextView) findViewById(R.id.textView);
        txtView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String msg = "ZARJA\nSingle Hand App Launcher\nv1.0.2\n\n" +
                        "(c)2016, Pronic Apps\n\nTutorial:\n" +
                        "1. Tap and hold on the finger icon.\n" +
                        "2. App icons will start to move toward your finger.\n" +
                        "3. When the wanted app icon is close enough, move your finger toward the selected icon.\n" +
                        "4. When your finger is on the icon, release the finger from the screen to start the app.\n" +
                        "5. By releasing the finger, the icons will reset their positions.\n\n" +
                        "NOTE: Zarja is ordering the apps according to their usage.";

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("About");
                alertDialogBuilder
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Close",new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        final TextView settingsView = (TextView) findViewById(R.id.settingsView);
        settingsView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        final ImageView imgView = (ImageView) findViewById(R.id.sonarView);
        imgView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                RelativeLayout relLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

                float x = -1;
                float y = -1;
                float viewX = -1;
                float viewY = -1;
                float viewW = -1;
                float viewH = -1;

                switch (event.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();

                        viewX = v.getX();
                        viewY = v.getY();

                        posX = viewX + x;
                        posY = viewY + y;

                        pressed = true;
                        away = false;

                        if (ZarjaProperties.isHaptic())
                        {
                            vibrator.vibrate(100);
                        }

                        v.animate().alpha(0.3f).setDuration(300).start();

                        if (!iconMoving)
                        {
                            iconMoving = true;
                            startMoving();
                        }

//                        Logger.getLogger("Z").info("DOWN X=" + posX + " Y=" + posY + " P=" + pressed + " A=" + away);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        pressed = false;
                        away = false;
                        iconMoving = false;
                        resetPositions();
                        v.animate().alpha(1f).setDuration(300).start();

//                        Logger.getLogger("Z").info("CANCELLED");
                        break;

                    case MotionEvent.ACTION_UP:
                        x = event.getX();
                        y = event.getY();

                        viewX = v.getX();
                        viewY = v.getY();

                        viewW = v.getWidth();
                        viewH = v.getHeight();

                        posX = viewX + x;
                        posY = viewY + y;

                        if (away)
                        {
                            away = false;

                            for (int i = pointer; i < appList.size(); i++)
                            {
                                if (i >= MAX + pointer)
                                {
                                    break;
                                }

                                AppLaunchInfo info = appList.get(i);
                                if (posX >= info.getIconView().getX() && posX < info.getIconView().getX() + info.getIconView().getWidth() && posY >= info.getIconView().getY() && posY < info.getIconView().getY() + info.getIconView().getHeight())
                                {
                                    info.getIconView().setAlpha(1.0f);
                                    info.getIconView().setBorder(false);

                                    if (ZarjaProperties.isSound())
                                    {
                                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                                        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                                    }

                                    final Intent _intent = appList.get(i).getIntent();
                                    startActivity(_intent);
                                    AI.getInstance().setAppUsage(appList.get(i).getPackageName());
                                    info.setCount(AI.getInstance().getCount(info.getPackageName()));
                                    Collections.sort(appList);
                                    resetPositions();
                                    break;
                                }
                            }

                        }
                        else
                        {
                        }

                        v.animate().alpha(1.0f).setDuration(300).start();

                        pressed= false;
                        iconMoving = false;

//                        Logger.getLogger("Z").info("UP");
                        break;

                    case MotionEvent.ACTION_MOVE:
                        x = event.getX();
                        y = event.getY();

                        viewX = v.getX();
                        viewY = v.getY();

                        viewW = v.getWidth();
                        viewH = v.getHeight();

                        posX = viewX + x;
                        posY = viewY + y;

                        if (pressed)
                        {
                            if (x >= 0 && x < viewW && y >= 0 && y < viewH)
                            {
                                if (away)
                                {
                                    away = false;

                                    if (!iconMoving)
                                    {
                                        iconMoving = true;
                                        startMoving();
                                    }
                                }
                            }
                            else
                            {
                                if (!away)
                                {
                                    away = true;
                                }

                                for (int i = pointer; i < appList.size(); i++)
                                {
                                    if (i >= MAX + pointer)
                                    {
                                        break;
                                    }

                                    IconView icon = appList.get(i).getIconView();

                                    if (posX >= icon.getX() && posX < icon.getX() + icon.getWidth() && posY >= icon.getY() && posY < icon.getY() + icon.getHeight())
                                    {
                                        icon.setAlpha(0.3f);
                                        ((IconView)icon).setBorder(true);
                                    }
                                    else
                                    {
                                        iconMoving = false;
                                        icon.setAlpha(1.0f);
                                        ((IconView)icon).setBorder(false);
                                    }
                                }
                            }
                        }

 //                       Logger.getLogger("Z").info("MOVE X=" + posX + " Y=" + posY + " P=" + pressed + " A=" + away);
                        break;

                    default:
                        break;
                }

                return true;
            }
        });
    }


    /**
     * Method starts moving icons
     */
    private void startMoving()
    {
        if (pointer == appList.size() - 1)
        {
            iconMoving = false;
            resetPositions();
            return;
        }

        setPosition(parkPos, appList.get(pointer), appList.get(pointer).getIconView());
        pointer++;

        for (int i = pointer; i < pointer + MAX; i++)
        {
            if (i >= appList.size())
            {
                break;
            }

            if (i == pointer)
            {
                animatePosition(COORD[i - pointer], appList.get(i), appList.get(i).getIconView(), new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (iconMoving)
                        {
                            startMoving();
                        }
                        else
                        {
                            if (!pressed)
                            {
                                resetPositions();
                            }
                        }
                    }
                });
            }
            else
            {
                animatePosition(COORD[i - pointer], appList.get(i), appList.get(i).getIconView());
            }
        }
    }


    /**
     * Initial positioning of icons
     */
    private void initPositionIcons()
    {
        RelativeLayout relLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        appList = appMgr.getApps();
//        AI.getInstance().re

        for (int i = 0; i < appList.size(); i++)
        {
            final AppLaunchInfo info = appList.get(i);

            IconView img = new IconView(getApplicationContext(), info.getIcon(), info.getName());
//            ImageView img = new ImageView(getApplicationContext());
//            img.setImageDrawable(info.getIcon());

            final Intent intent = info.getIntent();

            img.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    switch (event.getAction() & MotionEvent.ACTION_MASK)
                    {
                        case MotionEvent.ACTION_DOWN:
                            v.animate().alpha(0.3f).setDuration(100).start();
                            break;

                        case MotionEvent.ACTION_CANCEL:
                            v.animate().alpha(1f).setDuration(100).start();
                            break;

                        case MotionEvent.ACTION_UP:
                            v.animate().alpha(1f).setDuration(100).start();
                            if (event.getX() >= 0 && event.getX() < v.getWidth() && event.getY() >= 0 && event.getY() < v.getHeight())
                            {
                                if (ZarjaProperties.isSound())
                                {
                                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                                }

                                startActivity(intent);
                                AI.getInstance().setAppUsage(info.getPackageName());
                                info.setCount(AI.getInstance().getCount(info.getPackageName()));
                                Collections.sort(appList);
                                resetPositions();
                            }
                            break;

                        default:
                            break;
                    }

                    return true;
                }
            });

//            Logger.getLogger("Z").info("ICON W=" + info.getIcon().getIntrinsicWidth() + " H=" + info.getIcon().getIntrinsicHeight());
            info.setWidth(info.getIcon().getIntrinsicWidth());
            info.setHeight(info.getIcon().getIntrinsicHeight());
            img.setImgWidth(info.getIcon().getIntrinsicWidth());
            img.setImgHeight(info.getIcon().getIntrinsicHeight());
            img.setScaleX((float) iconSize / info.getWidth());
            img.setScaleY((float) iconSize / info.getHeight());

            if (i >= MAX)
            {
                setPosition(parkPos, info, img);
            }
            else
            {
                setPosition(COORD[i], info, img);
            }

            relLayout.addView(img);
            info.setIconView(img);
        }
    }


    /**
     * Method resets icon positions
     */
    private void resetPositions()
    {
        pointer = 0;

        for (int i = 0; i < appList.size(); i++)
        {
            if (ZarjaProperties.isHaptic())
            {
                vibrator.vibrate(100);
            }

            IconView img = appList.get(i).getIconView();

            if (i < appList.size() - 1)
            {
                img.animate().alpha(0.0f).setDuration(DELAY[ZarjaProperties.getSpeed()]).start();
            }
            else
            {
                img.animate().alpha(0.0f).setDuration(DELAY[ZarjaProperties.getSpeed()]).withEndAction(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < appList.size(); i++)
                        {
                            AppLaunchInfo info = appList.get(i);
                            IconView img = appList.get(i).getIconView();

                            if (i >= MAX)
                            {
                                setPosition(parkPos, info, img);
                            }
                            else
                            {
                                setPosition(COORD[i], info, img);
                            }

                            img.setAlpha(1.0f);
                        }
                    }
                }).start();
            }
        }
    }


    /**
     * Icon position setter
     * @param point Point
     * @param info App info
     * @param img Image
     */
    private void setPosition(Point point, AppLaunchInfo info, IconView img)
    {
        int iconXOff = (colPart - info.getWidth()) / 2;
        int iconYOff = (rowPart - info.getHeight()) / 2;

        int curCol = point.x;
        int curRow = point.y;

        info.setX(curCol * colPart + iconXOff);
        info.setY(curRow * rowPart + iconYOff);
        img.setX(info.getX());
        img.setY(info.getY());
    }


    /**
     * Animated icon position setter
     * @param point Point
     * @param info App info
     * @param img Image
     */
    private void animatePosition(Point point, AppLaunchInfo info, IconView img)
    {
        int iconXOff = (colPart - info.getWidth()) / 2;
        int iconYOff = (rowPart - info.getHeight()) / 2;

        int curCol = point.x;
        int curRow = point.y;

        info.setX(curCol * colPart + iconXOff);
        info.setY(curRow * rowPart + iconYOff);
        img.animate().x(info.getX()).setDuration(DELAY[ZarjaProperties.getSpeed()]).start();
        img.animate().y(info.getY()).setDuration(DELAY[ZarjaProperties.getSpeed()]).start();
    }


    /**
     * Animated icon position setter
     * @param point Point
     * @param info App info
     * @param img Image
     * @param runnable Executable code run at the end of animation
     */
    private void animatePosition(Point point, AppLaunchInfo info, IconView img, Runnable runnable)
    {
        int iconXOff = (colPart - info.getWidth()) / 2;
        int iconYOff = (rowPart - info.getHeight()) / 2;

        int curCol = point.x;
        int curRow = point.y;

        info.setX(curCol * colPart + iconXOff);
        info.setY(curRow * rowPart + iconYOff);
        img.animate().x(info.getX()).setDuration(DELAY[ZarjaProperties.getSpeed()]).start();
        img.animate().y(info.getY()).setDuration(DELAY[ZarjaProperties.getSpeed()]).withEndAction(runnable).start();
    }


    /**
     * Getter for app context
     * @return Context
     */
    public static Context getContext()
    {
        return context;
    }


    /**
     * Icon positions for right hand usage
     */
    public static final Point COORD_RIGHT[] =
            {
                    new Point(2, 4),
                    new Point(1, 4),
                    new Point(0, 4),

                    new Point(0, 3),
                    new Point(1, 3),
                    new Point(2, 3),
                    new Point(3, 3),

                    new Point(3, 2),
                    new Point(2, 2),
                    new Point(1, 2),
                    new Point(0, 2),

                    new Point(0, 1),
                    new Point(1, 1),
                    new Point(2, 1),
                    new Point(3, 1),

                    new Point(3, 0),
                    new Point(2, 0),
                    new Point(1, 0),
                    new Point(0, 0)
            };


    /**
     * Icon positions for left hand usage
     */
    public static final Point COORD_LEFT[] =
            {
                    new Point(1, 4),
                    new Point(2, 4),
                    new Point(3, 4),

                    new Point(3, 3),
                    new Point(2, 3),
                    new Point(1, 3),
                    new Point(0, 3),

                    new Point(0, 2),
                    new Point(1, 2),
                    new Point(2, 2),
                    new Point(3, 2),

                    new Point(3, 1),
                    new Point(2, 1),
                    new Point(1, 1),
                    new Point(0, 1),

                    new Point(0, 0),
                    new Point(1, 0),
                    new Point(2, 0),
                    new Point(3, 0)
            };

}
