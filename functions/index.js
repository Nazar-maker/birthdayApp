const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const { getMessaging } = require("firebase-admin/messaging");

initializeApp();

// Triggers whenever a new ping document is written to Firestore.
// Looks up the recipient's FCM token and sends them a heartbeat notification.
exports.sendHeartbeatNotification = onDocumentCreated("pings/{pingId}", async (event) => {
    const ping = event.data.data();
    const { to, senderName } = ping;

    const recipientDoc = await getFirestore().doc(`users/${to}`).get();
    if (!recipientDoc.exists) {
        console.log(`No user document found for ${to}`);
        return;
    }

    const { fcmToken } = recipientDoc.data();
    if (!fcmToken) {
        console.log(`No FCM token for ${to}`);
        return;
    }

    await getMessaging().send({
        token: fcmToken,
        // data-only message so the app's onMessageReceived always fires,
        // even when the app is in the foreground.
        data: {
            type: "heartbeat",
            senderName: senderName ?? "Someone",
        },
        android: {
            priority: "high",
        },
    });

    console.log(`Heartbeat sent from ${ping.from} to ${to}`);
});
