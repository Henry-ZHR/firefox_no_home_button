package moe.henry_zhr.firefox_no_home_button;

import androidx.annotation.Keep;

import java.util.HashSet;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

@Keep
public class MainHook implements IXposedHookLoadPackage {
  @Override
  public void handleLoadPackage(LoadPackageParam lpparam) {
    final HashSet<Integer> set = new HashSet<>();

    XposedHelpers.findAndHookMethod(
        "mozilla.components.browser.toolbar.BrowserToolbar",
        lpparam.classLoader,
        "addNavigationAction",
        "mozilla.components.concept.toolbar.Toolbar.Action",
        new XC_MethodHook() {
          @Override
          protected void beforeHookedMethod(MethodHookParam param) {
            // home button should be the first action added, so we just ignore the first action
            final int id = System.identityHashCode(param.thisObject);
            if (!set.contains(id)) {
              set.add(id);
              param.setResult(null);
            }
          }
        }
    );
  }
}