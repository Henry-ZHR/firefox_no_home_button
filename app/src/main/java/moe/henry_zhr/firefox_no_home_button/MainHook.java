package moe.henry_zhr.firefox_no_home_button;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {
  @Override
  public void handleLoadPackage(LoadPackageParam lpparam) {
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
            final Object listener = XposedHelpers.findField(action.getClass(), "listener").get(action);
            // org.mozilla.fenix.browser.BrowserFragment$initializeUI$homeAction$1
            if (listener.getClass().getName().contains("homeAction")) {
              param.setResult(null);
            }
          }
        }
    );
  }
}