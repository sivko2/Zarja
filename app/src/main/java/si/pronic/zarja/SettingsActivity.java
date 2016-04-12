package si.pronic.zarja;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * Activity for settings screen
 */
public class SettingsActivity extends AppCompatActivity
{

    /**
     * Creates UI
     * @param savedInstanceState State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AI.getInstance().resetCounters();
            }
        });

        final CheckBox leftHandedBox = (CheckBox) findViewById(R.id.leftHandedBox);
        leftHandedBox.setChecked(ZarjaProperties.isLeftHanded());
        leftHandedBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ZarjaProperties.setLeftHanded(isChecked);
                ZarjaProperties.save();
            }
        });

        final CheckBox hapticBox = (CheckBox) findViewById(R.id.hapticBox);
        hapticBox.setChecked(ZarjaProperties.isHaptic());
        hapticBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ZarjaProperties.setHaptic(isChecked);
                ZarjaProperties.save();
            }
        });

        final CheckBox soundBox = (CheckBox) findViewById(R.id.soundBox);
        soundBox.setChecked(ZarjaProperties.isSound());
        soundBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ZarjaProperties.setSound(isChecked);
                ZarjaProperties.save();
            }
        });

        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.incrementProgressBy(1);
        seekBar.setProgress(ZarjaProperties.getSpeed());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    seekBar.setProgress(progress);
                    ZarjaProperties.setSpeed(progress);
                    ZarjaProperties.save();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

        TextView slowView = (TextView) findViewById(R.id.slowView);
        slowView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                seekBar.setProgress(0);
                ZarjaProperties.setSpeed(0);
                ZarjaProperties.save();
            }
        });

        TextView mediumView = (TextView) findViewById(R.id.mediumView);
        mediumView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                seekBar.setProgress(1);
                ZarjaProperties.setSpeed(1);
                ZarjaProperties.save();
            }
        });

        TextView fastView = (TextView) findViewById(R.id.fastView);
        fastView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                seekBar.setProgress(2);
                ZarjaProperties.setSpeed(2);
                ZarjaProperties.save();
            }
        });
    }
}
