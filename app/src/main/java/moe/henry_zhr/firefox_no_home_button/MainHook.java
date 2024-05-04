package moe.henry_zhr.firefox_no_home_button;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {
  private static final String[] HOME_DRAWABLE_NAMES = {
      "mozac_ic_home",
      "mozac_ic_home_24"
  };

  @Override
  public void handleLoadPackage(LoadPackageParam lpparam) {
    final Set<Drawable> drawables = Collections.synchronizedSet(
        Collections.newSetFromMap(new WeakHashMap<>())
    );

    XposedHelpers.findAndHookMethod(
        "androidx.appcompat.content.res.AppCompatResources",
        lpparam.classLoader,
        "getDrawable",
        Context.class,
        int.class,
        new XC_MethodHook() {
          @Override
          protected void afterHookedMethod(MethodHookParam param) {
            final Context context = (Context) param.args[0];
            final String name = context.getResources().getResourceEntryName((int) param.args[1]);
            for (String item : HOME_DRAWABLE_NAMES) {
              if (name.equals(item)) {
                drawables.add((Drawable) param.getResult());
                break;
              }
            }
          }
        }
    );
    XposedBridge.hookAllMethods(
        XposedHelpers.findClass(
            "mozilla.components.browser.toolbar.BrowserToolbar",
            lpparam.classLoader
        ),
        "addNavigationAction",
        new XC_MethodHook() {
          @Override
          protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            final Object action = param.args[0];
            final Field field = XposedHelpers.findField(action.getClass(), "imageDrawable");
            if (drawables.contains((Drawable) field.get(action))) {
              param.setResult(null);
            }
          }
        }
    );
  }
}
