const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const STUDENT_ALICE = "student_alice_1";
const STUDENT_BOB = "student_bob_2";
const STAFF_UID = "staff_bk_1";
const STAFF_EMAIL = "andipriyantoko3@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
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

function buildReportPayload(uid, overrides = {}) {
  const now = new Date(Date.now() - 2000);
  return {
    reporterId: uid,
    ticketCode: "LP-0428",
    studentCode: "SW-8B-014",
    studentClassCode: "8B",
    privacyCodeOnly: true,
    category: "VERBAL",
    recipientType: "GURU_BK",
    assignedTeacherName: "Tim BK (Siapa Saja yang Tersedia)",
    assignedTeacherRole: "GURU_BK",
    anonymous: true,
    incidentTimeLabel: "25 Nov 2026, 09:30",
    locationArea: "Kantin",
    chronology: "Saya mendapat ejekan berulang saat jam istirahat di kantin.",
    evidenceNames: ["screenshot_1.png"],
    status: "BARU",
    stageIndex: 0,
    aiPriority: "SEDANG",
    humanDecision: "MENUNGGU",
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

function buildSosPayload(uid, overrides = {}) {
  const now = new Date(Date.now() - 2000);
  return {
    reporterId: uid,
    studentCode: "SW-8B-014",
    studentClassCode: "8B",
    reporterName: "SW-8B-014",
    locationLabel: "Koridor Kelas 8B",
    latitude: -6.2088,
    longitude: 106.8456,
    hasGpsFix: true,
    status: "AKTIF",
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

function buildStudentMasterPayload(staffUid, overrides = {}) {
  const now = new Date(Date.now() - 2000);
  return {
    nisn: "0081234567",
    namaLengkap: "Budi Santoso",
    kelas: "8B",
    kodeSiswa: "SW-8B-014",
    pinAkses: "482910",
    claimedUid: "",
    createdByUid: staffUid,
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

test("Unauthenticated user cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("reports").get());
  await assertFails(unauthDb.collection("students").get());
  await assertFails(unauthDb.collection("student_access").get());
  await assertFails(
    unauthDb.collection("reports").doc("rep_unauth").set(buildReportPayload(STUDENT_ALICE))
  );
});

test("Master Students & Student Access: Guru BK creates, student cannot list students, secret access get works", async () => {
  const staffDb = testEnv
    .authenticatedContext(STAFF_UID, { email: STAFF_EMAIL, email_verified: true })
    .firestore();
  const aliceDb = testEnv.authenticatedContext(STUDENT_ALICE).firestore();
  const now = new Date(Date.now() - 2000);

  await assertSucceeds(
    staffDb.collection("students").doc("SW-8B-014").set(buildStudentMasterPayload(STAFF_UID))
  );
  await assertSucceeds(
    staffDb.collection("student_access").doc("nisn_0081234567_482910").set({
      studentDocId: "SW-8B-014",
      kodeSiswa: "SW-8B-014",
      kelas: "8B",
      active: true,
      createdByUid: STAFF_UID,
      createdAt: now,
    })
  );

  // Student cannot enumerate student_access or read unclaimed students
  await assertFails(aliceDb.collection("student_access").get());
  await assertFails(aliceDb.collection("students").doc("SW-8B-014").get());

  // Student with exact NISN + PIN can lookup access index and claim own student doc
  await assertSucceeds(
    aliceDb.collection("student_access").doc("nisn_0081234567_482910").get()
  );
  await assertSucceeds(
    aliceDb.collection("students").doc("SW-8B-014").update({
      claimedUid: STUDENT_ALICE,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Another student cannot hijack claimed student doc
  const bobDb = testEnv.authenticatedContext(STUDENT_BOB).firestore();
  await assertFails(
    bobDb.collection("students").doc("SW-8B-014").update({
      claimedUid: STUDENT_BOB,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Reports & Emergency Identity Reveal: student submits with kodeSiswa, Guru BK opens real identity with audit log", async () => {
  const aliceDb = testEnv.authenticatedContext(STUDENT_ALICE).firestore();
  const staffDb = testEnv
    .authenticatedContext(STAFF_UID, { email: STAFF_EMAIL, email_verified: true })
    .firestore();

  await assertSucceeds(
    aliceDb.collection("reports").doc("rep_001").set(buildReportPayload(STUDENT_ALICE))
  );

  // Student cannot write emergency identity audit log
  await assertFails(
    aliceDb.collection("emergency_identity_logs").doc("log_1").set({
      reportId: "rep_001",
      ticketCode: "LP-0428",
      studentCode: "SW-8B-014",
      reason: "Unauthorized test",
      openedByUid: STUDENT_ALICE,
      openedByName: "Alice",
      createdAt: new Date(Date.now() - 1000),
    })
  );

  // Guru BK can update report with revealed identity and record immutable audit log
  await assertSucceeds(
    staffDb.collection("reports").doc("rep_001").update({
      emergencyRevealedName: "Budi Santoso (8B)",
      emergencyRevealReason: "Siswa melaporkan ancaman fisik mendesak",
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertSucceeds(
    staffDb.collection("emergency_identity_logs").doc("log_1").set({
      reportId: "rep_001",
      ticketCode: "LP-0428",
      studentCode: "SW-8B-014",
      reason: "Siswa melaporkan ancaman fisik mendesak",
      openedByUid: STAFF_UID,
      openedByName: "Bu Rani, S.Pd.",
      createdAt: new Date(Date.now() - 500),
    })
  );
});

test("SOS alerts: student attaches kodeSiswa and Guru BK updates status", async () => {
  const aliceDb = testEnv.authenticatedContext(STUDENT_ALICE).firestore();
  const staffDb = testEnv
    .authenticatedContext(STAFF_UID, { email: STAFF_EMAIL, email_verified: true })
    .firestore();

  await assertSucceeds(
    aliceDb.collection("sos_alerts").doc("sos_001").set(buildSosPayload(STUDENT_ALICE))
  );
  await assertSucceeds(
    staffDb.collection("sos_alerts").doc("sos_001").update({
      status: "DITANGANI",
      handledBy: "Bu Rani, S.Pd.",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Reports recipient selection: Guru Lainnya & LAINNYA category succeed, invalid recipientType fails", async () => {
  const aliceDb = testEnv.authenticatedContext(STUDENT_ALICE).firestore();

  await assertSucceeds(
    aliceDb.collection("reports").doc("rep_guru_lain").set(
      buildReportPayload(STUDENT_ALICE, {
        category: "LAINNYA",
        recipientType: "GURU_LAINNYA",
        assignedTeacherName: "Pak Budi, S.Pd. (Wali Kelas 8B)",
        assignedTeacherRole: "WALI_KELAS",
      })
    )
  );

  await assertFails(
    aliceDb.collection("reports").doc("rep_invalid_recipient").set(
      buildReportPayload(STUDENT_ALICE, {
        recipientType: "ADMIN_LAIN",
      })
    )
  );
});
