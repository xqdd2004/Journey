package com.codebrig.journey.proxy.misc;

import org.joor.Reflect;

/**
 * Wrapper to expose the fields of captured JCEF specific KeyEvent (CefKeyEvent),
 * see <a href="https://bitbucket.org/chromiumembedded/java-cef/src/master/java/org/cef/handler/CefKeyboardHandler.java">CefKeyboardHandler</a>
 * <p>
 * Javadoc taken from https://bitbucket.org/chromiumembedded/java-cef
 *
 * @author <a href="mailto:matyas.mazzag@gmail.com">Matyas Mazzag</a>
 */
public class CefKeyEventWrapper {

    public enum EventType {
        /**
         * Notification that a key transitioned from "up" to "down"
         */
        KEYEVENT_RAWKEYDOWN,

        /**
         * Notification that a key was pressed. This does not necessarily
         * correspond to a character depending on the key and language. Use
         * KEYEVENT_CHAR for character input.
         */
        KEYEVENT_KEYDOWN,

        /**
         * Notification that a key was released
         */
        KEYEVENT_KEYUP,

        /**
         * Notification that a character was typed. Use this for text input. Key
         * down events may generate 0, 1, or more than one character event
         * depending on the key, locale, and operating system.
         */
        KEYEVENT_CHAR
    }

    private final Reflect jcefNativeKeyEvent;

    public CefKeyEventWrapper(Object jcefNativeKeyEvent) {
        this.jcefNativeKeyEvent = Reflect.on(jcefNativeKeyEvent);
    }

    /**
     * The type of keyboard event.
     */
    public EventType getType() {
        return EventType.valueOf(jcefNativeKeyEvent.get("type").toString());
    }

    /**
     * Bit flags describing any pressed modifier keys.
     *
     * @see org.cef.handler.CefContextMenuHandler.EventFlags for values.
     */
    public int getModifiers() {
        return jcefNativeKeyEvent.get("modifiers");
    }

    /**
     * The Windows key code for the key event. This value is used by the DOM
     * specification. Sometimes it comes directly from the event (i.e. on
     * Windows) and sometimes it's determined using a mapping function. See
     * WebCore/platform/chromium/KeyboardCodes.h for the list of values.
     */
    public int getWindowsKeyCode() {
        return jcefNativeKeyEvent.get("windows_key_code");
    }

    /**
     * The actual key code generated by the platform.
     */
    public int getNativeKeyCode() {
        return jcefNativeKeyEvent.get("native_key_code");
    }

    /**
     * Indicates whether the event is considered a "system key" event (see
     * http://msdn.microsoft.com/en-us/library/ms646286(VS.85).aspx for details).
     * This value will always be false on non-Windows platforms.
     */
    public boolean isSystemKey() {
        return jcefNativeKeyEvent.get("is_system_key");
    }

    /**
     * The character generated by the keystroke.
     */
    public char getCharacter() {
        return jcefNativeKeyEvent.get("character");
    }

    /**
     * Same as character but unmodified by any concurrently-held modifiers
     * (except shift). This is useful for working out shortcut keys.
     **/
    public char getUnmodifiedCharacter() {
        return jcefNativeKeyEvent.get("unmodified_character");
    }

    /**
     * True if the focus is currently on an editable field on the page. This is
     * useful for determining if standard key events should be intercepted.
     */
    public boolean isFocusOnEditableField() {
        return jcefNativeKeyEvent.get("focus_on_editable_field");
    }
}