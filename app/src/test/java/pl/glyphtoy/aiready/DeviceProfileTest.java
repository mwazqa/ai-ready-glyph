package pl.glyphtoy.aiready;

import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeviceProfileTest {
    @Test public void phone3CodeAndMarketingAliasesSelectTheMatrixTarget() {
        for (String model : new String[]{"A024", " a024 ", "Phone (3)", "Nothing Phone (3)", "Nothing Phone 3"}) {
            DeviceProfile device = DeviceProfile.detect("Nothing", "Nothing", model);
            assertEquals("Nothing Phone (3)", device.displayName);
            assertTrue(device.nothing);
            assertTrue(device.supportsAnimations());
            assertEquals(25, device.matrixLength);
            assertEquals("A024", device.matrixTarget());
        }
    }

    @Test public void recognizesOlderModelsAndIndianPhone2WithoutSelectingPhone3() {
        String[][] models = {{"A063", "Phone (1)"}, {"A065", "Phone (2)"}, {"AIN065", "Phone (2)"},
                {"A142", "Phone (2a)"}, {"A142P", "Phone (2a) Plus"}, {"A059", "Phone (3a)"},
                {"A059P", "Phone (3a) Pro"}, {"A069", "Phone (4a)"}};
        for (String[] item : models) {
            DeviceProfile device = DeviceProfile.detect("Nothing", "Nothing", item[0]);
            assertEquals("Nothing " + item[1], device.displayName);
            assertFalse(device.hasGlyphMatrix());
            assertFalse(device.supportsAnimations());
            assertNull(device.matrixTarget());
        }
    }

    @Test public void smallerMatrixIsRecognizedWithoutClaimingArtworkCompatibility() {
        DeviceProfile device = DeviceProfile.detect("Nothing", "Nothing", "A069P");
        assertEquals("Nothing Phone (4a) Pro", device.displayName);
        assertTrue(device.hasGlyphMatrix());
        assertEquals(13, device.matrixLength);
        assertFalse(device.supportsAnimations());
        assertNull(device.matrixTarget());
    }

    @Test public void brandOrFullManufacturerCanEstablishNothingIdentity() {
        assertTrue(DeviceProfile.detect("Nothing Technology Limited", "", "A024").supportsAnimations());
        assertTrue(DeviceProfile.detect("", "nothing", "A024").supportsAnimations());
    }

    @Test public void foreignManufacturersAndLookalikeModelCodesAreRejected() {
        for (String manufacturer : new String[]{"Samsung", "NotNothing", "NothingElse", "", null})
            assertFalse(DeviceProfile.detect(manufacturer, manufacturer, "A024").supportsAnimations());
        for (String model : new String[]{"A024P", "A024-clone", "A0240", "Not Phone (3)", "Phone (3a) Pro"})
            assertFalse(DeviceProfile.detect("Nothing", "Nothing", model).supportsAnimations());
        assertFalse(DeviceProfile.detect("Nothing", "CMF", "A024").supportsAnimations());
    }

    @Test public void unknownAndMissingBuildIdentityUseSafeFallbacks() {
        DeviceProfile future = DeviceProfile.detect("Nothing", "Nothing", "A999");
        assertTrue(future.nothing);
        assertEquals("Nothing A999", future.displayName);
        assertFalse(future.supportsAnimations());
        assertEquals("Android device", DeviceProfile.detect(null, null, null).displayName);
        assertEquals("Android device", DeviceProfile.detect("unknown", "unknown", "unknown").displayName);
        assertEquals("Nothing device", DeviceProfile.detect("Nothing", "Nothing", "").displayName);
        assertEquals("Google Pixel 9", DeviceProfile.detect("Google", "google", "Pixel 9").displayName);
    }

    @Test public void detectionDoesNotDependOnTheUserLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(DeviceProfile.detect("nothing", "nothing", "nothing phone (3)").supportsAnimations());
        } finally { Locale.setDefault(original); }
    }
}
