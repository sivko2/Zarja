package si.pronic.zarja;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


/**
 * Application manager
 */
public class ApplicationManager
{
    // App context
    private Context context;


    /**
     * Constructor
     * @param context App context
     */
    public ApplicationManager(Context context)
    {
        this.context = context;
    }


    /**
     * Method returns list of ainstalled apps
     * @return List
     */
    public List<AppLaunchInfo> getApps()
    {
        List<AppLaunchInfo> retList = new ArrayList<>();

        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages)
        {
            if(pm.getLaunchIntentForPackage(packageInfo.packageName)!= null && !pm.getLaunchIntentForPackage(packageInfo.packageName).equals("") && !packageInfo.packageName.equals("si.pronic.zarja"))
            {
                AppLaunchInfo info = new AppLaunchInfo();
                int count = AI.getInstance().getCount(packageInfo.packageName);
                if (count == -1)
                {
                    AI.getInstance().setCount(packageInfo.packageName, 0);
                    count = AI.getInstance().getCount(packageInfo.packageName);
                }

                info.setCount(count);
                info.setName(new StringBuilder(pm.getApplicationLabel(packageInfo)).toString());
                info.setPackageName(packageInfo.packageName);
                info.setIntent(pm.getLaunchIntentForPackage(packageInfo.packageName));
                try
                {
                    info.setIcon(pm.getApplicationIcon(packageInfo.packageName));
                }
                catch (PackageManager.NameNotFoundException ex)
                {
                    Logger.getLogger("Z").severe("Can't find icon fopr package " + packageInfo.packageName);
                }

                retList.add(info);
//                Logger.getLogger("Z").info("Package Name: " + packageInfo.packageName + " Application Label: " + pm.getApplicationLabel(packageInfo));
            }
        }

        Collections.sort(retList);

        return retList;
    }

}
