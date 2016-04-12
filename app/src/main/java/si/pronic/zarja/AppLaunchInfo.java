package si.pronic.zarja;


import android.content.Intent;
import android.graphics.drawable.Drawable;


/**
 * App info
 */
public class AppLaunchInfo implements Comparable<AppLaunchInfo>
{
    // App name
    private String name;

    // Package name
    private String packageName;

    // Intent
    private Intent intent;

    // Icon
    private Drawable icon;

    // Image component
    private IconView iconView;

    // Counter
    private int count = 0;

    // Posotion and size
    private int x = -1;
    private int y = -1;
    private int width = -1;
    private int height = -1;


    /**
     * Constructor
     */
    public AppLaunchInfo()
    {
    }


    /**
     * Method used for comparator
     * @param f Object to compare
     * @return Value
     */
    @Override
    public int compareTo(AppLaunchInfo f)
    {
        if (count < f.count)
        {
            return 1;
        }
        else if (count > f.count)
        {
            return -1;
        }
        else
        {
            return name.compareToIgnoreCase(f.name);
        }
    }


    // Setters and getters

    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public String getPackageName()
    {
        return packageName;
    }


    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }


    public Intent getIntent()
    {
        return intent;
    }


    public void setIntent(Intent intent)
    {
        this.intent = intent;
    }


    public Drawable getIcon()
    {
        return icon;
    }


    public void setIcon(Drawable icon)
    {
        this.icon = icon;
    }


    public int getX()
    {
        return x;
    }


    public void setX(int x)
    {
        this.x = x;
    }


    public int getY()
    {
        return y;
    }


    public void setY(int y)
    {
        this.y = y;
    }


    public int getWidth()
    {
        return width;
    }


    public void setWidth(int width)
    {
        this.width = width;
    }


    public int getHeight()
    {
        return height;
    }


    public void setHeight(int height)
    {
        this.height = height;
    }


    public int getCount()
    {
        return count;
    }


    public void setCount(int count)
    {
        this.count = count;
    }


    public IconView getIconView()
    {
        return iconView;
    }


    public void setIconView(IconView iconView)
    {
        this.iconView = iconView;
    }


}
