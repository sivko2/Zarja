package si.pronic.zarja;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Icon view component with image and text below the image
 */
public class IconView extends LinearLayout
{
    // Image
    private ImageView imgView;

    // Text
    private TextView txtView;


    /**
     * Constructor
     * @param context App context
     * @param icon Icon
     * @param text Text
     */
    public IconView(Context context, Drawable icon, String text)
    {
        super(context);
        setOrientation(VERTICAL);

        imgView = new ImageView(context);
        imgView.setImageDrawable(icon);
        addView(imgView);

        txtView = new TextView(context);
        txtView.setTextColor(Color.BLACK);
        txtView.setText(text);
        txtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f);
        txtView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        txtView.setMaxWidth(icon.getIntrinsicWidth());
        addView(txtView);

    }


    /**
     * Propagating on touch event listener
     * @param listener Listener
     */
    public void setOnTouchListener(OnTouchListener listener)
    {
        imgView.setOnTouchListener(listener);
    }


    /**
     * Method sets border
     * @param flag True if YES
     */
    public void setBorder(boolean flag)
    {
        if (flag)
        {
            imgView.setBackgroundColor(Color.LTGRAY);
        }
        else
        {
            imgView.setBackgroundColor(0);
        }
    }


    // Setters and getters

    public void setImgWidth(int width)
    {
        imgView.setMaxWidth(width);
        imgView.setMinimumWidth(width);
    }


    public int getImgWidth()
    {
        return imgView.getWidth();
    }


    public void setImgHeight(int height)
    {
        imgView.setMaxHeight(height);
        imgView.setMinimumHeight(height);
    }


    public int getImgHeight()
    {
        return imgView.getHeight();
    }


    public void setScaleX(float factor)
    {
        imgView.setScaleX(factor);
    }


    public void setScaleY(float factor)
    {
        imgView.setScaleY(factor);
    }

}
