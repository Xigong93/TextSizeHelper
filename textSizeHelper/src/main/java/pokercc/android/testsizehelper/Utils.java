package pokercc.android.testsizehelper;


abstract class Utils {
    public static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }
}
