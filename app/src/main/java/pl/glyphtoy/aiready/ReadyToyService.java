package pl.glyphtoy.aiready;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import com.nothing.ketchum.GlyphToy;

public final class ReadyToyService extends Service {
    private final Messenger messenger = new Messenger(new Handler(Looper.getMainLooper()) {
        @Override public void handleMessage(Message message) {
            if (message.what == GlyphToy.MSG_GLYPH_TOY) {
                String event = message.getData().getString(GlyphToy.MSG_GLYPH_TOY_DATA);
                if (GlyphToy.EVENT_CHANGE.equals(event)) MatrixController.get(ReadyToyService.this).play();
            } else super.handleMessage(message);
        }
    });
    @Override public IBinder onBind(Intent intent) {
        MatrixController.get(this).attachToy();
        return messenger.getBinder();
    }
    @Override public boolean onUnbind(Intent intent) {
        MatrixController.get(this).detachToy();
        return false;
    }
    @Override public void onDestroy() {
        MatrixController.get(this).detachToy();
        super.onDestroy();
    }
}
