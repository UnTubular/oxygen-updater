package com.arjanvlek.oxygenupdater.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import com.arjanvlek.oxygenupdater.MainActivity;
import com.arjanvlek.oxygenupdater.R;
import com.arjanvlek.oxygenupdater.Support.SettingsManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;
import java.util.Map;

import static android.app.Notification.DEFAULT_ALL;
import static android.app.Notification.PRIORITY_HIGH;
import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.arjanvlek.oxygenupdater.ApplicationContext.LOCALE_DUTCH;
import static com.arjanvlek.oxygenupdater.notifications.NotificationElement.DEVICE_NAME;
import static com.arjanvlek.oxygenupdater.notifications.NotificationElement.DUTCH_MESSAGE;
import static com.arjanvlek.oxygenupdater.notifications.NotificationElement.ENGLISH_MESSAGE;
import static com.arjanvlek.oxygenupdater.notifications.NotificationElement.NEW_DEVICE_NAME;
import static com.arjanvlek.oxygenupdater.notifications.NotificationElement.NEW_VERSION_NUMBER;
import static com.arjanvlek.oxygenupdater.notifications.NotificationElement.TYPE;

public class NotificationService extends FirebaseMessagingService {

    public static final int NEW_DEVICE_NOTIFICATION_ID = 1;
    public static final int NEW_UPDATE_NOTIFICATION_ID = 2;
    public static final int GENERIC_NOTIFICATION_ID = 3;
    public static final int UNKNOWN_NOTIFICATION_ID = 4;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> messageContents = remoteMessage.getData();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        SettingsManager settingsManager = new SettingsManager(getApplicationContext());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationType notificationType = NotificationType.valueOf(messageContents.get(TYPE.toString()));

        Notification.Builder builder = null;

        switch(notificationType) {
            case NEW_DEVICE:
                if(!settingsManager.receiveNewDeviceNotifications()) {
                    return;
                }
                builder = displayNewDeviceNotification(messageContents.get(NEW_DEVICE_NAME.toString()));
                break;
            case NEW_VERSION:
                if(!settingsManager.receiveSystemUpdateNotifications()) {
                    return;
                }
                builder = displayNewVersionNotification(messageContents.get(DEVICE_NAME.toString()), messageContents.get(NEW_VERSION_NUMBER.toString()));
                break;
            case GENERIC_NOTIFICATION:
                if(!settingsManager.receiveGenericNotifications()) {
                    return;
                }
                String appLocale = Locale.getDefault().getDisplayLanguage();
                String message;
                switch(appLocale) {
                    case LOCALE_DUTCH:
                        message = messageContents.get(DUTCH_MESSAGE.toString());
                        break;
                    default:
                        message = messageContents.get(ENGLISH_MESSAGE.toString());
                        break;
                }
                builder = displayGenericServerNotification(message);
        }
        if(builder == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            builder.setVisibility(VISIBILITY_PUBLIC);
            builder.setPriority(PRIORITY_HIGH);
        }

        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentIntent(contentIntent);
        builder.setDefaults(DEFAULT_ALL);
        builder.setAutoCancel(true);

        notificationManager.notify(getNotificationId(notificationType), builder.build());
    }

    private Notification.Builder displayGenericServerNotification(String message) {
       return new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_notification_general)
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(message))
                        .setContentText(message);
    }

    private Notification.Builder displayNewDeviceNotification(String newDeviceName) {
        // TODO make one string of this.
        String message = getString(R.string.notification_new_device) + " " + newDeviceName + " " + getString(R.string.notification_new_device_2);

        return new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_notification_new_phone)
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(message)
                                .setSummaryText(getString(R.string.notification_new_device_short)))
                        .setContentText(message);
    }

    private Notification.Builder displayNewVersionNotification(String deviceName, String versionNumber) {
        // TODO make one string of this.
        String message = getString(R.string.notification_version) + " " + versionNumber + " " + getString(R.string.notification_is_now_available) + " " + deviceName + "!";
        return new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_notification_update)
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(message)
                                .setSummaryText(getString(R.string.notification_update_short)))
                        .setContentText(message);

    }

    private int getNotificationId(NotificationType type) {
        switch(type) {
            case NEW_DEVICE:
                return NEW_DEVICE_NOTIFICATION_ID;
            case NEW_VERSION:
                return NEW_UPDATE_NOTIFICATION_ID;
            case GENERIC_NOTIFICATION:
                return GENERIC_NOTIFICATION_ID;
            default:
                return UNKNOWN_NOTIFICATION_ID;
        }
    }
}