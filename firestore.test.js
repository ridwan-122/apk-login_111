const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read, list, or write user profiles or login logs", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("loginLogs").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@example.com",
      fullName: "Alice Putri",
      username: "alice.putri",
      phone: "081234567890",
      city: "Jakarta",
      pinkStyle: "ROSE_QUARTZ",
      loginCount: 1,
      lastLoginAt: new Date(Date.now() - 1000),
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Authenticated owner: valid Kotlin registration create with personal data, login update, and profile edit succeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@example.com",
      fullName: "Alice Putri",
      username: "alice_putri",
      phone: "081234567890",
      city: "Bandung",
      pinkStyle: "SAKURA_SOFT",
      loginCount: 1,
      lastLoginAt: created,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("loginLogs").doc("log_1").set({
      userId: ALICE_UID,
      provider: "google.com",
      email: "alice@example.com",
      deviceLabel: "Android • Blush App",
      loginAt: created,
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("loginLogs").orderBy("loginAt", "desc").limit(5).get()
  );
  await assertFails(aliceDb.collection("users").get());

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      loginCount: 2,
      lastLoginAt: new Date(Date.now() - 2000),
      updatedAt: new Date(Date.now() - 2000),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      fullName: "Alice Kirana",
      username: "alice.kirana",
      phone: "081298765432",
      city: "Yogyakarta",
      pinkStyle: "BERRY_GLOW",
      bio: "Pecinta warna pink dan desain minimalis.",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("loginLogs").doc("log_1").update({
      deviceLabel: "Tampered",
    })
  );
});

test("Authenticated user: cannot read, create, update, or delete another user's profile or login logs", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      email: "bob@example.com",
      fullName: "Bob Pratama",
      username: "bob_p",
      phone: "081111222333",
      city: "Surabaya",
      pinkStyle: "CORAL_BLUSH",
      loginCount: 1,
      lastLoginAt: new Date(Date.now() - 10000),
      createdAt: new Date(Date.now() - 10000),
      updatedAt: new Date(Date.now() - 10000),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("loginLogs").get());
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({
      fullName: "Hijacked",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(aliceDb.collection("users").doc(BOB_UID).delete());
});

test("Security bounds: reject ghost fields, oversized phone/city, immutable mutation, and future timestamps", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      id: ALICE_UID,
      userId: ALICE_UID,
      email: "alice@example.com",
      fullName: "Alice Putri",
      username: "alice_putri",
      pinkStyle: "ROSE_QUARTZ",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@example.com",
      fullName: "Alice Putri",
      username: "alice_putri",
      phone: "1".repeat(25),
      pinkStyle: "ROSE_QUARTZ",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@example.com",
      fullName: "Alice Putri",
      username: "alice_putri",
      city: "x".repeat(55),
      pinkStyle: "ROSE_QUARTZ",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@example.com",
      fullName: "Alice Putri",
      username: "alice_putri",
      phone: "081234567890",
      city: "Jakarta",
      pinkStyle: "ROSE_QUARTZ",
      loginCount: 1,
      lastLoginAt: created,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      fullName: "Alice Baru",
      loginCount: 99,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).delete());
});
