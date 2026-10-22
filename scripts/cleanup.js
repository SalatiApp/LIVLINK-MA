// كيمسح كل إعلان فات عليه وقت الانتهاء (expiresAt) من Firestore
const admin = require("firebase-admin");

const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);
admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });
const db = admin.firestore();

async function main() {
  const now = admin.firestore.Timestamp.now();
  let total = 0;

  while (true) {
    const snap = await db.collection("ads").where("expiresAt", "<", now).limit(400).get();
    if (snap.empty) break;

    const batch = db.batch();
    snap.docs.forEach((doc) => batch.delete(doc.ref));
    await batch.commit();
    total += snap.size;
  }

  console.log(`Deleted ${total} expired ads.`);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
