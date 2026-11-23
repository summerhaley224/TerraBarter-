const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");
const serverTimestamp = () => new Date();

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

test("Unauthenticated user: cannot read or write users or skill_offerings", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("skill_offerings").where("isPublic", "==", true).get());
  await assertFails(unauthDb.collection("trades").where("participantIds", "array-contains", ALICE_UID).get());
});

test("Authenticated user: can create, read, and update own user profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      handle: "@alice_earth",
      displayName: "Alice Meadow",
      bioRegion: "Cascadia",
      skills: ["Organic Gardening"],
      barterItems: ["Heirloom Seeds"],
      wishlist: ["55-Gal Barrel"],
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      skills: ["Organic Gardening", "Solar Repair"],
      updatedAt: serverTimestamp(),
    })
  );
});

test("Authenticated user: cannot read or write another user's profile, or inject ghost field", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      handle: "@bob_solar",
      displayName: "Bob Vance",
      skills: [],
      barterItems: [],
      wishlist: [],
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      handle: "@alice",
      displayName: "Alice",
      skills: [],
      barterItems: [],
      wishlist: [],
      isVerified: true, // shadow/ghost field
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );
});

test("Skill offerings: unfiltered list fails, public query succeeds, cross-user update fails", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("skill_offerings").doc("skill_alice_1").set({
      userId: ALICE_UID,
      authorName: "Alice Meadow",
      authorHandle: "@alice",
      title: "Heirloom Produce",
      category: "FARMING_PRODUCE",
      isPureBarter: true,
      isPublic: true,
      tags: ["Organic"],
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );

  // Unfiltered list must fail (rules are not filters)
  await assertFails(bobDb.collection("skill_offerings").get());

  // Aligned filtered list succeeds
  await assertSucceeds(bobDb.collection("skill_offerings").where("isPublic", "==", true).get());

  // Cross-user update fails
  await assertFails(
    bobDb.collection("skill_offerings").doc("skill_alice_1").update({
      title: "Hijacked",
      updatedAt: serverTimestamp(),
    })
  );
});

test("Trades: participant isolation, state transition enforcement, and terminal lock", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const carolDb = testEnv.authenticatedContext("carol_789").firestore();

  await assertSucceeds(
    aliceDb.collection("trades").doc("trade_1").set({
      initiatorId: ALICE_UID,
      initiatorName: "Alice",
      receiverId: BOB_UID,
      receiverName: "Bob",
      participantIds: [ALICE_UID, BOB_UID],
      offeredItemTitle: "Cedar Planter",
      requestedItemTitle: "Tomato Crate",
      termsAgreed: "Direct peer trade",
      status: "PROPOSED",
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );

  // Non-participant cannot read trade
  await assertFails(carolDb.collection("trades").doc("trade_1").get());

  // Cannot jump directly from PROPOSED to REVIEWED
  await assertFails(
    bobDb.collection("trades").doc("trade_1").update({
      status: "REVIEWED",
      ratingScore: 5,
      reviewComment: "Skipped steps",
      updatedAt: serverTimestamp(),
    })
  );

  // Valid state transitions: PROPOSED -> ACCEPTED -> COMPLETED -> REVIEWED
  await assertSucceeds(
    bobDb.collection("trades").doc("trade_1").update({
      status: "ACCEPTED",
      updatedAt: serverTimestamp(),
    })
  );
  await assertSucceeds(
    aliceDb.collection("trades").doc("trade_1").update({
      status: "COMPLETED",
      updatedAt: serverTimestamp(),
    })
  );
  await assertSucceeds(
    aliceDb.collection("trades").doc("trade_1").update({
      status: "REVIEWED",
      ratingScore: 5,
      reviewComment: "Honest trade!",
      stewardshipBadge: "Harm-Free Earth Trade",
      updatedAt: serverTimestamp(),
    })
  );

  // Terminal state locking: REVIEWED trade cannot be modified again
  await assertFails(
    aliceDb.collection("trades").doc("trade_1").update({
      status: "COMPLETED",
      updatedAt: serverTimestamp(),
    })
  );
});
