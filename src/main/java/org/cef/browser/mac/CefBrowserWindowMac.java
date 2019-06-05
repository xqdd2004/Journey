package org.cef.browser.mac;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.cef.browser.CefBrowserWindow;

import java.awt.*;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CefBrowserWindowMac implements CefBrowserWindow {

    private static long[] result;

    @Override
    @SuppressWarnings("unchecked")
    public long getWindowHandle(Component comp) {
        System.out.println("got to getWindowHandle: " + comp);
        result = new long[1];
        while (comp != null) {
            if (comp.isLightweight()) {
                comp = comp.getParent();
                continue;
            }
            @SuppressWarnings("deprecation")
            ComponentPeer peer = comp.getPeer();

            Class LWComponentPeer;
            Class CPlatformWindow;
            Class CFNativeAction;
            Method execute;
            Method getPlatformWindow;
            try {
                LWComponentPeer = Class.forName("sun.lwawt.LWComponentPeer");
                CPlatformWindow = Class.forName("sun.lwawt.macosx.CPlatformWindow");
                CFNativeAction = Class.forName("sun.lwawt.macosx.CFRetainedResource.CFNativeAction");
                execute = CPlatformWindow.getMethod("execute", CFNativeAction);
                getPlatformWindow = LWComponentPeer.getMethod("getPlatformWindow");
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }

            if (LWComponentPeer.isInstance(peer)) {
                Object pWindow;
                try {
                    pWindow = getPlatformWindow.invoke(peer);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
                if (CPlatformWindow.isInstance(pWindow)) {
                    try {
                        DynamicType.Unloaded nativeActionRun = new ByteBuddy()
                                .subclass(CFNativeAction)
                                .method(ElementMatchers.named("run"))
                                .intercept(MethodDelegation.to(CefBrowserWindowMac.class))
                                .make();
                        execute.invoke(pWindow, nativeActionRun.load(getClass().getClassLoader())
                                .getLoaded().newInstance());
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
            comp = comp.getParent();
        }
        System.out.println("did result: " + result[0]);
        return result[0];
    }

    static void run(long l) {
        System.out.println("Ran run: " + l);
        result[0] = l;
    }
}
